package com.skillbridge.service;

import com.skillbridge.dto.InternshipDto;
import com.skillbridge.dto.InternshipSkillDto;
import com.skillbridge.model.*;
import com.skillbridge.repository.ApplicationRepository;
import com.skillbridge.repository.InternshipRepository;
import com.skillbridge.repository.StudentProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InternshipService {

    private final InternshipRepository internshipRepository;
    private final ApplicationRepository applicationRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final SkillMatchingService skillMatchingService;

    public InternshipService(InternshipRepository internshipRepository,
                             ApplicationRepository applicationRepository,
                             StudentProfileRepository studentProfileRepository,
                             SkillMatchingService skillMatchingService) {
        this.internshipRepository = internshipRepository;
        this.applicationRepository = applicationRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.skillMatchingService = skillMatchingService;
    }

    public List<InternshipDto> getAllInternships(String search, String category, WorkMode workMode, User currentStudent) {
        List<Internship> list;
        if (search != null && !search.isBlank()) {
            String pattern = "%" + search.trim().toLowerCase() + "%";
            list = internshipRepository.searchInternships(InternshipStatus.ACTIVE, pattern);
        } else {
            list = internshipRepository.findByStatusOrderByCreatedAtDesc(InternshipStatus.ACTIVE);
        }

        StudentProfile studentProfile = null;
        if (currentStudent != null && currentStudent.getRole() == Role.ROLE_STUDENT) {
            studentProfile = studentProfileRepository.findByUserId(currentStudent.getId()).orElse(null);
        }

        final StudentProfile finalProfile = studentProfile;

        return list.stream()
                .filter(i -> category == null || category.isBlank() || category.equalsIgnoreCase("All") || i.getCategory().equalsIgnoreCase(category))
                .filter(i -> workMode == null || i.getWorkMode() == workMode)
                .map(i -> convertToDto(i, finalProfile))
                .sorted((a, b) -> {
                    if (finalProfile != null && a.getUserMatchScore() != null && b.getUserMatchScore() != null) {
                        return Double.compare(b.getUserMatchScore(), a.getUserMatchScore()); // Descending by AI match
                    }
                    return b.getId().compareTo(a.getId());
                })
                .collect(Collectors.toList());
    }

    public InternshipDto getInternshipById(Long id, User currentStudent) {
        Internship internship = internshipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Internship not found with id: " + id));

        StudentProfile studentProfile = null;
        if (currentStudent != null && currentStudent.getRole() == Role.ROLE_STUDENT) {
            studentProfile = studentProfileRepository.findByUserId(currentStudent.getId()).orElse(null);
        }

        return convertToDto(internship, studentProfile);
    }

    public List<InternshipDto> getEmployerInternships(User employer) {
        List<Internship> list = internshipRepository.findByEmployerOrderByCreatedAtDesc(employer);
        return list.stream().map(i -> convertToDto(i, null)).collect(Collectors.toList());
    }

    @Transactional
    public InternshipDto createInternship(InternshipDto dto, User employer) {
        Internship internship = new Internship();
        internship.setEmployer(employer);
        internship.setTitle(dto.getTitle());
        internship.setCompanyName(dto.getCompanyName());
        internship.setCompanyLogo(dto.getCompanyLogo());
        internship.setLocation(dto.getLocation());
        internship.setWorkMode(dto.getWorkMode() != null ? dto.getWorkMode() : WorkMode.REMOTE);
        internship.setCategory(dto.getCategory() != null ? dto.getCategory() : "Software Engineering");
        internship.setDuration(dto.getDuration() != null ? dto.getDuration() : "3 Months");
        internship.setStipendMonthly(dto.getStipendMonthly() != null ? dto.getStipendMonthly() : 0.0);
        internship.setDescription(dto.getDescription());
        internship.setResponsibilities(dto.getResponsibilities());
        internship.setPerks(dto.getPerks());
        internship.setOpenings(dto.getOpenings() != null ? dto.getOpenings() : 1);
        internship.setStatus(dto.getStatus() != null ? dto.getStatus() : InternshipStatus.ACTIVE);
        internship.setDeadline(dto.getDeadline());

        if (dto.getRequiredSkills() != null) {
            for (InternshipSkillDto sDto : dto.getRequiredSkills()) {
                InternshipSkill skill = new InternshipSkill(
                        sDto.getSkillName(),
                        sDto.getMinimumLevel(),
                        sDto.getIsMandatory(),
                        sDto.getWeight()
                );
                internship.addRequiredSkill(skill);
            }
        }

        internship = internshipRepository.save(internship);
        return convertToDto(internship, null);
    }

    @Transactional
    public InternshipDto updateInternship(Long id, InternshipDto dto, User employer) {
        Internship internship = internshipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Internship not found"));

        if (!internship.getEmployer().getId().equals(employer.getId())) {
            throw new RuntimeException("Unauthorized: You do not own this internship posting");
        }

        internship.setTitle(dto.getTitle());
        internship.setCompanyName(dto.getCompanyName());
        internship.setCompanyLogo(dto.getCompanyLogo());
        internship.setLocation(dto.getLocation());
        internship.setWorkMode(dto.getWorkMode());
        internship.setCategory(dto.getCategory());
        internship.setDuration(dto.getDuration());
        internship.setStipendMonthly(dto.getStipendMonthly());
        internship.setDescription(dto.getDescription());
        internship.setResponsibilities(dto.getResponsibilities());
        internship.setPerks(dto.getPerks());
        internship.setOpenings(dto.getOpenings());
        internship.setStatus(dto.getStatus());
        internship.setDeadline(dto.getDeadline());

        if (dto.getRequiredSkills() != null) {
            internship.getRequiredSkills().clear();
            for (InternshipSkillDto sDto : dto.getRequiredSkills()) {
                InternshipSkill skill = new InternshipSkill(
                        sDto.getSkillName(),
                        sDto.getMinimumLevel(),
                        sDto.getIsMandatory(),
                        sDto.getWeight()
                );
                internship.addRequiredSkill(skill);
            }
        }

        internship = internshipRepository.save(internship);
        return convertToDto(internship, null);
    }

    @Transactional
    public void deleteInternship(Long id, User employer) {
        Internship internship = internshipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Internship not found"));

        if (!internship.getEmployer().getId().equals(employer.getId())) {
            throw new RuntimeException("Unauthorized: You do not own this internship posting");
        }

        internshipRepository.delete(internship);
    }

    public InternshipDto convertToDto(Internship internship, StudentProfile studentProfile) {
        InternshipDto dto = new InternshipDto();
        dto.setId(internship.getId());
        dto.setEmployerId(internship.getEmployer().getId());
        dto.setEmployerName(internship.getEmployer().getFullName());
        dto.setTitle(internship.getTitle());
        dto.setCompanyName(internship.getCompanyName());
        dto.setCompanyLogo(internship.getCompanyLogo());
        dto.setLocation(internship.getLocation());
        dto.setWorkMode(internship.getWorkMode());
        dto.setCategory(internship.getCategory());
        dto.setDuration(internship.getDuration());
        dto.setStipendMonthly(internship.getStipendMonthly());
        dto.setDescription(internship.getDescription());
        dto.setResponsibilities(internship.getResponsibilities());
        dto.setPerks(internship.getPerks());
        dto.setOpenings(internship.getOpenings());
        dto.setStatus(internship.getStatus());
        dto.setDeadline(internship.getDeadline());
        dto.setCreatedAt(internship.getCreatedAt());

        long count = applicationRepository.countByInternshipId(internship.getId());
        dto.setApplicantCount(count);

        if (internship.getRequiredSkills() != null) {
            dto.setRequiredSkills(internship.getRequiredSkills().stream().map(s ->
                    new InternshipSkillDto(s.getId(), s.getSkillName(), s.getMinimumLevel(), s.getIsMandatory(), s.getWeight())
            ).collect(Collectors.toList()));
        }

        if (studentProfile != null) {
            double score = skillMatchingService.calculateMatchScore(studentProfile, internship);
            dto.setUserMatchScore(score);
        }

        return dto;
    }
}
