package com.example.complaint.repository;

import com.example.complaint.entity.HODHistory;
import com.example.complaint.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HODHistoryRepository extends JpaRepository<HODHistory, Long> {
    List<HODHistory> findByUserAndDeleted(User user, boolean deleted);
    List<HODHistory> findByUserAndStatusAndDeleted(User user, String status, boolean deleted);
    List<HODHistory> findByDepartmentAndDeleted(String department, boolean deleted);
}
