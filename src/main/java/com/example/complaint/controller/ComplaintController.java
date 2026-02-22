package com.example.complaint.controller;

import com.example.complaint.entity.*;
import com.example.complaint.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class ComplaintController {

    @Autowired
    private ComplaintRepository complaintRepo;

    @Autowired
    private UserRepository userRepo;
//
//    @GetMapping("/dashboard")
//    public String dashboard(Model model, Principal principal){
//
//        User user = userRepo.findByEmail(principal.getName()).get();
//
//        model.addAttribute("complaints",
//                complaintRepo.findByUser(user));
//
//        model.addAttribute("complaint", new Complaint());
//
//        return "dashboard";
//    }

    @PostMapping("/complaint")
    public String submitComplaint(@ModelAttribute Complaint complaint,
                                  Principal principal){

        User user = userRepo.findByEmail(principal.getName()).get();

        complaint.setStatus("PENDING");
        complaint.setUser(user);

        complaintRepo.save(complaint);

        return "redirect:/dashboard";
    }
}
