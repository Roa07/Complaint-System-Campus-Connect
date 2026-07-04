package com.example.complaint.controller;

import com.example.complaint.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ComplaintRepository complaintRepo;

    @GetMapping("/complaints")
    public String allComplaints(Model model){

        model.addAttribute("complaints",
                complaintRepo.findAll());

        return "Admin/home";
    }
}
