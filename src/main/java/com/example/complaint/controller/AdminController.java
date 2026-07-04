package com.example.complaint.controller;

import com.example.complaint.entity.Complaint;
import com.example.complaint.entity.User;
import com.example.complaint.enums.ComplaintStatus;
import com.example.complaint.repository.ComplaintRepository;
import com.example.complaint.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElse(null);
        List<Complaint> complaints = complaintRepository.findAll();
        
        long totalComplaints = complaints.size();
        long pendingComplaints = complaints.stream().filter(c -> c.getStatus() == ComplaintStatus.PENDING).count();
        long resolvedComplaints = complaints.stream().filter(c -> c.getStatus() == ComplaintStatus.APPROVED).count();
        
        model.addAttribute("user", user);
        model.addAttribute("complaints", complaints);
        model.addAttribute("totalComplaints", totalComplaints);
        model.addAttribute("pendingComplaints", pendingComplaints);
        model.addAttribute("resolvedComplaints", resolvedComplaints);
        
        return "admin-dashboard";
    }

    @PostMapping("/approve/{id}")
    public String approveComplaint(@PathVariable Long id) {
        Complaint complaint = complaintRepository.findById(id).orElseThrow();
        complaint.setStatus(ComplaintStatus.APPROVED);
        complaintRepository.save(complaint);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/reject/{id}")
    public String rejectComplaint(@PathVariable Long id) {
        Complaint complaint = complaintRepository.findById(id).orElseThrow();
        complaint.setStatus(ComplaintStatus.REJECTED);
        complaintRepository.save(complaint);
        return "redirect:/admin/dashboard";
    }
}
