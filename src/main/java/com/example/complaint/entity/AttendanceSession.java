package com.example.complaint.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_sessions")
@Data
public class AttendanceSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String sessionToken;

    private String subject;
    private String course;
    private String division;
    private String year;
    private String semester;
    private String department;
    
    private Integer lectureNumber;
    private LocalDateTime generatedTime;
    private LocalDateTime expiryTime; // 12 Hours validity
    private String status = "Active"; // Active, Expired

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teacher_id")
    private User teacher;
}
