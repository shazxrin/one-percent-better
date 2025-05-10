package io.github.shazxrin.onepercentbetter.controller;

import io.github.shazxrin.onepercentbetter.service.checkin.CheckInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/check-ins")
@RestController
public class CheckInController {
    private final CheckInService checkInService;

    @Autowired
    public CheckInController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @PostMapping("/today")
    public void checkInToday() {
        checkInService.checkInToday();
    }
}
