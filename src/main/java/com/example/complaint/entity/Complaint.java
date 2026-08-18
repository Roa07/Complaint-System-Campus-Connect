package com.example.complaint.entity;

import com.example.complaint.enums.ComplaintStatus; // Added Enum import
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String blockName;
    private String description;
    private String location;
    private String image;

    // ✅ Added Status Field
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintStatus status = ComplaintStatus.PENDING;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name="assigned_teacher_id")
    private User assignedTeacher;

    private String category;
    private String priority;
    private LocalDateTime dueDate;
    private String resolutionNotes;
    private String feedback;

    @OneToMany(mappedBy = "complaint", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "complaint", cascade = CascadeType.ALL)
    private List<ComplaintLike> likes;

    @PrePersist
    @PreUpdate
    private void ensureDefaults() {
        if (status == null) {
            status = ComplaintStatus.PENDING;
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
