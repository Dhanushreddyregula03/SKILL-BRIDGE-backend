package com.skillbridge.controller;

import com.skillbridge.dto.InternshipDto;
import com.skillbridge.model.User;
import com.skillbridge.model.WorkMode;
import com.skillbridge.service.AuthService;
import com.skillbridge.service.InternshipService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/internships")
public class InternshipController {

    private final InternshipService internshipService;
    private final AuthService authService;

    public InternshipController(InternshipService internshipService, AuthService authService) {
        this.internshipService = internshipService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<List<InternshipDto>> getAllInternships(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) WorkMode workMode) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(internshipService.getAllInternships(search, category, workMode, currentUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InternshipDto> getInternshipById(@PathVariable Long id) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(internshipService.getInternshipById(id, currentUser));
    }

    @GetMapping("/my-postings")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYER')")
    public ResponseEntity<List<InternshipDto>> getMyInternships() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(internshipService.getEmployerInternships(currentUser));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_EMPLOYER')")
    public ResponseEntity<InternshipDto> createInternship(@Valid @RequestBody InternshipDto dto) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(internshipService.createInternship(dto, currentUser));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYER')")
    public ResponseEntity<InternshipDto> updateInternship(@PathVariable Long id, @Valid @RequestBody InternshipDto dto) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(internshipService.updateInternship(id, dto, currentUser));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYER')")
    public ResponseEntity<?> deleteInternship(@PathVariable Long id) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        internshipService.deleteInternship(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
