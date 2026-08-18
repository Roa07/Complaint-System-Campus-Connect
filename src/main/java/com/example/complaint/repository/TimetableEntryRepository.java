package com.example.complaint.repository;

import com.example.complaint.entity.TimetableEntry;
import com.example.complaint.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TimetableEntryRepository extends JpaRepository<TimetableEntry, Long> {
    List<TimetableEntry> findByDepartmentAndCourseAndYearAndSemesterAndDivision(
            String department, String course, String year, String semester, String division);

    List<TimetableEntry> findByTeacher(User teacher);
}
