package ru.krey.sandmine.controller;

import lombok.RequiredArgsConstructor;
import org.neo4j.driver.internal.value.DateValue;
import org.springframework.web.bind.annotation.*;
import ru.krey.sandmine.DTO.*;
import ru.krey.sandmine.domain.Shift;
import ru.krey.sandmine.domain.Worker;
import ru.krey.sandmine.domain.Zone;
import ru.krey.sandmine.exceptions.NotFoundException;
import ru.krey.sandmine.repository.ShiftRepository;
import ru.krey.sandmine.repository.WorkerRepository;
import ru.krey.sandmine.repository.ZoneRepository;

import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/worker")
@RequiredArgsConstructor
public class WorkerController {
    private final WorkerRepository workerRepository;
    private final ZoneRepository zoneRepository;
    private final ShiftRepository shiftRepository;

    @GetMapping("/get_shifts/{workerId}")
    List<ShiftDto> getWorkerShifts(@PathVariable Long workerId) {
        Worker worker = workerRepository.findById(workerId).orElseThrow(NotFoundException::new);

        return shiftRepository.getAllShiftsByWorker(workerId)
                .stream()
                .map(shift -> new ShiftDto(
                        shift.getId(),
                        shift.getDate().asLocalDate().toString(),
                        shift.getAttended(),
                        shift.getId() == null ? -1 : shift.getId()
                ))
                .collect(Collectors.toList());
    }

    @GetMapping("/all")
    Set<WorkerDto> getWorkerAll() {
        return workerRepository
                .findAll()
                .stream()
                .filter(Objects::nonNull)
                .map(WorkerDto::new)
                .collect(Collectors.toSet());
    }

    @GetMapping("/{workerId}")
    WorkerDto getWorker(@PathVariable Long workerId) {
        Worker worker = workerRepository.findById(workerId).orElseThrow(NotFoundException::new);
        return new WorkerDto(worker);
    }

    @GetMapping("/roles")
    Set<String> getRolesAll() {
        return workerRepository.findAllRoles();
    }

    @PostMapping("/check")
    CheckWorkerResponseDto checkWorker(@RequestBody CheckWorkerRequestDto params) {
        Worker worker = workerRepository.findByPassId(params.getPassId()).orElseThrow(NotFoundException::new);

        Worker warden = workerRepository.findById(params.getWardenId()).orElseThrow(NotFoundException::new);

        Shift wardenCurrentShift = warden.getShifts()
                .stream()
                .filter(shift -> shift.getDate().asLocalDate().equals(LocalDate.now()))
                .findFirst()
                .orElseThrow(NotFoundException::new);

        if (worker.getZonesWithAccess().contains(wardenCurrentShift.getZone())) {
            worker.getShifts()
                    .stream()
                    .filter(shift_ -> shift_.getDate().asLocalDate().equals(LocalDate.now()))
                    .findFirst()
                    .ifPresent(shift -> {
                        Shift newShift = new Shift(shift.getId(), shift.getDate(), true, shift.getZone());
                        shiftRepository.save(newShift);
                    });
        }
        return new CheckWorkerResponseDto(
                worker.getSurname(),
                worker.getName(),
                worker.getPatronymic(),
                worker.getEmail(),
                worker.getPhoneNumber(),
                worker.getRole(),
                worker.getPassport(),
                worker.getZonesWithAccess().stream().map(Zone::getId).collect(Collectors.toList()),
                worker.getZonesWithAccess().contains(wardenCurrentShift.getZone())
        );
    }

    @PostMapping("/edit")
    WorkerDto editWorker(@RequestBody WorkerDto workerDto) {
        if (workerDto == null) {
            throw new NotFoundException();
        }
        Worker oldWorker = workerRepository.findById(workerDto.getWorkerId()).orElseThrow(NotFoundException::new);

        Set<Zone> zonesWithAccess = workerDto
                .getZonesWithAccess()
                .stream()
                .filter(Objects::nonNull)
                .map(zoneId -> zoneRepository.findById(zoneId).orElse(null))
                .collect(Collectors.toSet());

        return new WorkerDto(
                workerRepository.save(
                        new Worker(
                                oldWorker.getId(),
                                workerDto.getSurname(),
                                workerDto.getName(),
                                workerDto.getPatronymic(),
                                workerDto.getEmail(),
                                workerDto.getPhoneNumber(),
                                workerDto.getPassport(),
                                workerDto.getRole(),
                                workerDto.getPassId(),
                                oldWorker.getPassword(),
                                zonesWithAccess,
                                oldWorker.getShifts()
                        )
                )
        );
    }

    @PostMapping("/new")
    WorkerDto addWorker(@RequestBody WorkerDto workerDto) {
        Set<Zone> zonesWithAccess = workerDto.getZonesWithAccess()
                .stream().filter(Objects::nonNull)
                .map(zoneId -> zoneRepository.findById(zoneId).orElse(null))
                .collect(Collectors.toSet());

        Set<Shift> shifts = generateShifts(zonesWithAccess);

        return new WorkerDto(
                workerRepository.save(
                        new Worker(
                                null,
                                workerDto.getSurname(),
                                workerDto.getName(),
                                workerDto.getPatronymic(),
                                workerDto.getEmail(),
                                workerDto.getPhoneNumber(),
                                workerDto.getPassport(),
                                workerDto.getRole(),
                                workerDto.getPassId(),
                                randomPassword(),
                                zonesWithAccess,
                                shifts
                        )
                )
        );
    }

