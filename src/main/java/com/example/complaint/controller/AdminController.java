package com.example.complaint.controller;

import com.example.complaint.entity.Complaint;
import com.example.complaint.entity.User;
import com.example.complaint.enums.ComplaintStatus;
import com.example.complaint.repository.CommentRepository;
import com.example.complaint.repository.ComplaintRepository;
import com.example.complaint.repository.LikeRepository;
import com.example.complaint.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LikeRepository likeRepository;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElse(null);
        List<Complaint> complaints = complaintRepository.findAll();
        
        long totalComplaints = complaints.size();
        long pendingComplaints = complaints.stream().filter(c -> c.getStatus() == ComplaintStatus.PENDING).count();
        long resolvedComplaints = complaints.stream().filter(c -> c.getStatus() == ComplaintStatus.APPROVED).count();
        
        model.addAttribute("user", user);
        model.addAttribute("userInitials", user == null ? "AD" : user.getInitials());
        model.addAttribute("userDisplayName", user == null ? "Admin" : user.getDisplayName());
        model.addAttribute("complaints", complaints);
        model.addAttribute("totalComplaints", totalComplaints);
        model.addAttribute("pendingComplaints", pendingComplaints);
        model.addAttribute("resolvedComplaints", resolvedComplaints);
        
        return "admin-dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(Model model, Authentication authentication) {
        User admin = userRepository.findByEmail(authentication.getName()).orElse(null);
        List<User> users = userRepository.findAll();

        long totalUsers = users.size();
        long studentCount = users.stream().filter(u -> "ROLE_STUDENT".equals(u.getRole()) || "ROLE_USER".equals(u.getRole())).count();
        long teacherCount = users.stream().filter(u -> "ROLE_TEACHER".equals(u.getRole())).count();
        long adminCount = users.stream().filter(u -> "ROLE_ADMIN".equals(u.getRole())).count();

        model.addAttribute("user", admin);
        model.addAttribute("userInitials", admin == null ? "AD" : admin.getInitials());
        model.addAttribute("userDisplayName", admin == null ? "Admin" : admin.getDisplayName());
        model.addAttribute("users", users);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("studentCount", studentCount);
        model.addAttribute("teacherCount", teacherCount);
        model.addAttribute("adminCount", adminCount);

        return "admin/users";
    }

    @PostMapping("/users/{id}/delete")
    @Transactional
    public String deleteUser(@PathVariable Long id,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        User currentAdmin = userRepository.findByEmail(authentication.getName()).orElse(null);
        User targetUser = userRepository.findById(id).orElse(null);

        if (targetUser == null) {
            redirectAttributes.addFlashAttribute("error", "User not found.");
            return "redirect:/admin/users";
        }

        if (currentAdmin != null && currentAdmin.getId().equals(targetUser.getId())) {
            redirectAttributes.addFlashAttribute("error", "You cannot delete your own admin account.");
            return "redirect:/admin/users";
        }

        likeRepository.deleteByUser(targetUser);
        commentRepository.deleteByUser(targetUser);

        List<Complaint> touchedComplaints = complaintRepository.findAll().stream()
                .filter(complaint -> isSameUser(complaint.getUser(), targetUser)
                        || isSameUser(complaint.getAssignedTeacher(), targetUser))
                .peek(complaint -> {
                    if (complaint.getStatus() == null) {
                        complaint.setStatus(ComplaintStatus.PENDING);
                    }
                    if (isSameUser(complaint.getUser(), targetUser)) {
                        complaint.setUser(null);
                    }
                    if (isSameUser(complaint.getAssignedTeacher(), targetUser)) {
                        complaint.setAssignedTeacher(null);
                    }
                })
                .toList();

        complaintRepository.saveAll(touchedComplaints);
        userRepository.delete(targetUser);

        redirectAttributes.addFlashAttribute("success", "User deleted successfully.");
        return "redirect:/admin/users";
    }

    @GetMapping("/assign")
    public String assignPage(Model model, Authentication authentication) {
        User admin = userRepository.findByEmail(authentication.getName()).orElse(null);
        List<Complaint> complaints = complaintRepository.findAll();
        List<User> teachers = userRepository.findAll().stream()
                .filter(u -> "ROLE_TEACHER".equals(u.getRole()))
                .toList();

        long pendingCount = complaints.stream().filter(c -> c.getStatus() == ComplaintStatus.PENDING).count();
        long assignedCount = complaints.stream().filter(c -> c.getAssignedTeacher() != null && c.getStatus() == ComplaintStatus.PENDING).count();
        long resolvedCount = complaints.stream().filter(c -> c.getStatus() == ComplaintStatus.APPROVED).count();

        model.addAttribute("user", admin);
        model.addAttribute("complaints", complaints);
        model.addAttribute("teachers", teachers);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("assignedCount", assignedCount);
        model.addAttribute("resolvedCount", resolvedCount);

        return "admin/assign";
    }

    @PostMapping("/assign/submit")
    public String submitAssignment(@RequestParam Long complaintId,
                                   @RequestParam Long teacherId,
                                   @RequestParam String priority,
                                   @RequestParam String dueDate) {
        Complaint complaint = complaintRepository.findById(complaintId).orElseThrow();
        User teacher = userRepository.findById(teacherId).orElseThrow();

        complaint.setAssignedTeacher(teacher);
        complaint.setPriority(priority);
        complaint.setDueDate(LocalDate.parse(dueDate).atStartOfDay());
        complaintRepository.save(complaint);

        return "redirect:/admin/assign";
    }

    @GetMapping("/reports")
    public String reportsPage(Model model, Authentication authentication) {
        User admin = userRepository.findByEmail(authentication.getName()).orElse(null);
        List<Complaint> complaints = complaintRepository.findAll();

        long totalComplaints = complaints.size();
        long pendingCount = complaints.stream().filter(c -> c.getStatus() == ComplaintStatus.PENDING).count();
        long resolvedCount = complaints.stream().filter(c -> c.getStatus() == ComplaintStatus.APPROVED).count();
        long rejectedCount = complaints.stream().filter(c -> c.getStatus() == ComplaintStatus.REJECTED).count();

        model.addAttribute("user", admin);
        model.addAttribute("complaints", complaints);
        model.addAttribute("totalComplaints", totalComplaints);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("resolvedCount", resolvedCount);
        model.addAttribute("rejectedCount", rejectedCount);

        return "admin/reports";
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

    private boolean isSameUser(User left, User right) {
        return left != null && right != null && left.getId() != null && left.getId().equals(right.getId());
    }
}
