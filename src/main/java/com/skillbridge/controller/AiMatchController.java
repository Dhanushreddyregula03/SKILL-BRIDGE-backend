package com.skillbridge.controller;

import com.skillbridge.dto.SkillGapAnalysisDto;
import com.skillbridge.model.*;
import com.skillbridge.repository.InternshipRepository;
import com.skillbridge.repository.StudentProfileRepository;
import com.skillbridge.service.AuthService;
import com.skillbridge.service.SkillMatchingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiMatchController {

    private final SkillMatchingService skillMatchingService;
    private final AuthService authService;
    private final InternshipRepository internshipRepository;
    private final StudentProfileRepository studentProfileRepository;

    public AiMatchController(SkillMatchingService skillMatchingService,
                             AuthService authService,
                             InternshipRepository internshipRepository,
                             StudentProfileRepository studentProfileRepository) {
        this.skillMatchingService = skillMatchingService;
        this.authService = authService;
        this.internshipRepository = internshipRepository;
        this.studentProfileRepository = studentProfileRepository;
    }

    @GetMapping("/analyze-internship/{internshipId}")
    public ResponseEntity<SkillGapAnalysisDto> analyzeInternship(@PathVariable Long internshipId) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }

        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new RuntimeException("Internship not found with id: " + internshipId));

        StudentProfile profile = studentProfileRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        SkillGapAnalysisDto analysis = skillMatchingService.analyzeSkillGap(profile, internship);
        return ResponseEntity.ok(analysis);
    }

    @PostMapping("/analyze-gap/public")
    public ResponseEntity<SkillGapAnalysisDto> analyzeGapPublic(@RequestBody Map<String, Object> payload) {
        // Construct temporary student profile and internship for playground analysis
        String roleTitle = (String) payload.getOrDefault("roleTitle", "Full-Stack Developer");
        String company = (String) payload.getOrDefault("companyName", "Target Company");
        List<String> userSkillNames = (List<String>) payload.getOrDefault("userSkills", List.of());
        List<String> requiredSkillNames = (List<String>) payload.getOrDefault("requiredSkills", List.of());
        String resumeText = (String) payload.getOrDefault("resumeText", "");

        StudentProfile profile = new StudentProfile();
        profile.setHeadline("Playground Student Profile");
        profile.setResumeText(resumeText);
        for (String sName : userSkillNames) {
            profile.addSkill(new StudentSkill(sName, ProficiencyLevel.INTERMEDIATE, 12));
        }

        Internship mockInternship = new Internship();
        mockInternship.setId(0L);
        mockInternship.setTitle(roleTitle);
        mockInternship.setCompanyName(company);
        mockInternship.setDescription("Target internship requirements and technologies.");

        for (String rName : requiredSkillNames) {
            mockInternship.addRequiredSkill(new InternshipSkill(rName, ProficiencyLevel.INTERMEDIATE, true, 3));
        }

        SkillGapAnalysisDto analysis = skillMatchingService.analyzeSkillGap(profile, mockInternship);
        return ResponseEntity.ok(analysis);
    }
}
