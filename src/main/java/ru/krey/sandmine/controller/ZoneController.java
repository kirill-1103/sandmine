package ru.krey.sandmine.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.krey.sandmine.DTO.ZoneDto;
import ru.krey.sandmine.domain.Zone;
import ru.krey.sandmine.exceptions.NotFoundException;
import ru.krey.sandmine.repository.ZoneRepository;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/zone")
@RequiredArgsConstructor
public class ZoneController {
    private final ZoneRepository zoneRepository;

    @GetMapping("/all")
    Set<ZoneDto> getAllZones(){
        return zoneRepository.findAll()
                .stream()
                .filter(Objects::nonNull)
                .map(zone -> new ZoneDto(
                        zone.getId() == null ? -1 : zone.getId(),
                        zone.getName()
                ))
                .collect(Collectors.toSet());

    }

    @GetMapping("/{zoneId}")
    ZoneDto getZone(@PathVariable Long zoneId){
        Zone zone = zoneRepository
                .findById(zoneId).orElseThrow(NotFoundException::new);
        return new ZoneDto(zone);
    }

    @PostMapping("/ids")
    List<ZoneDto> getZonesByIds(@RequestBody List<Long> zonesIds){
        return zoneRepository.findAllById(zonesIds)
                .stream()
                .map(ZoneDto::new)
                .collect(Collectors.toList());
    }
}

