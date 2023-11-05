package com.argus.controller;

import com.argus.service.UserActivationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@RequestMapping("/user")
@RestController
public class ActivationController {
    private final UserActivationService userActivationService;

    public ActivationController(UserActivationService userActivationService) {
        this.userActivationService = userActivationService;
    }
    @GetMapping("/activation")
    public ResponseEntity<?> activation(@RequestParam("id") String id){
        var res = userActivationService.activation(id);
        if (res) {
            return ResponseEntity.ok().body("Registered successfully");
        }
        //TODO: edge cases!
        return ResponseEntity.internalServerError().build();
    }
}
