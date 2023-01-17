package ru.krey.sandmine.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ru.krey.sandmine.domain.Worker;
import ru.krey.sandmine.domain.Zone;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkerDto {
    private Long workerId;

    @NonNull private String surname;

    @NonNull private String name;

    @NonNull private String patronymic;

    @NonNull private String email;

    @NonNull private String phoneNumber;

    @NonNull private String passport;

    @NonNull private String role;

    @NonNull private Long passId;

    private String password = null;

    @NonNull private List<Long> zonesWithAccess = new ArrayList<>();

    public WorkerDto(Worker worker){
        this.workerId = worker.getId();
        this.email = worker.getEmail();
        this.surname = worker.getSurname();
        this.name = worker.getName();
        this.patronymic = worker.getPatronymic();
        this.phoneNumber = worker.getPhoneNumber();
        this.passId = worker.getPassId();
        this.passport = worker.getPassport();
        this.role = worker.getRole();
        this.password = worker.getPassword();
        this.zonesWithAccess = worker.getZonesWithAccess().stream().map(Zone::getId).collect(Collectors.toList());
    }
}
