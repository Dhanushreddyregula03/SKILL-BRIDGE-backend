package com.skillbridge.service;

import com.skillbridge.dto.SkillGapAnalysisDto;
import com.skillbridge.model.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SkillMatchingService {

    // Common synonyms and aliases for technical skills
    private static final Map<String, String> SKILL_ALIASES = new HashMap<>();

    static {
        SKILL_ALIASES.put("reactjs", "react");
        SKILL_ALIASES.put("react.js", "react");
        SKILL_ALIASES.put("springboot", "spring boot");
        SKILL_ALIASES.put("spring", "spring boot");
        SKILL_ALIASES.put("nodejs", "node.js");
        SKILL_ALIASES.put("node", "node.js");
        SKILL_ALIASES.put("js", "javascript");
        SKILL_ALIASES.put("ts", "typescript");
        SKILL_ALIASES.put("postgres", "postgresql");
        SKILL_ALIASES.put("k8s", "kubernetes");
        SKILL_ALIASES.put("py", "python");
        SKILL_ALIASES.put("golang", "go");
        SKILL_ALIASES.put("docker container", "docker");
        SKILL_ALIASES.put("aws cloud", "aws");
        SKILL_ALIASES.put("ml", "machine learning");
        SKILL_ALIASES.put("ai", "artificial intelligence");
        SKILL_ALIASES.put("deep learning", "machine learning");
        SKILL_ALIASES.put("nlp", "natural language processing");
    }

    public double calculateMatchScore(StudentProfile profile, Internship internship) {
        if (internship.getRequiredSkills() == null || internship.getRequiredSkills().isEmpty()) {
            return 75.0; // Baseline if no specific skills specified
        }

        List<StudentSkill> studentSkills = profile != null && profile.getSkills() != null 
                ? profile.getSkills() 
                : Collections.emptyList();

        Map<String, StudentSkill> normalizedStudentSkills = new HashMap<>();
        for (StudentSkill s : studentSkills) {
            String norm = normalizeSkill(s.getSkillName());
            normalizedStudentSkills.put(norm, s);
        }

        double totalWeightedPoints = 0.0;
        double maxPossibleWeightedPoints = 0.0;

        for (InternshipSkill req : internship.getRequiredSkills()) {
            String reqNorm = normalizeSkill(req.getSkillName());
            int weight = req.getWeight() != null ? req.getWeight() : 3;
            if (Boolean.TRUE.equals(req.getIsMandatory())) {
                weight *= 2;
            }

            maxPossibleWeightedPoints += (weight * 1.0);

            if (normalizedStudentSkills.containsKey(reqNorm)) {
                StudentSkill studentSkill = normalizedStudentSkills.get(reqNorm);
                int studentLvl = studentSkill.getLevel() != null ? studentSkill.getLevel().getLevelValue() : 2;
                int reqLvl = req.getMinimumLevel() != null ? req.getMinimumLevel().getLevelValue() : 1;

                if (studentLvl >= reqLvl) {
                    totalWeightedPoints += (weight * 1.0);
                } else if (studentLvl == reqLvl - 1) {
                    totalWeightedPoints += (weight * 0.70);
                } else {
                    totalWeightedPoints += (weight * 0.40);
                }
            } else if (profile != null && profile.getResumeText() != null && 
                       profile.getResumeText().toLowerCase().contains(reqNorm)) {
                // Resume mentions keyword but not in formal skill list
                totalWeightedPoints += (weight * 0.50);
            }
        }

        double skillScore = maxPossibleWeightedPoints > 0 
                ? (totalWeightedPoints / maxPossibleWeightedPoints) * 85.0 
                : 70.0;

        // Contextual boost from Resume / Bio text analysis (up to 15 points)
        double textRelevanceBoost = calculateResumeRelevance(profile, internship);

        double finalScore = Math.min(100.0, Math.round((skillScore + textRelevanceBoost) * 10.0) / 10.0);
        return finalScore;
    }

    private double calculateResumeRelevance(StudentProfile profile, Internship internship) {
        if (profile == null) return 5.0;

        String studentText = (Optional.ofNullable(profile.getBio()).orElse("") + " " +
                              Optional.ofNullable(profile.getHeadline()).orElse("") + " " +
                              Optional.ofNullable(profile.getResumeText()).orElse("")).toLowerCase();

        String jobText = (Optional.ofNullable(internship.getDescription()).orElse("") + " " +
                          Optional.ofNullable(internship.getResponsibilities()).orElse("")).toLowerCase();

        if (studentText.isBlank() || jobText.isBlank()) {
            return 8.0; // Reasonable default
        }

        List<String> keywords = Arrays.asList(
                "rest api", "microservices", "git", "ci/cd", "agile", "scrum", "sql", "nosql",
                "cloud", "unit testing", "architecture", "data structures", "algorithms",
                "distributed systems", "frontend", "backend", "full-stack", "full stack"
        );

        int matchedCount = 0;
        for (String kw : keywords) {
            if (jobText.contains(kw) && studentText.contains(kw)) {
                matchedCount++;
            }
        }

        return Math.min(15.0, matchedCount * 2.5);
    }

    public SkillGapAnalysisDto analyzeSkillGap(StudentProfile profile, Internship internship) {
        SkillGapAnalysisDto result = new SkillGapAnalysisDto();
        result.setInternshipId(internship.getId());
        result.setInternshipTitle(internship.getTitle());
        result.setCompanyName(internship.getCompanyName());

        double matchPercentage = calculateMatchScore(profile, internship);
        result.setMatchPercentage(matchPercentage);

        if (matchPercentage >= 85.0) {
            result.setVerdict("Exceptional Match (Top 5%)");
            result.setMatchSummary("Your profile strongly aligns with this role's tech stack and requirements. You are a prime candidate!");
        } else if (matchPercentage >= 70.0) {
            result.setVerdict("Strong Candidate Match");
            result.setMatchSummary("You possess the majority of core required competencies with only minor gaps that can be mastered quickly.");
        } else if (matchPercentage >= 50.0) {
            result.setVerdict("Moderate Potential Match");
            result.setMatchSummary("You have good foundational knowledge. Bridging the highlighted skills will significantly increase your chances.");
        } else {
            result.setVerdict("Skill Gap Identified");
            result.setMatchSummary("Several key requirements are currently missing. Follow the customized 3-week study roadmap below to qualify.");
        }

        List<StudentSkill> studentSkills = profile != null && profile.getSkills() != null 
                ? profile.getSkills() 
                : Collections.emptyList();

        Map<String, StudentSkill> studentSkillMap = new HashMap<>();
        for (StudentSkill s : studentSkills) {
            studentSkillMap.put(normalizeSkill(s.getSkillName()), s);
        }

        List<SkillGapAnalysisDto.SkillItem> matched = new ArrayList<>();
        List<SkillGapAnalysisDto.SkillItem> missing = new ArrayList<>();

        for (InternshipSkill req : internship.getRequiredSkills()) {
            String norm = normalizeSkill(req.getSkillName());
            boolean isMandatory = Boolean.TRUE.equals(req.getIsMandatory());
            String reqLvlStr = req.getMinimumLevel() != null ? req.getMinimumLevel().name() : "BEGINNER";

            if (studentSkillMap.containsKey(norm)) {
                StudentSkill userSkill = studentSkillMap.get(norm);
                String userLvlStr = userSkill.getLevel() != null ? userSkill.getLevel().name() : "BEGINNER";

                int userLvlVal = userSkill.getLevel() != null ? userSkill.getLevel().getLevelValue() : 1;
                int reqLvlVal = req.getMinimumLevel() != null ? req.getMinimumLevel().getLevelValue() : 1;

                String recommendation;
                if (userLvlVal >= reqLvlVal) {
                    recommendation = "Verified proficiency meets role expectations.";
                } else {
                    recommendation = "Upgrade from " + userLvlStr + " to " + reqLvlStr + " by completing 1 hands-on project.";
                }

                matched.add(new SkillGapAnalysisDto.SkillItem(
                        req.getSkillName(),
                        userLvlStr,
                        reqLvlStr,
                        isMandatory,
                        recommendation
                ));
            } else {
                missing.add(new SkillGapAnalysisDto.SkillItem(
                        req.getSkillName(),
                        "Not in Profile",
                        reqLvlStr,
                        isMandatory,
                        isMandatory 
                            ? "CRITICAL: Employer marked this as mandatory. Spend 4-6 hours practicing core fundamentals."
                            : "Recommended: Good-to-have skill to stand out among other applicants."
                ));
            }
        }

        result.setMatchedSkills(matched);
        result.setMissingSkills(missing);

        // Generate tailored Learning Roadmap
        result.setLearningRoadmap(generateRoadmap(missing, internship));

        // Generate AI Interview Questions
        result.setInterviewPrepQuestions(generateInterviewQuestions(matched, missing, internship));

        return result;
    }

    private List<SkillGapAnalysisDto.RoadmapStep> generateRoadmap(List<SkillGapAnalysisDto.SkillItem> missing, Internship internship) {
        List<SkillGapAnalysisDto.RoadmapStep> roadmap = new ArrayList<>();

        if (missing.isEmpty()) {
            roadmap.add(new SkillGapAnalysisDto.RoadmapStep(
                    "Phase 1 (Interview Focus)",
                    "Deep-Dive Technical Architecture & System Design",
                    "Advanced System Patterns for " + internship.getTitle(),
                    "Review concurrency, caching (Redis), REST API versioning, and end-to-end testing practices.",
                    Arrays.asList("System Design Primer GitHub", "Official Spring & React Documentation", "LeetCode System Design Challenges")
            ));
            roadmap.add(new SkillGapAnalysisDto.RoadmapStep(
                    "Phase 2 (Portfolio Polish)",
                    "Showcase Relevant Github Projects",
                    "Targeted Project Demonstrating " + internship.getCompanyName() + "'s Stack",
                    "Deploy a live full-stack demo showcasing clean architecture, CI/CD pipeline, and clear README documentation.",
                    Arrays.asList("GitHub README Best Practices", "Free Vercel/Render Deployment Guides")
            ));
            return roadmap;
        }

        List<String> missingSkillNames = missing.stream().map(SkillGapAnalysisDto.SkillItem::getSkillName).toList();

        // Week 1
        String w1Skill = missingSkillNames.get(0);
        roadmap.add(new SkillGapAnalysisDto.RoadmapStep(
                "Week 1: Core Fundamentals",
                "Master " + w1Skill + " Essentials",
                w1Skill + " Syntax, Architecture, and Key Mental Models",
                "Complete official interactive tutorials and build 2 micro-projects from scratch focusing on " + w1Skill + ".",
                Arrays.asList(
                        "Official " + w1Skill + " Documentation & Quickstart",
                        "FreeCodeCamp Interactive " + w1Skill + " Bootcamp",
                        "roadmap.sh Developer Roadmaps"
                )
        ));

        // Week 2
        String w2Skill = missingSkillNames.size() > 1 ? missingSkillNames.get(1) : w1Skill;
        roadmap.add(new SkillGapAnalysisDto.RoadmapStep(
                "Week 2: Practical Integration",
                "Integrate " + w2Skill + " into Full-Stack Application",
                "Hands-on Implementation & API Integration",
                "Develop a working module combining " + w1Skill + " and " + w2Skill + " with realistic error handling and unit tests.",
                Arrays.asList(
                        "GitHub Open-Source Examples for " + w2Skill,
                        "Baeldung / Dev.to practical guides",
                        "Postman API Testing Best Practices"
                )
        ));

        // Week 3
        roadmap.add(new SkillGapAnalysisDto.RoadmapStep(
                "Week 3: Portfolio & ATS Resume Optimization",
                "Deploy Capstone Project & Update SkillBridge Profile",
                "Production Deployment & ATS Keyword Alignment",
                "Document your projects on GitHub with architecture diagrams, update your profile skills on SkillBridge, and submit your application.",
                Arrays.asList(
                        "ATS Resume Formatting Checklist",
                        "GitHub Portfolio Showcase Template",
                        "Behavioral & Technical Mock Interview Rubric"
                )
        ));

        return roadmap;
    }

    private List<SkillGapAnalysisDto.InterviewQuestion> generateInterviewQuestions(
            List<SkillGapAnalysisDto.SkillItem> matched,
            List<SkillGapAnalysisDto.SkillItem> missing,
            Internship internship) {

        List<SkillGapAnalysisDto.InterviewQuestion> questions = new ArrayList<>();

        // Generate tailored questions based on matched strengths
        for (SkillGapAnalysisDto.SkillItem item : matched) {
            String name = item.getSkillName().toLowerCase();
            if (name.contains("react")) {
                questions.add(new SkillGapAnalysisDto.InterviewQuestion(
                        "How do you optimize rendering performance in React, and when would you use useMemo versus useCallback?",
                        "React.js",
                        "Evaluates practical understanding of React's virtual DOM reconciliation and re-render lifecycle.",
                        "Mention reference equality, dependency arrays, React.memo for component memoization, and avoiding premature optimization."
                ));
            } else if (name.contains("spring") || name.contains("java")) {
                questions.add(new SkillGapAnalysisDto.InterviewQuestion(
                        "Explain Spring Boot's Dependency Injection and how Bean Scopes work in a high-concurrency REST API.",
                        "Spring Boot",
                        "Tests core backend engineering principles, IoC container mechanics, and thread safety.",
                        "Discuss Singleton vs Prototype scope, @Autowired / Constructor injection, and ensuring stateless Service beans."
                ));
            } else if (name.contains("sql") || name.contains("database") || name.contains("postgres")) {
                questions.add(new SkillGapAnalysisDto.InterviewQuestion(
                        "How would you diagnose and optimize a slow query running in a relational database?",
                        "SQL / Database",
                        "Critical for real-world backend and data workflows.",
                        "Explain EXPLAIN ANALYZE, B-Tree indexes, composite indexing, eliminating N+1 queries, and database connection pooling."
                ));
            } else if (name.contains("docker") || name.contains("cloud") || name.contains("aws")) {
                questions.add(new SkillGapAnalysisDto.InterviewQuestion(
                        "What is the difference between containerization with Docker and traditional virtualization?",
                        "DevOps / Cloud",
                        "Assesses modern deployment and infrastructure readiness.",
                        "Explain OS kernel sharing, lightweight container runtimes, immutable image layers, and multi-stage Docker builds."
                ));
            } else if (name.contains("python") || name.contains("machine learning") || name.contains("ai")) {
                questions.add(new SkillGapAnalysisDto.InterviewQuestion(
                        "How do you address overfitting when training a predictive model, and how do you evaluate model metrics?",
                        "AI & Machine Learning",
                        "Core competency check for data science and AI roles.",
                        "Mention cross-validation, regularization (L1/L2), dropout, precision vs recall, ROC-AUC, and data augmentation."
                ));
            }
        }

        // Add targeted questions for missing skills that recruiters might probe
        for (SkillGapAnalysisDto.SkillItem item : missing) {
            if (questions.size() >= 5) break;
            questions.add(new SkillGapAnalysisDto.InterviewQuestion(
                    "While " + item.getSkillName() + " is listed in our requirements, how would you ramp up and apply your transferable skills to deliver value with it quickly?",
                    item.getSkillName() + " (Growth Mindset)",
                    "Recruiters test how quickly and resourcefully you learn unfamiliar technologies on the job.",
                    "Highlight your learning methodology, relevant foundational similarities, and recent examples where you picked up a new tool in under 2 weeks."
            ));
        }

        // Default question if list is small
        if (questions.size() < 3) {
            questions.add(new SkillGapAnalysisDto.InterviewQuestion(
                    "Walk us through a challenging bug or architecture problem you encountered in a recent project and how you solved it.",
                    "Problem Solving & Engineering Ownership",
                    "Evaluates analytical troubleshooting and root cause analysis.",
                    "Use the STAR method (Situation, Task, Action, Result) with specific technical metrics and lessons learned."
            ));
        }

        return questions;
    }

    private String normalizeSkill(String skill) {
        if (skill == null) return "";
        String clean = skill.trim().toLowerCase();
        return SKILL_ALIASES.getOrDefault(clean, clean);
    }
}
