package ru.krey.sandmine.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MineStatsFilterDto {
    private String dateEdit;

    private String dateFrom;

    private String dateTo;

    private List<Long> lastEditorsId;

    private Double weightFrom;

    private Double weightTo;

    private List<Long> zoneIds;
}
