package com.example.complaint.repository;

import com.example.complaint.entity.AdminHistory;
import com.example.complaint.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AdminHistoryRepository extends JpaRepository<AdminHistory, Long> {
    List<AdminHistory> findByUserAndDeleted(User user, boolean deleted);
    List<AdminHistory> findByUserAndStatusAndDeleted(User user, String status, boolean deleted);
    List<AdminHistory> findByDeleted(boolean deleted);
}
