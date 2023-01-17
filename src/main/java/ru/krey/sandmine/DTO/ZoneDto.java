package ru.krey.sandmine.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ru.krey.sandmine.domain.Zone;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZoneDto {
    private Long zoneId;

    @NonNull private String name;

    public ZoneDto(Zone zone){
        this.zoneId = zone.getId();
        this.name = zone.getName();
    }
}
