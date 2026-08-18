package com.example.complaint.repository;

import com.example.complaint.entity.SyllabusPlan;
import com.example.complaint.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SyllabusPlanRepository extends JpaRepository<SyllabusPlan, Long> {
    List<SyllabusPlan> findByTeacher(User teacher);
}
