package ru.krey.sandmine.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllShiftsDto {
    private Long workerId;
    @NonNull private String surname;
    @NonNull private String name;
    @NonNull private String patronymic;
    @NonNull private String email;
    @NonNull private String phoneNumber;
    @NonNull private String role;
    @NonNull private String date;
    @NonNull private Long ZoneId;
}
