package com.example.complaint.repository;

import com.example.complaint.entity.AttendanceSession;
import com.example.complaint.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AttendanceSessionRepository extends JpaRepository<AttendanceSession, Long> {
    Optional<AttendanceSession> findBySessionToken(String token);
    List<AttendanceSession> findByTeacher(User teacher);
    List<AttendanceSession> findByDepartment(String department);
}
