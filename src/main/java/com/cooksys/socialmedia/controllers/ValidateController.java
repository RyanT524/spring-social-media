package com.cooksys.socialmedia.controllers;

import com.cooksys.socialmedia.services.ValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/validate")
@RequiredArgsConstructor
public class ValidateController {

    private final ValidateService validateService;

    @GetMapping("/username/exists/@{username}")
    public boolean validateUsernameExists(@PathVariable("username") String username) {
        return validateService.validateUsernameExists(username);
    }

    @GetMapping("/tag/exists/{label}")
    public boolean validateHashtagExists(@PathVariable("label") String label) {
        return validateService.validateHashtagExists(label);
    }

    @GetMapping("username/available/@{username}")
    public boolean validateUsernameAvailable(@PathVariable("username") String username) {
        return validateService.validateUsernameAvailable(username);
    }

}
