package com.example.complaint.controller;

import com.example.complaint.entity.Complaint;
import com.example.complaint.entity.User;
import com.example.complaint.repository.ComplaintRepository;
import com.example.complaint.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard")
    public String studentDashboard(Model model, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        
        // Students see only their own complaints
        List<Complaint> complaints = complaintRepository.findByUser(user);
        
        model.addAttribute("user", user);
        model.addAttribute("complaints", complaints);
        
        return "student-dashboard";
    }
}
