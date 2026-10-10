package com.skillbridge.dto;

import java.util.ArrayList;
import java.util.List;

public class SkillGapAnalysisDto {
    private Long internshipId;
    private String internshipTitle;
    private String companyName;
    private Double matchPercentage;
    private String verdict;
    private String matchSummary;

    private List<SkillItem> matchedSkills = new ArrayList<>();
    private List<SkillItem> missingSkills = new ArrayList<>();
    private List<RoadmapStep> learningRoadmap = new ArrayList<>();
    private List<InterviewQuestion> interviewPrepQuestions = new ArrayList<>();

    public SkillGapAnalysisDto() {
    }

    public static class SkillItem {
        private String skillName;
        private String userProficiency;
        private String requiredProficiency;
        private boolean isMandatory;
        private String recommendation;

        public SkillItem() {
        }

        public SkillItem(String skillName, String userProficiency, String requiredProficiency, boolean isMandatory, String recommendation) {
            this.skillName = skillName;
            this.userProficiency = userProficiency;
            this.requiredProficiency = requiredProficiency;
            this.isMandatory = isMandatory;
            this.recommendation = recommendation;
        }

        public String getSkillName() {
            return skillName;
        }

        public void setSkillName(String skillName) {
            this.skillName = skillName;
        }

        public String getUserProficiency() {
            return userProficiency;
        }

        public void setUserProficiency(String userProficiency) {
            this.userProficiency = userProficiency;
        }

        public String getRequiredProficiency() {
            return requiredProficiency;
        }

        public void setRequiredProficiency(String requiredProficiency) {
            this.requiredProficiency = requiredProficiency;
        }

        public boolean isMandatory() {
            return isMandatory;
        }

        public void setMandatory(boolean mandatory) {
            isMandatory = mandatory;
        }

        public String getRecommendation() {
            return recommendation;
        }

        public void setRecommendation(String recommendation) {
            this.recommendation = recommendation;
        }
    }

    public static class RoadmapStep {
        private String phase;
        private String title;
        private String focusArea;
        private String actionPlan;
        private List<String> resources = new ArrayList<>();

        public RoadmapStep() {
        }

        public RoadmapStep(String phase, String title, String focusArea, String actionPlan, List<String> resources) {
            this.phase = phase;
            this.title = title;
            this.focusArea = focusArea;
            this.actionPlan = actionPlan;
            this.resources = resources;
        }

        public String getPhase() {
            return phase;
        }

        public void setPhase(String phase) {
            this.phase = phase;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getFocusArea() {
            return focusArea;
        }

        public void setFocusArea(String focusArea) {
            this.focusArea = focusArea;
        }

        public String getActionPlan() {
            return actionPlan;
        }

        public void setActionPlan(String actionPlan) {
            this.actionPlan = actionPlan;
        }

        public List<String> getResources() {
            return resources;
        }

        public void setResources(List<String> resources) {
            this.resources = resources;
        }
    }

    public static class InterviewQuestion {
        private String question;
        private String skillFocus;
        private String whyItMatters;
        private String keyConceptsToMention;

        public InterviewQuestion() {
        }

        public InterviewQuestion(String question, String skillFocus, String whyItMatters, String keyConceptsToMention) {
            this.question = question;
            this.skillFocus = skillFocus;
            this.whyItMatters = whyItMatters;
            this.keyConceptsToMention = keyConceptsToMention;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getSkillFocus() {
            return skillFocus;
        }

        public void setSkillFocus(String skillFocus) {
            this.skillFocus = skillFocus;
        }

        public String getWhyItMatters() {
            return whyItMatters;
        }

        public void setWhyItMatters(String whyItMatters) {
            this.whyItMatters = whyItMatters;
        }

        public String getKeyConceptsToMention() {
            return keyConceptsToMention;
        }

        public void setKeyConceptsToMention(String keyConceptsToMention) {
            this.keyConceptsToMention = keyConceptsToMention;
        }
    }

    public Long getInternshipId() {
        return internshipId;
    }

    public void setInternshipId(Long internshipId) {
        this.internshipId = internshipId;
    }

    public String getInternshipTitle() {
        return internshipTitle;
    }

    public void setInternshipTitle(String internshipTitle) {
        this.internshipTitle = internshipTitle;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Double getMatchPercentage() {
        return matchPercentage;
    }

    public void setMatchPercentage(Double matchPercentage) {
        this.matchPercentage = matchPercentage;
    }

    public String getVerdict() {
        return verdict;
    }

    public void setVerdict(String verdict) {
        this.verdict = verdict;
    }

    public String getMatchSummary() {
        return matchSummary;
    }

    public void setMatchSummary(String matchSummary) {
        this.matchSummary = matchSummary;
    }

    public List<SkillItem> getMatchedSkills() {
        return matchedSkills;
    }

    public void setMatchedSkills(List<SkillItem> matchedSkills) {
        this.matchedSkills = matchedSkills;
    }

    public List<SkillItem> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<SkillItem> missingSkills) {
        this.missingSkills = missingSkills;
    }

    public List<RoadmapStep> getLearningRoadmap() {
        return learningRoadmap;
    }

    public void setLearningRoadmap(List<RoadmapStep> learningRoadmap) {
        this.learningRoadmap = learningRoadmap;
    }

    public List<InterviewQuestion> getInterviewPrepQuestions() {
        return interviewPrepQuestions;
    }

    public void setInterviewPrepQuestions(List<InterviewQuestion> interviewPrepQuestions) {
        this.interviewPrepQuestions = interviewPrepQuestions;
    }
}
