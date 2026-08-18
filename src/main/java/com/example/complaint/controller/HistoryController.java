package com.example.complaint.controller;

import com.example.complaint.entity.*;
import com.example.complaint.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HistoryController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TeacherHistoryRepository teacherHistoryRepo;

    @Autowired
    private StudentHistoryRepository studentHistoryRepo;

    @Autowired
    private HODHistoryRepository hodHistoryRepo;

    @Autowired
    private AdminHistoryRepository adminHistoryRepo;

    @GetMapping("/history")
    public String viewHistory(Model model, Authentication auth) {
        User user = userRepo.findByEmail(auth.getName()).orElseThrow();
        String role = user.getRole();
        
        List<?> activities = new ArrayList<>();
        long totalActivities = 0;
        long archivedRecords = 0;
        long deletedRecords = 0;
        long thisMonthActivities = 0;
        String lastActivityDate = "N/A";

        if (role.equals("ROLE_TEACHER")) {
            List<TeacherHistory> list = teacherHistoryRepo.findByUserAndDeleted(user, false);
            activities = list;
            totalActivities = list.size();
            archivedRecords = list.stream().filter(h -> h.getStatus().equals("Archived")).count();
            deletedRecords = teacherHistoryRepo.findAll().stream().filter(h -> h.getUser().getId().equals(user.getId()) && h.isDeleted()).count();
            thisMonthActivities = list.stream().filter(h -> h.getCreatedAt().getMonth() == LocalDateTime.now().getMonth()).count();
            lastActivityDate = list.isEmpty() ? "N/A" : list.get(0).getCreatedAt().toString();
        } else if (role.equals("ROLE_STUDENT")) {
            List<StudentHistory> list = studentHistoryRepo.findByUserAndDeleted(user, false);
            activities = list;
            totalActivities = list.size();
            archivedRecords = list.stream().filter(h -> h.getStatus().equals("Archived")).count();
            deletedRecords = studentHistoryRepo.findAll().stream().filter(h -> h.getUser().getId().equals(user.getId()) && h.isDeleted()).count();
            thisMonthActivities = list.stream().filter(h -> h.getCreatedAt().getMonth() == LocalDateTime.now().getMonth()).count();
            lastActivityDate = list.isEmpty() ? "N/A" : list.get(0).getCreatedAt().toString();
        } else if (role.equals("ROLE_HOD")) {
            List<HODHistory> list = hodHistoryRepo.findByUserAndDeleted(user, false);
            activities = list;
            totalActivities = list.size();
            archivedRecords = list.stream().filter(h -> h.getStatus().equals("Archived")).count();
            deletedRecords = hodHistoryRepo.findAll().stream().filter(h -> h.getUser().getId().equals(user.getId()) && h.isDeleted()).count();
            thisMonthActivities = list.stream().filter(h -> h.getCreatedAt().getMonth() == LocalDateTime.now().getMonth()).count();
            lastActivityDate = list.isEmpty() ? "N/A" : list.get(0).getCreatedAt().toString();
        } else if (role.equals("ROLE_ADMIN")) {
            List<AdminHistory> list = adminHistoryRepo.findByDeleted(false);
            activities = list;
            totalActivities = list.size();
            archivedRecords = list.stream().filter(h -> h.getStatus().equals("Archived")).count();
            deletedRecords = adminHistoryRepo.findAll().stream().filter(AdminHistory::isDeleted).count();
            thisMonthActivities = list.stream().filter(h -> h.getCreatedAt().getMonth() == LocalDateTime.now().getMonth()).count();
            lastActivityDate = list.isEmpty() ? "N/A" : list.get(0).getCreatedAt().toString();
        }

        model.addAttribute("user", user);
        model.addAttribute("role", role);
        model.addAttribute("historyList", activities);
        model.addAttribute("totalActivities", totalActivities);
        model.addAttribute("archivedRecords", archivedRecords);
        model.addAttribute("deletedRecords", deletedRecords);
        model.addAttribute("thisMonthActivities", thisMonthActivities);
        model.addAttribute("lastActivityDate", lastActivityDate);

        return "history";
    }
}
