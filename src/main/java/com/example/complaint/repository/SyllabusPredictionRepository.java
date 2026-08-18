package com.example.complaint.repository;

import com.example.complaint.entity.SyllabusPrediction;
import com.example.complaint.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SyllabusPredictionRepository extends JpaRepository<SyllabusPrediction, Long> {
    List<SyllabusPrediction> findByTeacher(User teacher);
}
