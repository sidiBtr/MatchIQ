package com.matchiq.matchiq.controller;

import com.matchiq.matchiq.service.FootballImportService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class FootballImportController {

    private final FootballImportService footballImportService;

    public FootballImportController(
            FootballImportService footballImportService
    ) {
        this.footballImportService = footballImportService;
    }

    @PostMapping("/import-fixtures")
    public String importFixtures() {

        int count = footballImportService.importFixtures(39, 2024);

        return "Successfully processed " + count + " fixtures";
    }
}