package ru.krey.sandmine.DTO;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkerWithShiftsDto {
    private Long workerId;

    @NonNull private String surname;

    @NonNull private String name;

    @NonNull private String patronymic;

    @NonNull private String email;

    @NonNull private String phoneNumber;

    @NonNull private String role;

    @NonNull private List<Shift> shifts ;

    @AllArgsConstructor
    @Getter
    @Setter
    public static class Shift{
        String date;
        Long zoneId;
        Boolean attended;
    }
}
