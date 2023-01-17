package ru.krey.sandmine.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class CheckWorkerResponseDto {
    @NonNull private String surname;

    @NonNull private String name;

    @NonNull private String patronymic;

    @NonNull private String email;

    @NonNull private String phoneNumber;

    @NonNull private String role;

    @NonNull private String passport;

    @NonNull private List<Long> zonesWithAccess = new ArrayList<>();

    @NonNull private Boolean allowed;
}
