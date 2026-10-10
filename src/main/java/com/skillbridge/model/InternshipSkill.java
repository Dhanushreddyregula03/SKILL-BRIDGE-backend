package com.skillbridge.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "internship_skills")
public class InternshipSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "internship_id")
    @JsonBackReference
    private Internship internship;

    @Column(nullable = false)
    private String skillName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProficiencyLevel minimumLevel = ProficiencyLevel.BEGINNER;

    private Boolean isMandatory = true;

    private Integer weight = 3; // 1 to 5 scale

    public InternshipSkill() {
    }

    public InternshipSkill(String skillName, ProficiencyLevel minimumLevel, Boolean isMandatory, Integer weight) {
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

    public Internship getInternship() {
        return internship;
    }

    public void setInternship(Internship internship) {
        this.internship = internship;
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
