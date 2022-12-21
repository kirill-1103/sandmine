package ru.krey.sandmine.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.krey.sandmine.repository.ZoneRepository;

@Controller
@RequestMapping("/")
public class MainController {

    private ZoneRepository zoneRepository;

    public MainController(ZoneRepository zoneRepository){
        this.zoneRepository = zoneRepository;
    }

    @GetMapping
    public String main(Model model){
        return "index";
    }
}
