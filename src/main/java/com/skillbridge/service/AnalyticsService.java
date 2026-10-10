package com.skillbridge.service;

import com.skillbridge.dto.DashboardStatsDto;
import com.skillbridge.model.Application;
import com.skillbridge.model.ApplicationStatus;
import com.skillbridge.model.User;
import com.skillbridge.repository.ApplicationRepository;
import com.skillbridge.repository.InternshipRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    private final ApplicationRepository applicationRepository;
    private final InternshipRepository internshipRepository;

    public AnalyticsService(ApplicationRepository applicationRepository,
                            InternshipRepository internshipRepository) {
        this.applicationRepository = applicationRepository;
        this.internshipRepository = internshipRepository;
    }

    public DashboardStatsDto getStudentStats(User student) {
        List<Application> applications = applicationRepository.findByStudentOrderByAppliedAtDesc(student);
        DashboardStatsDto dto = new DashboardStatsDto();

        dto.setActiveApplications(applications.size());
        dto.setTotalInternships(internshipRepository.count());

        long shortlisted = applications.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.SHORTLISTED || 
                             a.getStatus() == ApplicationStatus.INTERVIEW_SCHEDULED || 
                             a.getStatus() == ApplicationStatus.OFFERED)
                .count();
        dto.setShortlistedCount(shortlisted);

        double avgScore = applications.stream()
                .mapToDouble(Application::getMatchScore)
                .average()
                .orElse(0.0);
        dto.setAverageMatchScore(Math.round(avgScore * 10.0) / 10.0);

        Map<String, Long> dist = new HashMap<>();
        for (Application a : applications) {
            dist.put(a.getStatus().name(), dist.getOrDefault(a.getStatus().name(), 0L) + 1);
        }
        dto.setStatusDistribution(dist);

        return dto;
    }

    public DashboardStatsDto getEmployerStats(User employer) {
        List<Application> applications = applicationRepository.findByInternshipEmployerOrderByAppliedAtDesc(employer);
        DashboardStatsDto dto = new DashboardStatsDto();

        long internshipCount = internshipRepository.findByEmployerOrderByCreatedAtDesc(employer).size();
        dto.setTotalInternships(internshipCount);
        dto.setActiveApplications(applications.size());

        long shortlisted = applications.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.SHORTLISTED || 
                             a.getStatus() == ApplicationStatus.INTERVIEW_SCHEDULED || 
                             a.getStatus() == ApplicationStatus.OFFERED)
                .count();
        dto.setShortlistedCount(shortlisted);

        double avgScore = applications.stream()
                .mapToDouble(Application::getMatchScore)
                .average()
                .orElse(0.0);
        dto.setAverageMatchScore(Math.round(avgScore * 10.0) / 10.0);

        Map<String, Long> dist = new HashMap<>();
        for (Application a : applications) {
            dist.put(a.getStatus().name(), dist.getOrDefault(a.getStatus().name(), 0L) + 1);
        }
        dto.setStatusDistribution(dist);

        return dto;
    }
}
