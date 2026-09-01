package com.phamquocviet.registrationservice.controller;

import com.phamquocviet.registrationservice.dto.RegistrationRequest;
import com.phamquocviet.registrationservice.entity.Registration;
import com.phamquocviet.registrationservice.service.RegistrationService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/registrations")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(
            RegistrationService registrationService
    ) {
        this.registrationService =
                registrationService;
    }

    @PostMapping
    public ResponseEntity<Registration> register(
            @Valid
            @RequestBody RegistrationRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        registrationService.register(
                                request
                        )
                );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Registration> getById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                registrationService.getById(id)
        );
    }

    @GetMapping("/my")
    public ResponseEntity<List<Registration>> getMyRegistrations(
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(registrationService.getByStudentId(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Registration> cancel(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                registrationService.cancel(id)
        );
    }
}
