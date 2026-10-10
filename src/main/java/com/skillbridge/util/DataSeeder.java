package com.skillbridge.util;

import com.skillbridge.model.*;
import com.skillbridge.repository.ApplicationRepository;
import com.skillbridge.repository.InternshipRepository;
import com.skillbridge.repository.RecruiterProfileRepository;
import com.skillbridge.repository.StudentProfileRepository;
import com.skillbridge.repository.UserRepository;
import com.skillbridge.service.SkillMatchingService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final InternshipRepository internshipRepository;
    private final ApplicationRepository applicationRepository;
    private final PasswordEncoder passwordEncoder;
    private final SkillMatchingService skillMatchingService;

    public DataSeeder(UserRepository userRepository,
                      StudentProfileRepository studentProfileRepository,
                      RecruiterProfileRepository recruiterProfileRepository,
                      InternshipRepository internshipRepository,
                      ApplicationRepository applicationRepository,
                      PasswordEncoder passwordEncoder,
                      SkillMatchingService skillMatchingService) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.recruiterProfileRepository = recruiterProfileRepository;
        this.internshipRepository = internshipRepository;
        this.applicationRepository = applicationRepository;
        this.passwordEncoder = passwordEncoder;
        this.skillMatchingService = skillMatchingService;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return; // Data already seeded
        }

        // 1. Create Recruiters
        User recruiter1 = new User("recruiter@skillbridge.io", passwordEncoder.encode("password123"), "Marcus Vance", Role.ROLE_EMPLOYER);
        recruiter1.setPhone("+1-555-0199");
        recruiter1 = userRepository.save(recruiter1);

        RecruiterProfile rProfile1 = new RecruiterProfile(recruiter1, "Nexus Technologies");
        rProfile1.setCompanyWebsite("https://nexus-tech.io");
        rProfile1.setIndustry("Enterprise Cloud & AI Solutions");
        rProfile1.setCompanyLogo("https://images.unsplash.com/photo-1549923746-c502d488b3ea?w=128&auto=format&fit=crop&q=80");
        rProfile1.setLocation("San Francisco, CA (Remote-First)");
        rProfile1.setCompanyDescription("Nexus builds scalable cloud architectures and enterprise developer platforms for Fortune 500 teams.");
        recruiterProfileRepository.save(rProfile1);

        User recruiter2 = new User("sarah.recruiter@skillbridge.io", passwordEncoder.encode("password123"), "Sarah Jenkins", Role.ROLE_EMPLOYER);
        recruiter2.setPhone("+1-555-0144");
        recruiter2 = userRepository.save(recruiter2);

        RecruiterProfile rProfile2 = new RecruiterProfile(recruiter2, "CloudScale AI Labs");
        rProfile2.setCompanyWebsite("https://cloudscale.ai");
        rProfile2.setIndustry("Applied Artificial Intelligence & LLMs");
        rProfile2.setCompanyLogo("https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=128&auto=format&fit=crop&q=80");
        rProfile2.setLocation("Boston, MA / Remote");
        rProfile2.setCompanyDescription("Pioneering neuro-symbolic AI engines and real-time generative agents for automated engineering.");
        recruiterProfileRepository.save(rProfile2);

        // 2. Create Students
        User student1 = new User("alex.student@skillbridge.io", passwordEncoder.encode("password123"), "Alex Morgan", Role.ROLE_STUDENT);
        student1.setPhone("+1-555-0101");
        student1 = userRepository.save(student1);

        StudentProfile sProfile1 = new StudentProfile(student1);
        sProfile1.setHeadline("Full-Stack Developer | Spring Boot, React & Cloud Enthusiast");
        sProfile1.setUniversity("University of California, Berkeley");
        sProfile1.setDegree("B.S. in Computer Science");
        sProfile1.setCgpa(3.88);
        sProfile1.setGraduationYear(2027);
        sProfile1.setGithubUrl("https://github.com/alex-morgan-dev");
        sProfile1.setLinkedinUrl("https://linkedin.com/in/alex-morgan-dev");
        sProfile1.setPortfolioUrl("https://alexmorgan.dev");
        sProfile1.setBio("Passionate third-year software engineering student with proven project experience building high-throughput REST APIs and reactive web apps. Eager to solve challenging distributed systems problems.");
        sProfile1.setResumeText("Skills: Java, Spring Boot, React, JavaScript, SQL, PostgreSQL, REST APIs, Git, Docker, Microservices, HTML5, Tailwind CSS. Developed a collaborative document editor using WebSockets and Spring Boot. Built a high-concurrency e-commerce order processing pipeline with PostgreSQL.");

        sProfile1.addSkill(new StudentSkill("Java", ProficiencyLevel.ADVANCED, 24));
        sProfile1.addSkill(new StudentSkill("Spring Boot", ProficiencyLevel.ADVANCED, 18));
        sProfile1.addSkill(new StudentSkill("React", ProficiencyLevel.ADVANCED, 20));
        sProfile1.addSkill(new StudentSkill("JavaScript", ProficiencyLevel.ADVANCED, 30));
        sProfile1.addSkill(new StudentSkill("PostgreSQL", ProficiencyLevel.INTERMEDIATE, 15));
        sProfile1.addSkill(new StudentSkill("Docker", ProficiencyLevel.INTERMEDIATE, 10));
        sProfile1.addSkill(new StudentSkill("Git", ProficiencyLevel.EXPERT, 36));
        sProfile1.addSkill(new StudentSkill("REST API", ProficiencyLevel.EXPERT, 24));
        studentProfileRepository.save(sProfile1);

        User student2 = new User("priya.student@skillbridge.io", passwordEncoder.encode("password123"), "Priya Sharma", Role.ROLE_STUDENT);
        student2.setPhone("+1-555-0102");
        student2 = userRepository.save(student2);

        StudentProfile sProfile2 = new StudentProfile(student2);
        sProfile2.setHeadline("AI & Machine Learning Researcher | NLP & Computer Vision");
        sProfile2.setUniversity("Carnegie Mellon University");
        sProfile2.setDegree("B.S. in Artificial Intelligence");
        sProfile2.setCgpa(3.95);
        sProfile2.setGraduationYear(2026);
        sProfile2.setGithubUrl("https://github.com/priyasharma-ai");
        sProfile2.setLinkedinUrl("https://linkedin.com/in/priyasharma-ai");
        sProfile2.setBio("Undergraduate researcher focused on Natural Language Processing, Transformer architectures, and automated question-answering systems.");
        sProfile2.setResumeText("Skills: Python, PyTorch, Machine Learning, Deep Learning, NLP, HuggingFace, FastAPI, Pandas, NumPy, Scikit-Learn, Docker, Linux, SQL.");

        sProfile2.addSkill(new StudentSkill("Python", ProficiencyLevel.EXPERT, 36));
        sProfile2.addSkill(new StudentSkill("Machine Learning", ProficiencyLevel.ADVANCED, 24));
        sProfile2.addSkill(new StudentSkill("PyTorch", ProficiencyLevel.ADVANCED, 18));
        sProfile2.addSkill(new StudentSkill("NLP", ProficiencyLevel.ADVANCED, 16));
        sProfile2.addSkill(new StudentSkill("FastAPI", ProficiencyLevel.INTERMEDIATE, 12));
        sProfile2.addSkill(new StudentSkill("Docker", ProficiencyLevel.BEGINNER, 6));
        studentProfileRepository.save(sProfile2);

        // 3. Create Internships
        Internship int1 = new Internship();
        int1.setEmployer(recruiter1);
        int1.setTitle("Full-Stack Java & React Developer Intern");
        int1.setCompanyName("Nexus Technologies");
        int1.setCompanyLogo("https://images.unsplash.com/photo-1549923746-c502d488b3ea?w=128&auto=format&fit=crop&q=80");
        int1.setLocation("Remote");
        int1.setWorkMode(WorkMode.REMOTE);
        int1.setCategory("Software Engineering");
        int1.setDuration("6 Months");
        int1.setStipendMonthly(35000.0);
        int1.setDescription("Join our core product engineering squad to build next-generation cloud collaboration tools. You'll contribute to high-performance microservices and dynamic user interfaces used by thousands of engineering organizations.");
        int1.setResponsibilities("1. Design and maintain RESTful web services using Java 21 and Spring Boot.\n2. Build responsive user experiences using React and modern CSS.\n3. Write robust unit and integration tests.\n4. Participate in agile sprint planning and peer code reviews.");
        int1.setPerks("Certificate of Internship, Flexible Hours, Letter of Recommendation, Pre-Placement Offer (PPO) potential, MacBook Pro provided.");
        int1.setOpenings(3);
        int1.setStatus(InternshipStatus.ACTIVE);
        int1.setDeadline(LocalDate.now().plusDays(45));

        int1.addRequiredSkill(new InternshipSkill("Java", ProficiencyLevel.INTERMEDIATE, true, 5));
        int1.addRequiredSkill(new InternshipSkill("Spring Boot", ProficiencyLevel.INTERMEDIATE, true, 5));
        int1.addRequiredSkill(new InternshipSkill("React", ProficiencyLevel.INTERMEDIATE, true, 4));
        int1.addRequiredSkill(new InternshipSkill("PostgreSQL", ProficiencyLevel.BEGINNER, false, 3));
        int1.addRequiredSkill(new InternshipSkill("Docker", ProficiencyLevel.BEGINNER, false, 3));
        int1 = internshipRepository.save(int1);

        Internship int2 = new Internship();
        int2.setEmployer(recruiter2);
        int2.setTitle("AI & Machine Learning Research Intern");
        int2.setCompanyName("CloudScale AI Labs");
        int2.setCompanyLogo("https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=128&auto=format&fit=crop&q=80");
        int2.setLocation("Boston, MA");
        int2.setWorkMode(WorkMode.HYBRID);
        int2.setCategory("AI & Data Science");
        int2.setDuration("3 Months");
        int2.setStipendMonthly(45000.0);
        int2.setDescription("Work directly with PhD researchers and ML staff engineers to train, fine-tune, and deploy retrieval-augmented generation (RAG) pipelines and LLM evaluation benchmarks.");
        int2.setResponsibilities("1. Implement PyTorch training pipelines for specialized embedding models.\n2. Benchmark inference latency and quantization trade-offs.\n3. Publish technical findings and contribute to open-source model repositories.");
        int2.setPerks("GPU compute cluster access, Conference attendance sponsorship, Mentorship by senior research scientists, High-conversion PPO.");
        int2.setOpenings(2);
        int2.setStatus(InternshipStatus.ACTIVE);
        int2.setDeadline(LocalDate.now().plusDays(30));

        int2.addRequiredSkill(new InternshipSkill("Python", ProficiencyLevel.ADVANCED, true, 5));
        int2.addRequiredSkill(new InternshipSkill("Machine Learning", ProficiencyLevel.INTERMEDIATE, true, 5));
        int2.addRequiredSkill(new InternshipSkill("PyTorch", ProficiencyLevel.INTERMEDIATE, true, 4));
        int2.addRequiredSkill(new InternshipSkill("NLP", ProficiencyLevel.BEGINNER, false, 3));
        int2.addRequiredSkill(new InternshipSkill("Docker", ProficiencyLevel.BEGINNER, false, 2));
        int2 = internshipRepository.save(int2);

        Internship int3 = new Internship();
        int3.setEmployer(recruiter1);
        int3.setTitle("Cloud DevOps & Infrastructure Intern");
        int3.setCompanyName("Nexus Technologies");
        int3.setCompanyLogo("https://images.unsplash.com/photo-1549923746-c502d488b3ea?w=128&auto=format&fit=crop&q=80");
        int3.setLocation("Remote");
        int3.setWorkMode(WorkMode.REMOTE);
        int3.setCategory("Cloud & DevOps");
        int3.setDuration("4 Months");
        int3.setStipendMonthly(40000.0);
        int3.setDescription("Help automate our multi-region Kubernetes clusters, CI/CD pipelines, and observability telemetry with Prometheus and Grafana.");
        int3.setResponsibilities("1. Maintain Terraform scripts for cloud infrastructure provisioning.\n2. Enhance GitHub Actions CI/CD workflows.\n3. Monitor cluster health and optimize container resource limits.");
        int3.setPerks("AWS Certification vouchers, Flexible Schedule, Mentorship.");
        int3.setOpenings(2);
        int3.setStatus(InternshipStatus.ACTIVE);
        int3.setDeadline(LocalDate.now().plusDays(25));

        int3.addRequiredSkill(new InternshipSkill("Docker", ProficiencyLevel.INTERMEDIATE, true, 5));
        int3.addRequiredSkill(new InternshipSkill("Kubernetes", ProficiencyLevel.BEGINNER, true, 4));
        int3.addRequiredSkill(new InternshipSkill("AWS", ProficiencyLevel.BEGINNER, false, 4));
        int3.addRequiredSkill(new InternshipSkill("Linux", ProficiencyLevel.INTERMEDIATE, true, 3));
        int3.addRequiredSkill(new InternshipSkill("Git", ProficiencyLevel.INTERMEDIATE, false, 3));
        int3 = internshipRepository.save(int3);

        Internship int4 = new Internship();
        int4.setEmployer(recruiter2);
        int4.setTitle("Frontend Web Experience & UI Intern");
        int4.setCompanyName("CloudScale AI Labs");
        int4.setCompanyLogo("https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=128&auto=format&fit=crop&q=80");
        int4.setLocation("New York, NY");
        int4.setWorkMode(WorkMode.HYBRID);
        int4.setCategory("Software Engineering");
        int4.setDuration("3 Months");
        int4.setStipendMonthly(30000.0);
        int4.setDescription("Craft slick, lightning-fast web interfaces and interactive AI visualizations with React, Tailwind CSS, and WebGL.");
        int4.setResponsibilities("1. Implement interactive dashboard widgets with React and TypeScript.\n2. Ensure 100% responsiveness and accessibility across devices.\n3. Collaborate with product designers on user flow prototypes.");
        int4.setPerks("Design software licenses, Flexible working hours, Stipend + Performance Bonus.");
        int4.setOpenings(1);
        int4.setStatus(InternshipStatus.ACTIVE);
        int4.setDeadline(LocalDate.now().plusDays(20));

        int4.addRequiredSkill(new InternshipSkill("React", ProficiencyLevel.ADVANCED, true, 5));
        int4.addRequiredSkill(new InternshipSkill("JavaScript", ProficiencyLevel.ADVANCED, true, 5));
        int4.addRequiredSkill(new InternshipSkill("TypeScript", ProficiencyLevel.INTERMEDIATE, false, 4));
        int4.addRequiredSkill(new InternshipSkill("Tailwind CSS", ProficiencyLevel.INTERMEDIATE, false, 3));
        int4 = internshipRepository.save(int4);

        // 4. Create Sample Applications with pre-computed AI Match scores
        double scoreAlexInt1 = skillMatchingService.calculateMatchScore(sProfile1, int1);
        Application app1 = new Application(int1, student1, scoreAlexInt1, 
                "I have extensive hands-on experience building Spring Boot backends and React frontends, including full-stack microservices and PostgreSQL persistence. I would love to contribute to Nexus's cloud platform!");
        app1.setStatus(ApplicationStatus.SHORTLISTED);
        app1.setRecruiterNotes("Impressive match score (92%+). Candidate has exact tech stack alignment with Java and React. Scheduled for round 1 technical interview.");
        applicationRepository.save(app1);

        double scorePriyaInt2 = skillMatchingService.calculateMatchScore(sProfile2, int2);
        Application app2 = new Application(int2, student2, scorePriyaInt2, 
                "As an AI researcher at CMU focusing on NLP and PyTorch model fine-tuning, I am thrilled by CloudScale's mission in neuro-symbolic AI.");
        app2.setStatus(ApplicationStatus.INTERVIEW_SCHEDULED);
        app2.setRecruiterNotes("Top tier candidate with 95% match. Research experience in PyTorch and NLP directly aligns with our RAG pipeline team.");
        applicationRepository.save(app2);

        double scoreAlexInt3 = skillMatchingService.calculateMatchScore(sProfile1, int3);
        Application app3 = new Application(int3, student1, scoreAlexInt3, 
                "Eager to broaden my DevOps skills and container orchestration workflows with your cloud engineering squad.");
        app3.setStatus(ApplicationStatus.UNDER_REVIEW);
        app3.setRecruiterNotes("Good Docker knowledge. Minor gap in Kubernetes and AWS; checking with platform team lead.");
        applicationRepository.save(app3);
    }
}
