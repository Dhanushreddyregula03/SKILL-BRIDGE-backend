package com.skillbridge.repository;

import com.skillbridge.model.Application;
import com.skillbridge.model.Internship;
import com.skillbridge.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByStudentOrderByAppliedAtDesc(User student);
    List<Application> findByInternshipOrderByAppliedAtDesc(Internship internship);
    List<Application> findByInternshipEmployerOrderByAppliedAtDesc(User employer);
    Optional<Application> findByInternshipIdAndStudentId(Long internshipId, Long studentId);
    boolean existsByInternshipIdAndStudentId(Long internshipId, Long studentId);
    long countByInternshipId(Long internshipId);
    long countByStudentId(Long studentId);
}
