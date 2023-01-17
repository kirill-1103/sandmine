package ru.krey.sandmine.controller;

import lombok.RequiredArgsConstructor;
import org.neo4j.driver.internal.value.DateTimeValue;
import org.neo4j.driver.internal.value.DateValue;
import org.neo4j.driver.internal.value.LocalDateTimeValue;
import org.springframework.web.bind.annotation.*;
import ru.krey.sandmine.DTO.MineStatsDto;
import ru.krey.sandmine.DTO.MineStatsFilterDto;
import ru.krey.sandmine.domain.MineStats;
import ru.krey.sandmine.domain.Worker;
import ru.krey.sandmine.domain.Zone;
import ru.krey.sandmine.exceptions.NotFoundException;
import ru.krey.sandmine.repository.MineStatsRepository;
import ru.krey.sandmine.repository.WorkerRepository;
import ru.krey.sandmine.repository.ZoneRepository;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/mine_stats")
@RequiredArgsConstructor
public class MineStatsController {
    private final MineStatsRepository mineStatsRepository;
    private final ZoneRepository zoneRepository;
    private final WorkerRepository workerRepository;

    @GetMapping("/all")
    List<MineStatsDto> getMineStatsAll(){
        List<MineStatsDto> mineStatsList = mineStatsRepository.findAllSortedByDate()
                .stream()
                .map(
                        mineStats -> {
                            Long editorId = mineStats.getLastEditedBy().getId();
                            if(editorId == null){
                                throw new NotFoundException();
                            }
                            return new MineStatsDto(
                                    mineStats.getId(),
                                    mineStats.getParentZone().getId(),
                                    editorId,
                                    mineStats.getDate().asLocalDate().toString(),
                                    mineStats.getWeight(),
                                    mineStats.getLastEditTime().asZonedDateTime().toLocalDateTime().toString()
                            );
                        }
                ).collect(Collectors.toList());
        return mineStatsList;
    }

    @GetMapping("/{id}")
    MineStatsDto getMineStats(@PathVariable Long id){
        MineStats mineStats = mineStatsRepository.findById(id).orElseThrow(NotFoundException::new);
        Long editorId = mineStats.getLastEditedBy().getId();
        if(editorId == null) {
            throw new NotFoundException();
        }
        return new MineStatsDto(
                mineStats.getId(),
                mineStats.getParentZone().getId(),
                editorId,
                mineStats.getDate().asLocalDate().toString(),
                mineStats.getWeight(),
                mineStats.getLastEditTime().asZonedDateTime().toLocalDateTime().toString()
        );
    }

    @PostMapping("/create")
    MineStatsDto createMineStats(@RequestBody MineStatsDto mineStatsDto){
        Zone zone = getZone(mineStatsDto);
        Worker editor = getWorker(mineStatsDto);

        MineStats mineStats = mineStatsRepository.save(
                new MineStats(
                       (new DateValue(LocalDate.parse(mineStatsDto.getDate()))),
                        mineStatsDto.getWeight(),
                        new DateTimeValue(ZonedDateTime.of(LocalDateTime.parse(mineStatsDto.getLastEditTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")), ZoneId.systemDefault())),
                        editor,
                        zone
                )
        );

        zoneRepository.save(zone);
        mineStatsDto.setId(mineStats.getId());
        return mineStatsDto;
    }

    @PostMapping("/edit")
    MineStatsDto editMineStats(@RequestBody MineStatsDto mineStatsDto){
        MineStats old = mineStatsRepository.findById(mineStatsDto.getId() == null ? -1 : mineStatsDto.getId()).orElseThrow(NotFoundException::new);

        Zone zone = getZone(mineStatsDto);

        Worker editor = getWorker(mineStatsDto);

        mineStatsRepository.save(
                new MineStats(
                        old.getId(),
                        new DateValue(LocalDate.parse(mineStatsDto.getDate())),
                        mineStatsDto.getWeight(),
                        new DateTimeValue(ZonedDateTime.of(LocalDateTime.parse(mineStatsDto.getLastEditTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")), ZoneId.systemDefault())),
                        editor,
                        zone
                )
        );
        return mineStatsDto;
    }

    @PostMapping("/filter")
    List<MineStatsDto> getFilterMineStats(@RequestBody MineStatsFilterDto mineStatsFilterDto){
        LocalDateTime timeEditStart = LocalDateTime.MIN;
        LocalDateTime timeEditEnd = LocalDateTime.MAX.minusDays(3);
        Long diff = 10800L;

        if(mineStatsFilterDto.getDateEdit() != null){
            timeEditStart = LocalDateTime.parse(mineStatsFilterDto.getDateEdit()).toLocalDate().atStartOfDay();
            timeEditEnd = LocalDateTime.parse(mineStatsFilterDto.getDateEdit()).toLocalDate().atTime(23,59,59);
        }

        Long secondsStart = timeEditStart.toEpochSecond(ZoneOffset.UTC) + diff;
        Long secondsEnd = timeEditEnd.toEpochSecond(ZoneOffset.UTC) + diff;

        List<MineStats> filteredMineStats = mineStatsRepository.getFilteredMineStats(
                secondsStart,
                secondsEnd,
                mineStatsFilterDto.getDateFrom() == null ? LocalDate.of(1,1,1).toString() : LocalDate.parse(mineStatsFilterDto.getDateFrom()).toString(),
                mineStatsFilterDto.getDateTo() == null? LocalDate.of(9999,12,31).toString() :LocalDate.parse(mineStatsFilterDto.getDateTo()).toString(),
                mineStatsFilterDto.getLastEditorsId() == null ? new ArrayList<Long>() : mineStatsFilterDto.getLastEditorsId(),
                mineStatsFilterDto.getWeightFrom() == null ? Double.MIN_VALUE : mineStatsFilterDto.getWeightFrom(),
                mineStatsFilterDto.getWeightTo() == null ? Double.MAX_VALUE : mineStatsFilterDto.getWeightTo(),
                mineStatsFilterDto.getZoneIds() == null ? new ArrayList<Long>() : mineStatsFilterDto.getZoneIds()
        );

        return filteredMineStats.stream().map(mineStats ->
            new MineStatsDto(
                    mineStats.getId(),
                    mineStats.getParentZone().getId(),
                    mineStats.getLastEditedBy().getId(),
                    mineStats.getDate().asLocalDate().toString(),
                    mineStats.getWeight(),
                    mineStats.getLastEditTime().asZonedDateTime().toLocalDateTime().toString()
            )
        ).collect(Collectors.toList());
    }

    Zone getZone(MineStatsDto mineStatsDto){
        return zoneRepository.findById(mineStatsDto.getZoneId()).orElseThrow(NotFoundException::new);
    }

    Worker getWorker(MineStatsDto mineStatsDto){
        return workerRepository.findById(mineStatsDto.getEditorId()).orElseThrow(NotFoundException::new);
    }
}
