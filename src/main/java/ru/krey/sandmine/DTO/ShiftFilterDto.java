package ru.krey.sandmine.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShiftFilterDto {
    private Long workerId;

    private String dateFrom;

    private String dateTo;

    private Boolean attended;

    private List<Long> zoneIds;
}
