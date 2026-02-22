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


    @Column(unique = true)
    private String email;

    private String password;

    private String role;

    private String profileImage;
    private boolean enabled;

}
