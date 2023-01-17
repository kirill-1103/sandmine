package ru.krey.sandmine.controller;

import lombok.RequiredArgsConstructor;
import org.neo4j.driver.internal.value.DateValue;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.ArrayUtils;
import ru.krey.sandmine.DTO.*;
import ru.krey.sandmine.domain.Shift;
import ru.krey.sandmine.domain.Worker;
import ru.krey.sandmine.domain.Zone;
import ru.krey.sandmine.exceptions.AlreadyExistsException;
import ru.krey.sandmine.exceptions.NotFoundException;
import ru.krey.sandmine.repository.ShiftRepository;
import ru.krey.sandmine.repository.WorkerRepository;
import ru.krey.sandmine.repository.ZoneRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/shifts")
@RequiredArgsConstructor
public class ShiftController {
    private final ShiftRepository shiftRepository;
    private final ZoneRepository zoneRepository;
    private final WorkerRepository workerRepository;

    @PostMapping("/create")
    ShiftDto createShift(@RequestBody ShiftDto shiftDto) {
        if (shiftDto.getShiftId() != null) {
            shiftRepository.findById(shiftDto.getShiftId()).ifPresent((it) -> {
                throw new AlreadyExistsException();
            });
        }
        Zone zone = zoneRepository.findById(shiftDto.getZoneId()).orElseThrow(NotFoundException::new);
        Shift shift = new Shift(shiftDto.getShiftId(), new DateValue(LocalDate.parse(shiftDto.getDate())), shiftDto.getAttended(), zone);
        shiftRepository.save(shift);
        return new ShiftDto(shiftRepository.save(shift));
    }

    @GetMapping("/{workerId}")
    WorkerWithShiftsDto getWorkerShifts(@PathVariable Long workerId) {
        Worker worker = workerRepository.findById(workerId).orElseThrow(NotFoundException::new);

        Set<Shift> sortedShifts = shiftRepository.getAllShiftsByWorker(workerId);

        return new WorkerWithShiftsDto(
                worker.getId(),
                worker.getSurname(),
                worker.getName(),
                worker.getPatronymic(),
                worker.getEmail(),
                worker.getPhoneNumber(),
                worker.getRole(),
                sortedShifts.stream().map(shift ->
                        new WorkerWithShiftsDto.Shift(
                                shift.getDate().asLocalDate().toString(),
                                shift.getZone().getId() == null ? -1 : shift.getZone().getId(),
                                shift.getAttended()
                        )
                ).collect(Collectors.toList())
        );
    }

    @GetMapping("/all")
    List<AllShiftsDto> getAllShifts() {
        List<AllShiftsDto> result = new ArrayList<>();
        workerRepository.findAll().forEach(worker -> {
            result.addAll(worker.getShifts().stream().map(shift -> {
                        return new AllShiftsDto(
                                worker.getId(),
                                worker.getSurname(),
                                worker.getName(),
                                worker.getPatronymic(),
                                worker.getEmail(),
                                worker.getPhoneNumber(),
                                worker.getRole(),
                                shift.getDate().asLocalDate().toString(),
                                shift.getZone().getId() == null ? -1 : shift.getZone().getId()
                        );
                    }).sorted(Comparator.comparing(AllShiftsDto::getDate)).toList()
            );
        });
        return result;
    }

    @PostMapping("/all/filter")
    List<AllShiftsDto> getAllShiftsFiltered(@RequestBody AllShiftsFilterDto filters) {
        LocalDate dateFrom = filters.getDateFrom() == null ? LocalDate.of(1,1,1) : LocalDate.parse(filters.getDateFrom());
        LocalDate dateTo = filters.getDateTo() == null ? LocalDate.of(9999,12,31) : LocalDate.parse(filters.getDateTo());
        String phoneRegular = filters.getPhone() == null ? ".*" : filters.getPhone().replace("+", "\\+?").replace(" ", "");
        String roleRegular = filters.getRole() == null ? ".*" : filters.getRole();
        String nameRegular = filters.getName() == null ? "" : filters.getName();
        String surnameRegular = filters.getSurname() == null ? "" : filters.getSurname();
        String patronymicRegular = filters.getPatronymic() == null ? "" : filters.getPatronymic();

        String zoneIdsRegular = filters.getZoneIds() == null
                ? ".*"
                : filters.getZoneIds().stream()
                .map(Object::toString)
                .collect(Collectors.joining("|"));

        List<AllShiftsDto> result = new ArrayList<AllShiftsDto>();

        List<Shift> shifts = shiftRepository.allShiftsFilter(
                "(?i).*}" + nameRegular + ".*",
                "(?i).*" + surnameRegular + ".*",
                "(?i).*" + patronymicRegular + ".*",
                "(?i)[0-9]*" + phoneRegular + "[0-9]*",
                "(?i)" + roleRegular,
                dateFrom.toString(),
                dateTo.toString(),
                "(?i)" + zoneIdsRegular
        );

        shifts.forEach(shift -> {
            Worker worker = workerRepository.getWorkerByShift(shift.getId());
            result.add(
                    new AllShiftsDto(
                            worker.getId(),
                            worker.getSurname(),
                            worker.getName(),
                            worker.getPatronymic(),
                            worker.getEmail(),
                            worker.getPhoneNumber(),
                            worker.getRole(),
                            shift.getDate().asLocalDate().toString(),
                            shift.getZone().getId() == null ? -1 : shift.getZone().getId()
                    )
            );
        });

        return result;
    }

    @PostMapping("/filter")
    Set<ShiftDto> getFilteredShifts(@RequestBody ShiftFilterDto shiftFilterDto) {
        Boolean needFiltering = false;
        LocalDate dateFrom = LocalDate.of(1,1,1);
        LocalDate dateTo = LocalDate.of(9999,12,31);
        String attended = "(?i).*";
        String zoneIds = "(?i).*";

        if (shiftFilterDto.getDateFrom() != null) {
            dateFrom = LocalDate.parse(shiftFilterDto.getDateFrom());
            needFiltering = true;
        }
        if(shiftFilterDto.getDateTo() !=null){
            dateTo = LocalDate.parse(shiftFilterDto.getDateTo());
            needFiltering = true;
        }
        if(shiftFilterDto.getAttended() != null){
            attended = "(?i)" + shiftFilterDto.getAttended().toString();
            needFiltering = true;
        }
        if(shiftFilterDto.getZoneIds()!=null){
            zoneIds = "(?i)"+shiftFilterDto.getZoneIds().stream()
                    .map(Object::toString)
                    .collect(Collectors.joining("|"));
            needFiltering = true;
        }
        Set<Shift> shiftSet;
        if(!needFiltering){
            shiftSet = shiftRepository.getAllShiftsByWorker(shiftFilterDto.getWorkerId())
                    .stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        }else{
            shiftSet = shiftRepository.getFilteredShiftList(shiftFilterDto.getWorkerId(), dateFrom.toString(), dateTo.toString(), attended, zoneIds);
        }

        return shiftSet.stream().map(shift->
                new ShiftDto(
                    shift.getId(),
                    shift.getDate().asLocalDate().toString(),
                    shift.getAttended(),
                    shift.getZone().getId() == null ? -1 :  shift.getZone().getId()
        )).collect(Collectors.toSet());
    }
}
