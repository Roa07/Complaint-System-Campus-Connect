package com.example.complaint.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String middleName;
    private String lastName;

    @Transient
    private String fullName;

    @Column(unique = true)
    private String email;

    private String password;

    private String role;

    private String profileImage;
    private boolean enabled;
    
    private String department;
    private String phone;

    // Student specific
    private String year;
    private String division;
    private String rollNumber;
    private String semester;
    private String academicYear;
    private String course;

    // Teacher & HOD specific
    private String designation;
    private String employeeId;
    private Integer experience;
    private String subject;

    // Relationship: Student/Teacher -> HOD
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hod_id")
    private User hod;

    public String getDisplayName() {
        String first = clean(firstName);
        String middle = clean(middleName);
        String last = clean(lastName);
        String name = (first + " " + middle + " " + last).replaceAll("\\s+", " ").trim();

        if (!name.isEmpty()) {
            return name;
        }

        String fallbackEmail = clean(email);
        return fallbackEmail.isEmpty() ? "User" : fallbackEmail;
    }

    public String getInitials() {
        String first = firstLetter(firstName);
        String last = firstLetter(lastName);
        String initials = (first + last).trim();

        if (!initials.isEmpty()) {
            return initials.toUpperCase();
        }

        String displayName = getDisplayName();
        String[] parts = displayName.split("\\s+");
        if (parts.length >= 2) {
            return (firstLetter(parts[0]) + firstLetter(parts[parts.length - 1])).toUpperCase();
        }

        String fallback = firstLetter(displayName);
        return fallback.isEmpty() ? "U" : fallback.toUpperCase();
    }

    public void applyFullName() {
        String name = clean(fullName);
        if (name.isEmpty()) {
            return;
        }

        String[] parts = name.split("\\s+");
        firstName = parts[0];

        if (parts.length == 1) {
            middleName = "";
            lastName = "";
            return;
        }

        lastName = parts[parts.length - 1];

        if (parts.length > 2) {
            StringBuilder middle = new StringBuilder();
            for (int i = 1; i < parts.length - 1; i++) {
                if (!middle.isEmpty()) {
                    middle.append(" ");
                }
                middle.append(parts[i]);
            }
            middleName = middle.toString();
        } else {
            middleName = "";
        }
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private String firstLetter(String value) {
        String cleanValue = clean(value);
        return cleanValue.isEmpty() ? "" : cleanValue.substring(0, 1);
    }
}
