package ru.krey.sandmine.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MineStatsDto {
    private Long id;

    private Long zoneId;

    private Long editorId;

    @NonNull private String date;

    @NonNull private Double weight;

    @NonNull private String lastEditTime;
}
