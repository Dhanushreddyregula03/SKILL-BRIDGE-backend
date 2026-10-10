package com.skillbridge.repository;

import com.skillbridge.model.Internship;
import com.skillbridge.model.InternshipStatus;
import com.skillbridge.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InternshipRepository extends JpaRepository<Internship, Long> {
    List<Internship> findByStatusOrderByCreatedAtDesc(InternshipStatus status);
    List<Internship> findByEmployerOrderByCreatedAtDesc(User employer);
    
    @Query("SELECT DISTINCT i FROM Internship i LEFT JOIN i.requiredSkills s " +
           "WHERE i.status = :status AND (" +
           "LOWER(i.title) LIKE :pattern OR " +
           "LOWER(i.companyName) LIKE :pattern OR " +
           "LOWER(i.description) LIKE :pattern OR " +
           "LOWER(s.skillName) LIKE :pattern)")
    List<Internship> searchInternships(@Param("status") InternshipStatus status, @Param("pattern") String pattern);
}
