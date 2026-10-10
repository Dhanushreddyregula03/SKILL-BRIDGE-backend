package com.skillbridge.controller;

import com.skillbridge.dto.RecruiterProfileDto;
import com.skillbridge.dto.StudentProfileDto;
import com.skillbridge.model.User;
import com.skillbridge.service.AuthService;
import com.skillbridge.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final AuthService authService;

    public ProfileController(ProfileService profileService, AuthService authService) {
        this.profileService = profileService;
        this.authService = authService;
    }

    @GetMapping("/student")
    public ResponseEntity<StudentProfileDto> getStudentProfile() {
        User user = authService.getCurrentAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(profileService.getStudentProfileByUserId(user.getId()));
    }

    @PutMapping("/student")
    public ResponseEntity<StudentProfileDto> updateStudentProfile(@RequestBody StudentProfileDto dto) {
        User user = authService.getCurrentAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(profileService.updateStudentProfile(user.getId(), dto));
    }

    @GetMapping("/recruiter")
    public ResponseEntity<RecruiterProfileDto> getRecruiterProfile() {
        User user = authService.getCurrentAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(profileService.getRecruiterProfileByUserId(user.getId()));
    }

    @PutMapping("/recruiter")
    public ResponseEntity<RecruiterProfileDto> updateRecruiterProfile(@RequestBody RecruiterProfileDto dto) {
        User user = authService.getCurrentAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(profileService.updateRecruiterProfile(user.getId(), dto));
    }
}
