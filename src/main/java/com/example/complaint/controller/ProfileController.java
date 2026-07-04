package com.example.complaint.controller;

import com.example.complaint.entity.User;
import com.example.complaint.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.UUID;

@Controller
public class ProfileController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public ProfileController(UserRepository userRepository,
                             BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ======================
    // VIEW PROFILE
    // ======================
    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {

        User user = userRepository
                .findByEmail(principal.getName())
                .orElse(null);

        model.addAttribute("user", user);
        return "profile";
    }

    // ======================
    // UPDATE PROFILE INFO
    // ======================
    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute User updatedUser,
                                @RequestParam("image") MultipartFile file,
                                Principal principal) throws IOException {

        User user = userRepository
                .findByEmail(principal.getName())
                .orElse(null);

        user.setFirstName(updatedUser.getFirstName());
        user.setMiddleName(updatedUser.getMiddleName());
        user.setLastName(updatedUser.getLastName());
        user.setEmail(updatedUser.getEmail());

        // Image Upload
        if (!file.isEmpty()) {

            String uploadDir = "uploads/profile/";
            File dir = new File(uploadDir);

            if (!dir.exists()) dir.mkdirs();

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            file.transferTo(new File(uploadDir + fileName));

            user.setProfileImage(fileName);
        }

        userRepository.save(user);

        return "redirect:/profile?updated";
    }

    // ======================
    // CHANGE PASSWORD
    // ======================
    @PostMapping("/profile/change-password")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Principal principal,
                                 Model model) {

        User user = userRepository
                .findByEmail(principal.getName())
                .orElse(null);

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            model.addAttribute("error", "Old password incorrect");
            model.addAttribute("user", user);
            return "profile";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            model.addAttribute("user", user);
            return "profile";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return "redirect:/profile?passwordChanged";
    }
}