package ru.krey.sandmine.DTO;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {
    @NonNull private Long workerId;

    @NonNull private String workerType;

    @NonNull private String workerFullName;
}
