package com.example.complaint.repository;

import com.example.complaint.entity.Complaint;
import com.example.complaint.entity.User;
import com.example.complaint.enums.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    // This naming convention tells Spring Data JPA to generate the SQL
    // to fetch complaints and sort them by the 'createdAt' field automatically.
    List<Complaint> findAllByOrderByCreatedAtDesc();
    List<Complaint> findByUser(User user);
    List<Complaint> findByStatus(ComplaintStatus status);
    void deleteById(long id);
}