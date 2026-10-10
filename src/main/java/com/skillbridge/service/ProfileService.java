package com.skillbridge.service;

import com.skillbridge.dto.RecruiterProfileDto;
import com.skillbridge.dto.StudentProfileDto;
import com.skillbridge.dto.StudentSkillDto;
import com.skillbridge.model.RecruiterProfile;
import com.skillbridge.model.StudentProfile;
import com.skillbridge.model.StudentSkill;
import com.skillbridge.model.User;
import com.skillbridge.repository.RecruiterProfileRepository;
import com.skillbridge.repository.StudentProfileRepository;
import com.skillbridge.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;

    public ProfileService(UserRepository userRepository,
                          StudentProfileRepository studentProfileRepository,
                          RecruiterProfileRepository recruiterProfileRepository) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.recruiterProfileRepository = recruiterProfileRepository;
    }

    public StudentProfileDto getStudentProfileByUserId(Long userId) {
        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    StudentProfile newProfile = new StudentProfile(user);
                    return studentProfileRepository.save(newProfile);
                });

        return convertToStudentDto(profile);
    }

    @Transactional
    public StudentProfileDto updateStudentProfile(Long userId, StudentProfileDto dto) {
        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    return new StudentProfile(user);
                });

        User user = profile.getUser();
        if (dto.getFullName() != null && !dto.getFullName().isBlank()) {
            user.setFullName(dto.getFullName());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getAvatarUrl() != null) {
            user.setAvatarUrl(dto.getAvatarUrl());
        }
        userRepository.save(user);

        profile.setHeadline(dto.getHeadline());
        profile.setBio(dto.getBio());
        profile.setUniversity(dto.getUniversity());
        profile.setDegree(dto.getDegree());
        profile.setCgpa(dto.getCgpa());
        profile.setGraduationYear(dto.getGraduationYear());
        profile.setGithubUrl(dto.getGithubUrl());
        profile.setLinkedinUrl(dto.getLinkedinUrl());
        profile.setPortfolioUrl(dto.getPortfolioUrl());
        profile.setResumeText(dto.getResumeText());

        if (dto.getSkills() != null) {
            profile.getSkills().clear();
            for (StudentSkillDto skillDto : dto.getSkills()) {
                StudentSkill skill = new StudentSkill(
                        skillDto.getSkillName(),
                        skillDto.getLevel(),
                        skillDto.getExperienceMonths()
                );
                profile.addSkill(skill);
            }
        }

        profile = studentProfileRepository.save(profile);
        return convertToStudentDto(profile);
    }

    public RecruiterProfileDto getRecruiterProfileByUserId(Long userId) {
        RecruiterProfile profile = recruiterProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    RecruiterProfile newProfile = new RecruiterProfile(user, "My Company");
                    return recruiterProfileRepository.save(newProfile);
                });

        return convertToRecruiterDto(profile);
    }

    @Transactional
    public RecruiterProfileDto updateRecruiterProfile(Long userId, RecruiterProfileDto dto) {
        RecruiterProfile profile = recruiterProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    return new RecruiterProfile(user, dto.getCompanyName());
                });

        profile.setCompanyName(dto.getCompanyName());
        profile.setCompanyWebsite(dto.getCompanyWebsite());
        profile.setCompanyLogo(dto.getCompanyLogo());
        profile.setIndustry(dto.getIndustry());
        profile.setCompanyDescription(dto.getCompanyDescription());
        profile.setLocation(dto.getLocation());

        profile = recruiterProfileRepository.save(profile);
        return convertToRecruiterDto(profile);
    }

    public StudentProfileDto convertToStudentDto(StudentProfile profile) {
        StudentProfileDto dto = new StudentProfileDto();
        dto.setId(profile.getId());
        dto.setUserId(profile.getUser().getId());
        dto.setFullName(profile.getUser().getFullName());
        dto.setEmail(profile.getUser().getEmail());
        dto.setPhone(profile.getUser().getPhone());
        dto.setAvatarUrl(profile.getUser().getAvatarUrl());
        dto.setHeadline(profile.getHeadline());
        dto.setBio(profile.getBio());
        dto.setUniversity(profile.getUniversity());
        dto.setDegree(profile.getDegree());
        dto.setCgpa(profile.getCgpa());
        dto.setGraduationYear(profile.getGraduationYear());
        dto.setGithubUrl(profile.getGithubUrl());
        dto.setLinkedinUrl(profile.getLinkedinUrl());
        dto.setPortfolioUrl(profile.getPortfolioUrl());
        dto.setResumeText(profile.getResumeText());

        List<StudentSkillDto> skills = profile.getSkills().stream().map(s -> 
                new StudentSkillDto(s.getId(), s.getSkillName(), s.getLevel(), s.getExperienceMonths())
        ).collect(Collectors.toList());
        dto.setSkills(skills);

        return dto;
    }

    public RecruiterProfileDto convertToRecruiterDto(RecruiterProfile profile) {
        RecruiterProfileDto dto = new RecruiterProfileDto();
        dto.setId(profile.getId());
        dto.setUserId(profile.getUser().getId());
        dto.setCompanyName(profile.getCompanyName());
        dto.setCompanyWebsite(profile.getCompanyWebsite());
        dto.setCompanyLogo(profile.getCompanyLogo());
        dto.setIndustry(profile.getIndustry());
        dto.setCompanyDescription(profile.getCompanyDescription());
        dto.setLocation(profile.getLocation());
        return dto;
    }
}
