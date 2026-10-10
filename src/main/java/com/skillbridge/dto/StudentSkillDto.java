package com.skillbridge.dto;

import com.skillbridge.model.ProficiencyLevel;

public class StudentSkillDto {
    private Long id;
    private String skillName;
    private ProficiencyLevel level = ProficiencyLevel.INTERMEDIATE;
    private Integer experienceMonths;

    public StudentSkillDto() {
    }

    public StudentSkillDto(Long id, String skillName, ProficiencyLevel level, Integer experienceMonths) {
        this.id = id;
        this.skillName = skillName;
        this.level = level;
        this.experienceMonths = experienceMonths;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public ProficiencyLevel getLevel() {
        return level;
    }

    public void setLevel(ProficiencyLevel level) {
        this.level = level;
    }

    public Integer getExperienceMonths() {
        return experienceMonths;
    }

    public void setExperienceMonths(Integer experienceMonths) {
        this.experienceMonths = experienceMonths;
    }
}
