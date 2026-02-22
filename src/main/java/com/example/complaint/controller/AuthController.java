package com.example.complaint.controller;

import com.example.complaint.entity.User;
import com.example.complaint.repository.UserRepository;
import com.example.complaint.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private OtpService otpService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String registerPage(Model model){
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user,
                           @RequestParam String confirmPassword,
                           Model model) throws Exception {

        // 1. Validate Passwords match
        if(!user.getPassword().equals(confirmPassword)){
            model.addAttribute("error", "Passwords do not match");
            return "register";
        }

        // 2. Check if user already exists
        if(userRepo.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("error", "Email already registered!");
            return "register";
        }

        // 3. Prepare User Entity (Encoding and disabling until verified)
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(false);

        // Note: Middle name is handled automatically by @ModelAttribute
        // if your User entity has a field named 'middleName'.

        userRepo.save(user);

        // 4. Trigger OTP
        otpService.generateAndSendOtp(user.getEmail());

        // 5. Redirect to the verify page with the email parameter
        return "redirect:/verify?email=" + user.getEmail() + "&type=register";
    }

    @GetMapping("/verify")
    public String verifyPage(@RequestParam String email, Model model){
        model.addAttribute("email", email);
        return "verify";
    }

    @PostMapping("/verify")
    public String verifyOtp(@RequestParam String email,
                            @RequestParam String otp){

        if(otpService.validateOtp(email, otp)){
            User user = userRepo.findByEmail(email).get();
            user.setEnabled(true);
            userRepo.save(user);

            return "redirect:/login?verified";
        }

        return "redirect:/verify?email=" + email + "&error";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }
}