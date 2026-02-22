package com.example.complaint.repository;

import com.example.complaint.entity.Complaint;
import com.example.complaint.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint,Long> {
    List<Complaint> findByUser(User user);
}
