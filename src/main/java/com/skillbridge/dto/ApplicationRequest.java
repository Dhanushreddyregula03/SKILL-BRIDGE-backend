package com.skillbridge.dto;

import jakarta.validation.constraints.NotNull;

public class ApplicationRequest {
    @NotNull(message = "Internship ID is required")
    private Long internshipId;

    private String coverLetter;

    public ApplicationRequest() {
    }

    public ApplicationRequest(Long internshipId, String coverLetter) {
        this.internshipId = internshipId;
        this.coverLetter = coverLetter;
    }

    public Long getInternshipId() {
        return internshipId;
    }

    public void setInternshipId(Long internshipId) {
        this.internshipId = internshipId;
    }

    public String getCoverLetter() {
        return coverLetter;
    }

    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }
}
