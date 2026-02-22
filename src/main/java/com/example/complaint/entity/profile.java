package com.example.complaint.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "profile")
@Data


public class profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String phone_no;

}
