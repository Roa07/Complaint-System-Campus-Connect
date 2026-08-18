package com.example.complaint.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "syllabus_predictions")
@Data
public class SyllabusPrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    private String subject;
    private Integer predictedDelayDays = 0;
    private String riskLevel = "Low"; // Low, Medium, High
    private String recoveryPlan;
}
