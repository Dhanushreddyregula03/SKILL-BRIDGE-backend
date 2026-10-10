package com.skillbridge.dto;

import com.skillbridge.model.InternshipStatus;
import com.skillbridge.model.WorkMode;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InternshipDto {
    private Long id;
    private Long employerId;
    private String employerName;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Company name is required")
    private String companyName;

    private String companyLogo;
    private String location;
    private WorkMode workMode = WorkMode.REMOTE;
    private String category = "Software Engineering";
    private String duration = "3 Months";
    private Double stipendMonthly = 0.0;
    private String description;
    private String responsibilities;
    private String perks;
    private Integer openings = 1;
    private InternshipStatus status = InternshipStatus.ACTIVE;
    private LocalDate deadline;
    private LocalDateTime createdAt;
    private List<InternshipSkillDto> requiredSkills = new ArrayList<>();

    // Calculated on-the-fly for current student (if logged in)
    private Double userMatchScore;
    private Long applicantCount;

    public InternshipDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployerId() {
        return employerId;
    }

    public void setEmployerId(Long employerId) {
        this.employerId = employerId;
    }

    public String getEmployerName() {
        return employerName;
    }

    public void setEmployerName(String employerName) {
        this.employerName = employerName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public WorkMode getWorkMode() {
        return workMode;
    }

    public void setWorkMode(WorkMode workMode) {
        this.workMode = workMode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Double getStipendMonthly() {
        return stipendMonthly;
    }

    public void setStipendMonthly(Double stipendMonthly) {
        this.stipendMonthly = stipendMonthly;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResponsibilities() {
        return responsibilities;
    }

    public void setResponsibilities(String responsibilities) {
        this.responsibilities = responsibilities;
    }

    public String getPerks() {
        return perks;
    }

    public void setPerks(String perks) {
        this.perks = perks;
    }

    public Integer getOpenings() {
        return openings;
    }

    public void setOpenings(Integer openings) {
        this.openings = openings;
    }

    public InternshipStatus getStatus() {
        return status;
    }

    public void setStatus(InternshipStatus status) {
        this.status = status;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<InternshipSkillDto> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<InternshipSkillDto> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public Double getUserMatchScore() {
        return userMatchScore;
    }

    public void setUserMatchScore(Double userMatchScore) {
        this.userMatchScore = userMatchScore;
    }

    public Long getApplicantCount() {
        return applicantCount;
    }

    public void setApplicantCount(Long applicantCount) {
        this.applicantCount = applicantCount;
    }
}
