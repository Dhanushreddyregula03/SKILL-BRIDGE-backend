package com.skillbridge.dto;

import com.skillbridge.model.ProficiencyLevel;

public class InternshipSkillDto {
    private Long id;
    private String skillName;
    private ProficiencyLevel minimumLevel = ProficiencyLevel.BEGINNER;
    private Boolean isMandatory = true;
    private Integer weight = 3;

    public InternshipSkillDto() {
    }

    public InternshipSkillDto(Long id, String skillName, ProficiencyLevel minimumLevel, Boolean isMandatory, Integer weight) {
        this.id = id;
        this.skillName = skillName;
        this.minimumLevel = minimumLevel;
        this.isMandatory = isMandatory;
        this.weight = weight;
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

    public ProficiencyLevel getMinimumLevel() {
        return minimumLevel;
    }

    public void setMinimumLevel(ProficiencyLevel minimumLevel) {
        this.minimumLevel = minimumLevel;
    }

    public Boolean getIsMandatory() {
        return isMandatory;
    }

    public void setIsMandatory(Boolean mandatory) {
        isMandatory = mandatory;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}
