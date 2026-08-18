package com.example.complaint.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "syllabus_plans")
@Data
public class SyllabusPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    private String subject;
    private String department;
    private String course;
    private String semester;
    private String unitName;
    private String topic;
    private String subtopics;
    private String practicalTopics;
    private Integer totalTeachingHours;
    private Integer lecturesRequired;
    private Integer lecturesConducted = 0;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status = "On Track"; // On Track, Delayed, Completed
    private Integer progressPercentage = 0;
}
