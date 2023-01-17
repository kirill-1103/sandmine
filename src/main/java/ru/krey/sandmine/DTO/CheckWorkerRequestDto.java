package ru.krey.sandmine.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckWorkerRequestDto {
    @NonNull private Long passId;

    @NonNull private Long wardenId;
}
