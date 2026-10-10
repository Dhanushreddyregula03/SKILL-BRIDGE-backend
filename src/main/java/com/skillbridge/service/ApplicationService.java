package com.skillbridge.service;

import com.skillbridge.dto.ApplicationRequest;
import com.skillbridge.dto.ApplicationResponseDto;
import com.skillbridge.model.*;
import com.skillbridge.repository.ApplicationRepository;
import com.skillbridge.repository.InternshipRepository;
import com.skillbridge.repository.StudentProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final InternshipRepository internshipRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final SkillMatchingService skillMatchingService;

    public ApplicationService(ApplicationRepository applicationRepository,
                              InternshipRepository internshipRepository,
                              StudentProfileRepository studentProfileRepository,
                              SkillMatchingService skillMatchingService) {
        this.applicationRepository = applicationRepository;
        this.internshipRepository = internshipRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.skillMatchingService = skillMatchingService;
    }

    @Transactional
    public ApplicationResponseDto apply(ApplicationRequest request, User student) {
        Internship internship = internshipRepository.findById(request.getInternshipId())
                .orElseThrow(() -> new RuntimeException("Internship not found"));

        if (internship.getStatus() != InternshipStatus.ACTIVE) {
            throw new IllegalStateException("This internship is no longer accepting applications.");
        }

        if (applicationRepository.existsByInternshipIdAndStudentId(internship.getId(), student.getId())) {
            throw new IllegalArgumentException("You have already applied for this internship.");
        }

        StudentProfile profile = studentProfileRepository.findByUserId(student.getId()).orElse(null);
        double score = skillMatchingService.calculateMatchScore(profile, internship);

        String resumeSnapshot = "";
        if (profile != null) {
            resumeSnapshot = "Education: " + profile.getDegree() + " (" + profile.getUniversity() + ")\n" +
                             "CGPA: " + profile.getCgpa() + "\n" +
                             "Skills: " + profile.getSkills().stream().map(s -> s.getSkillName() + " (" + s.getLevel() + ")").collect(Collectors.joining(", ")) + "\n" +
                             "Resume Summary: " + (profile.getResumeText() != null ? profile.getResumeText() : "");
        }

        Application application = new Application(internship, student, score, request.getCoverLetter());
        application.setResumeSnapshot(resumeSnapshot);

        application = applicationRepository.save(application);
        return convertToDto(application, profile);
    }

    public List<ApplicationResponseDto> getStudentApplications(User student) {
        List<Application> applications = applicationRepository.findByStudentOrderByAppliedAtDesc(student);
        StudentProfile profile = studentProfileRepository.findByUserId(student.getId()).orElse(null);
        return applications.stream()
                .map(app -> convertToDto(app, profile))
                .collect(Collectors.toList());
    }

    public List<ApplicationResponseDto> getEmployerApplications(User employer) {
        List<Application> applications = applicationRepository.findByInternshipEmployerOrderByAppliedAtDesc(employer);
        // Order by match score descending to present highest qualified talent first
        return applications.stream()
                .map(app -> {
                    StudentProfile profile = studentProfileRepository.findByUserId(app.getStudent().getId()).orElse(null);
                    return convertToDto(app, profile);
                })
                .sorted((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()))
                .collect(Collectors.toList());
    }

    public List<ApplicationResponseDto> getApplicationsForInternship(Long internshipId, User employer) {
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new RuntimeException("Internship not found"));

        if (!internship.getEmployer().getId().equals(employer.getId())) {
            throw new RuntimeException("Unauthorized: You do not own this internship posting");
        }

        List<Application> applications = applicationRepository.findByInternshipOrderByAppliedAtDesc(internship);
        return applications.stream()
                .map(app -> {
                    StudentProfile profile = studentProfileRepository.findByUserId(app.getStudent().getId()).orElse(null);
                    return convertToDto(app, profile);
                })
                .sorted((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()))
                .collect(Collectors.toList());
    }

    @Transactional
    public ApplicationResponseDto updateStatus(Long applicationId, ApplicationStatus status, String recruiterNotes, User employer) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!application.getInternship().getEmployer().getId().equals(employer.getId())) {
            throw new RuntimeException("Unauthorized: You do not have permission to manage this applicant");
        }

        application.setStatus(status);
        if (recruiterNotes != null) {
            application.setRecruiterNotes(recruiterNotes);
        }
        application.setUpdatedAt(LocalDateTime.now());

        application = applicationRepository.save(application);
        StudentProfile profile = studentProfileRepository.findByUserId(application.getStudent().getId()).orElse(null);
        return convertToDto(application, profile);
    }

    private ApplicationResponseDto convertToDto(Application app, StudentProfile profile) {
        ApplicationResponseDto dto = new ApplicationResponseDto();
        dto.setId(app.getId());
        dto.setInternshipId(app.getInternship().getId());
        dto.setInternshipTitle(app.getInternship().getTitle());
        dto.setCompanyName(app.getInternship().getCompanyName());
        dto.setCompanyLogo(app.getInternship().getCompanyLogo());
        dto.setLocation(app.getInternship().getLocation());

        dto.setStudentId(app.getStudent().getId());
        dto.setStudentName(app.getStudent().getFullName());
        dto.setStudentEmail(app.getStudent().getEmail());

        if (profile != null) {
            dto.setStudentHeadline(profile.getHeadline());
            dto.setStudentUniversity(profile.getUniversity());
            dto.setStudentResumeText(profile.getResumeText());
        }

        dto.setStatus(app.getStatus());
        dto.setMatchScore(app.getMatchScore());
        dto.setCoverLetter(app.getCoverLetter());
        dto.setRecruiterNotes(app.getRecruiterNotes());
        dto.setAppliedAt(app.getAppliedAt());
        dto.setUpdatedAt(app.getUpdatedAt());

        return dto;
    }
}
