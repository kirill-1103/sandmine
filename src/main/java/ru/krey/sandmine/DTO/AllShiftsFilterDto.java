package ru.krey.sandmine.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllShiftsFilterDto {
    private String surname;

    private String name;

    private String patronymic;

    private String phone;

    private String role;

    private List<Long> zoneIds;

    private String dateFrom;

    private String dateTo;
}
