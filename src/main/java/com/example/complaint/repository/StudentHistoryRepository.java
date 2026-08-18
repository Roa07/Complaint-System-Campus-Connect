package com.example.complaint.repository;

import com.example.complaint.entity.StudentHistory;
import com.example.complaint.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StudentHistoryRepository extends JpaRepository<StudentHistory, Long> {
    List<StudentHistory> findByUserAndDeleted(User user, boolean deleted);
    List<StudentHistory> findByUserAndStatusAndDeleted(User user, String status, boolean deleted);
}
