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
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard")
    public String teacherDashboard(Model model, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElse(null);
        
        // Teachers see complaints assigned to them or their department (for now showing all for simplicity)
        List<Complaint> complaints = complaintRepository.findAll();
        
        model.addAttribute("user", user);
        model.addAttribute("complaints", complaints);
        
        return "teacher-dashboard";
    }
}
