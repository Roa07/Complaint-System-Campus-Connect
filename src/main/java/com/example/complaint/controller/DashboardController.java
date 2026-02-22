package com.example.complaint.controller;

import com.example.complaint.entity.User;
import com.example.complaint.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final UserRepository userRepository;

    public DashboardController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/home")
    public String home(Authentication authentication, Model model) {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        model.addAttribute("user", user);

        return "home";   // loads home.html
    }
}
