package com.example.complaint.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "timetable_entries")
@Data
public class TimetableEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String day;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    
    private String subject;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teacher_id")
    private User teacher;

    private String department;
    private String course;
    private String year;
    private String semester;
    private String division;
    private String classroom;
    private Integer lectureNumber;
}
