package com.skillbridge.controller;

import com.skillbridge.dto.ApplicationRequest;
import com.skillbridge.dto.ApplicationResponseDto;
import com.skillbridge.model.ApplicationStatus;
import com.skillbridge.model.User;
import com.skillbridge.service.ApplicationService;
import com.skillbridge.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final AuthService authService;

    public ApplicationController(ApplicationService applicationService, AuthService authService) {
        this.applicationService = applicationService;
        this.authService = authService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<ApplicationResponseDto> apply(@Valid @RequestBody ApplicationRequest request) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(applicationService.apply(request, currentUser));
    }

    @GetMapping("/student")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<List<ApplicationResponseDto>> getStudentApplications() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(applicationService.getStudentApplications(currentUser));
    }

    @GetMapping("/recruiter")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYER')")
    public ResponseEntity<List<ApplicationResponseDto>> getEmployerApplications() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(applicationService.getEmployerApplications(currentUser));
    }

    @GetMapping("/internship/{internshipId}")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYER')")
    public ResponseEntity<List<ApplicationResponseDto>> getApplicationsForInternship(@PathVariable Long internshipId) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(applicationService.getApplicationsForInternship(internshipId, currentUser));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYER')")
    public ResponseEntity<ApplicationResponseDto> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }

        String statusStr = body.get("status");
        String notes = body.get("recruiterNotes");

        ApplicationStatus status = ApplicationStatus.valueOf(statusStr);
        return ResponseEntity.ok(applicationService.updateStatus(id, status, notes, currentUser));
    }
}
