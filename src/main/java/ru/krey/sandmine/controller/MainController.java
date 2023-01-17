package ru.krey.sandmine.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.krey.sandmine.domain.Zone;
import ru.krey.sandmine.repository.ZoneRepository;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/")
public class MainController {

    private final ZoneRepository zoneRepository;

    public MainController(ZoneRepository zoneRepository) {
        this.zoneRepository = zoneRepository;
    }

    @GetMapping
    public String main(Model model) {
        List<Zone> zones = zoneRepository.findAll();
        model.addAttribute("zones", zones);
        return "index";
    }

    @GetMapping("/*")
    String allRequests() {
        return "forward:/";
    }
}
