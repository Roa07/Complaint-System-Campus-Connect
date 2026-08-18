package com.example.complaint.repository;

import com.example.complaint.entity.AttendanceSession;
import com.example.complaint.entity.StudentAttendance;
import com.example.complaint.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StudentAttendanceRepository extends JpaRepository<StudentAttendance, Long> {
    Optional<StudentAttendance> findByStudentAndSession(User student, AttendanceSession session);
    List<StudentAttendance> findBySession(AttendanceSession session);
}