    @PostMapping("/ids")
    List<WorkerDto> workersById(@RequestBody List<Long> workersIds) {
        return workerRepository.findAllByIdIn(workersIds).stream().map(WorkerDto::new).collect(Collectors.toList());
    }

    @PostMapping("/filter")
    Set<WorkerDto> getFilteredWorkers(@RequestBody WorkerFilterDto workerFilterDto) {
        Boolean needFiltering = false;
        String surname = "(?i).*";
        String name = "(?i).*";
        String patronymic = "(?i).*";
        String phoneNumber = "\\+?\\d*";
        String roles = ".*";
        String zoneIds = ".*";

        Boolean searchWithZones = false;

        if (workerFilterDto.getSurname() != null) {
            surname = "(?i)" + ".*" + workerFilterDto.getSurname() + ".*";
            needFiltering = true;
        }

        if (workerFilterDto.getName() != null) {
            name = "(?i)" + ".*" + workerFilterDto.getName() + ".*";
            needFiltering = true;
        }
        if (workerFilterDto.getPatronymic() != null) {
            patronymic = "(?i)" + ".*" + workerFilterDto.getPatronymic() + ".*";
            needFiltering = true;
        }

        if (workerFilterDto.getPhoneNumber() != null) {
            phoneNumber = "\\+?\\d*" + workerFilterDto.getPhoneNumber().replace("+", "").replace(" ", "") + "\\d*";
            needFiltering = true;
        }

        if (workerFilterDto.getRoles() != null) {
            roles = String.join("|", workerFilterDto.getRoles());
            needFiltering = true;
        }

        if (workerFilterDto.getZoneIds() != null) {
            if (!zoneIds.isBlank()) {
                zoneIds = workerFilterDto.getZoneIds().stream().map(Object::toString).collect(Collectors.joining("|"));
                searchWithZones = true;
            }
            needFiltering = true;
        }

        Set<Worker> workersSet;
        if (!needFiltering) {
            workersSet = workerRepository.findAll().stream().filter(Objects::nonNull).collect(Collectors.toSet());
        } else if (searchWithZones) {
            workersSet = new HashSet<>(workerRepository.getFilteredWorkersList(surname, name, patronymic, phoneNumber, roles, zoneIds));
        } else {
            workersSet = new HashSet<>(workerRepository.getFilteredWorkersListWithoutZones(surname, name, patronymic, phoneNumber, roles));
        }

        return workersSet.stream().map(worker->new WorkerDto(
                worker.getId(),
                worker.getSurname(),
                worker.getName(),
                worker.getPatronymic(),
                worker.getEmail(),
                worker.getPhoneNumber(),
                worker.getPassport(),
                worker.getRole(),
                worker.getPassId(),
                worker.getPassword(),
                worker.getZonesWithAccess().stream().map(Zone::getId).collect(Collectors.toList())
        )).collect(Collectors.toSet());
    }

    @GetMapping("/admins")
    List<WorkerDto> getAdmins(){
        return workerRepository.findAdmins().stream().map(WorkerDto::new).collect(Collectors.toList());
    }

    @PostMapping("/phone")
    WorkerDto getWorkerByPhone(@RequestBody String phoneNumber){
        Worker worker = workerRepository.getWorkerByPhoneNumber(phoneNumber);
        if(worker == null){
            return null;
        }
        return new WorkerDto(
                worker.getId(),
                worker.getSurname(),
                worker.getName(),
                worker.getPatronymic(),
                worker.getEmail(),
                worker.getPhoneNumber(),
                worker.getPassport(),
                worker.getRole(),
                worker.getPassId(),
                worker.getPassword(),
                worker.getZonesWithAccess().stream().map(Zone::getId).collect(Collectors.toList())
        );
    }

    @PostMapping("/email")
    WorkerDto getWorkerByEmail(@RequestBody String email){
        String emailFormat = email.substring(1,email.length()-1);
        Worker worker = workerRepository.getWorkerByEmail(emailFormat);
        if(worker == null){
            return null;
        }
        return new WorkerDto(
                worker.getId(),
                worker.getSurname(),
                worker.getName(),
                worker.getPatronymic(),
                worker.getEmail(),
                worker.getPhoneNumber(),
                worker.getPassport(),
                worker.getRole(),
                worker.getPassId(),
                worker.getPassword(),
                worker.getZonesWithAccess().stream().map(Zone::getId).collect(Collectors.toList())
        );
    }

    @PostMapping("/remove")
    void removeWorker(@RequestBody Long workerId){
        workerRepository.deleteById(workerId);
    }
    private Set<Shift> generateShifts(Set<Zone> zones) {
        List<Shift> shifts = new ArrayList<>();

        for (long i = -365; i <= 365; i++) {
            LocalDate today = LocalDate.now().minusDays(i);
            if (today.getDayOfWeek() == DayOfWeek.SUNDAY || today.getDayOfWeek() == DayOfWeek.SATURDAY) {
                continue;
            }
            Boolean attended;
            if (i > 0) {
                attended = !(Math.random() > 0.8);
            } else {
                attended = false;
            }

            int zoneId = new Random().nextInt(zones.size());
            shifts.add(new Shift(
                    null,
                    new DateValue(today),
                    attended,
                    zones.stream().toList().get(zoneId)
            ));
        }
        return new HashSet<>(shifts);
    }

    private String randomPassword() {
        int length = 3;
        String password = "";
        for (int i = 0; i < length; i++) {
            password = password + Objects.toString(new Random().nextInt(9));
        }
        return password;
    }
}
