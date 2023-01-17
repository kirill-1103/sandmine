package ru.krey.sandmine.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.krey.sandmine.DTO.AuthDto;
import ru.krey.sandmine.DTO.AuthResponseDto;
import ru.krey.sandmine.domain.Worker;
import ru.krey.sandmine.exceptions.NotFoundException;
import ru.krey.sandmine.repository.WorkerRepository;

import java.util.Locale;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {
    private final WorkerRepository workerRepository;

    Worker currentUser = null;

    @PostMapping
    AuthResponseDto login(@RequestBody AuthDto authDto) {
        Worker worker = workerRepository.findByLogin(authDto.getLogin().toLowerCase(Locale.getDefault()));
        if (worker == null) {
            throw new NotFoundException();
        }

        Long workerId = worker.getId();
        if (workerId == null) {
            throw new NotFoundException();
        }

        if (!worker.getPassword().equals(authDto.getPassword())) {
            throw new NotFoundException();
        }

        AuthResponseDto resultDto = new AuthResponseDto(workerId,
                worker.getRole(),
                worker.getSurname() + " " + worker.getName().subSequence(0, 1) + ". " + worker.getPatronymic().subSequence(0, 1) + ".");

        this.currentUser = worker;
        return resultDto;
    }

}
