package com.example.complaint.repository;

import com.example.complaint.entity.DiaryEntry;
import com.example.complaint.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface DiaryRepository extends JpaRepository<DiaryEntry, Long> {
    List<DiaryEntry> findByTeacher(User teacher);
    List<DiaryEntry> findByTeacherAndDate(User teacher, LocalDate date);
    List<DiaryEntry> findByTeacherAndSubjectContainingIgnoreCase(User teacher, String subject);
    List<DiaryEntry> findByTeacherAndDraft(User teacher, boolean draft);
}
