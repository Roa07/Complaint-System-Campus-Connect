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
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.time.LocalDate;
import com.example.complaint.entity.TimetableEntry;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.example.complaint.repository.TimetableEntryRepository timetableRepo;

    @GetMapping("/dashboard")
    public String studentDashboard(Model model, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        
        // Students see only their own complaints
        List<Complaint> complaints = complaintRepository.findByUser(user);
        
        long totalComplaints = complaints.size();
        long resolvedComplaints = complaints.stream().filter(c -> c.getStatus() == ComplaintStatus.APPROVED).count();
        long pendingComplaints = complaints.stream().filter(c -> c.getStatus() == ComplaintStatus.PENDING).count();

        // Load Student specific timetable entries matching their registered department, course, year, semester, and division
        List<TimetableEntry> timetable = timetableRepo.findByDepartmentAndCourseAndYearAndSemesterAndDivision(
                user.getDepartment(), user.getCourse(), user.getYear(), user.getSemester(), user.getDivision());

        // Filter today's timetable entries
        String dayOfWeek = LocalDate.now().getDayOfWeek().name();
        String dayStr = dayOfWeek.substring(0, 1) + dayOfWeek.substring(1).toLowerCase(); // e.g. Monday
        List<TimetableEntry> todayTimetable = timetable.stream()
                .filter(e -> dayStr.equalsIgnoreCase(e.getDay()))
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("user", user);
        model.addAttribute("complaints", complaints);
        model.addAttribute("totalComplaints", totalComplaints);
        model.addAttribute("resolvedComplaints", resolvedComplaints);
        model.addAttribute("pendingComplaints", pendingComplaints);
        model.addAttribute("timetable", timetable);
        model.addAttribute("todayTimetable", todayTimetable);
        
        return "student-dashboard";
    }
}
