package com.example.complaint.repository;

import com.example.complaint.entity.TeacherHistory;
import com.example.complaint.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TeacherHistoryRepository extends JpaRepository<TeacherHistory, Long> {
    List<TeacherHistory> findByUserAndDeleted(User user, boolean deleted);
    List<TeacherHistory> findByUserAndStatusAndDeleted(User user, String status, boolean deleted);
}
