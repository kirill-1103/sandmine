package ru.krey.sandmine.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkerFilterDto {
    private String surname;
    private String name;
    private String patronymic;
    private String phoneNumber;
    private List<String> roles;
    private List<Long> zoneIds;
}
