package com.example.complaint.controller;

// 1. Core Spring & Security Imports
import com.example.complaint.entity.Complaint;
import com.example.complaint.entity.ComplaintLike;
import com.example.complaint.entity.User;
import com.example.complaint.enums.ComplaintStatus;
import com.example.complaint.repository.ComplaintRepository;
import com.example.complaint.repository.LikeRepository;
import com.example.complaint.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

// 2. Java Utility Imports
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Controller
public class DashboardController {

    // 3. Repository Fields
    private final UserRepository userRepository;
    private final ComplaintRepository complaintRepository;
    private final LikeRepository likeRepository;

    // 4. Constructor Injection
    public DashboardController(UserRepository userRepository,
                               ComplaintRepository complaintRepository,
                               LikeRepository likeRepository) {
        this.userRepository = userRepository;
        this.complaintRepository = complaintRepository;
        this.likeRepository = likeRepository;
    }

    // 5. GET Mapping for Home Page
    @GetMapping("/")
    public String rootRedirect(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }
        
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        if (role.equals("ROLE_ADMIN")) {
            return "redirect:/admin/dashboard";
        } else if (role.equals("ROLE_TEACHER")) {
            return "redirect:/teacher/dashboard";
        } else {
            return "redirect:/student/dashboard";
        }
    }

    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {
        // NOTE: Ensure your findByEmail matches the return type here (Optional vs User)
        User user = userRepository.findByEmail(authentication.getName()).orElse(null);

        List<Complaint> complaints;

        if (user != null && user.getRole().equals("ROLE_ADMIN")) {
            // Admin sees all
            complaints = complaintRepository.findAll();
        } else {
            // User sees only their own
            complaints = complaintRepository.findByStatus(ComplaintStatus.APPROVED);
        }

        model.addAttribute("complaints", complaints);
        model.addAttribute("user", user);

        return "home";
    }

    // 6. GET Mapping for Complaint Submission Page
    @GetMapping("/home2")
    public String submit(Authentication authentication, Model model) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        model.addAttribute("user", user);
        return "home2";
    }

    @GetMapping("/complaint")
    public String complaintPage(Authentication authentication, Model model) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        model.addAttribute("user", user);
        return "complaint";
    }

    @GetMapping("/emergency")
    public String emergencyPage() {
        return "emergence";
    }

    @GetMapping("/fest")
    public String festPage() {
        return "fest";
    }

    // 7. POST Mapping for Processing the Complaint & Image
    @PostMapping("/submit")
    public String postComplaint(
            @RequestParam String description,
            @RequestParam String location,
            @RequestParam(required = false) String blockName,
            @RequestParam("image") MultipartFile file,
            Authentication authentication) throws IOException {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        Complaint complaint = new Complaint();
        complaint.setDescription(description);
        complaint.setLocation(location);
        complaint.setBlockName(blockName);
        complaint.setUser(user);
        complaint.setCreatedAt(LocalDateTime.now());

        // ✅ IMAGE UPLOAD LOGIC
        if (file != null && !file.isEmpty()) {
            String uploadDir = "src/main/resources/static/uploads/complaints/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            file.transferTo(new File(dir.getAbsolutePath() + File.separator + fileName));

            complaint.setImage(fileName);
        }

        complaintRepository.save(complaint);

        return "redirect:/home";
    }

    // 8. POST Mapping for Deleting a Complaint (Secure)
    @PostMapping("/complaints/{id}/delete")
    public String deleteComplaint(@PathVariable Long id, Authentication auth) {
        Complaint complaint = complaintRepository.findById(id).orElse(null);

        // Security check: Only delete if the logged-in user owns it
        if (complaint != null && complaint.getUser().getEmail().equals(auth.getName())) {
            complaintRepository.delete(complaint);
        }

        return "redirect:/home";
    }

    // 9. POST Mapping for Toggling a Like
    @PostMapping("/complaints/{id}/like")
    public String toggleLike(@PathVariable Long id, Authentication auth) {
        Complaint complaint = complaintRepository.findById(id).orElse(null);
        User user = userRepository.findByEmail(auth.getName()).orElse(null);

        if (complaint != null && user != null) {
            // Check if user already liked this complaint
            if (likeRepository.existsByComplaintAndUser(complaint, user)) {
                ComplaintLike existingLike = complaint.getLikes().stream()
                        .filter(like -> like.getUser().getId().equals(user.getId()))
                        .findFirst()
                        .orElse(null);
                if (existingLike != null) {
                    likeRepository.delete(existingLike);
                }
            } else {
                // If no, save a new like
                ComplaintLike newLike = new ComplaintLike();
                newLike.setComplaint(complaint);
                newLike.setUser(user);
                likeRepository.save(newLike);
            }
        }

        return "redirect:/home";
    }

}