package com.example.complaint.controller;

import com.example.complaint.entity.Complaint;
import com.example.complaint.repository.ComplaintRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ComplaintRestController {

    private final ComplaintRepository complaintRepository;

    // Constructor Injection
    public ComplaintRestController(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    /**
     * Fetches all complaints in descending order of creation.
     * Accessible at: http://localhost:8080/api/complaints
     */
    @GetMapping("/complaints")
    public List<Complaint> getAllComplaints() {
        // Ensure this method exists in your ComplaintRepository interface
        return complaintRepository.findAllByOrderByCreatedAtDesc();
    }
}