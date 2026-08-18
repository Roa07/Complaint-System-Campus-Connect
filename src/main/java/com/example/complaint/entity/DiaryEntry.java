package com.example.complaint.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "diary_entries")
@Data
public class DiaryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    private LocalDate date;
    private Integer lectureNumber;
    private String subject;
    private String topicCovered;
    private String learningOutcome;
    private String className;
    private String division;
    private String semester;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer attendance;
    private String teachingMethod;
    private String remarks;
    private String homework;
    private String assignmentGiven;
    private String nextLecturePlanning;
    private boolean draft;
}
