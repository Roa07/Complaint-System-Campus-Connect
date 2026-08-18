package com.example.complaint.controller;

import com.example.complaint.entity.*;
import com.example.complaint.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/hod")
public class HODController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private DiaryRepository diaryRepo;

    @Autowired
    private SyllabusPlanRepository syllabusPlanRepo;

    @Autowired
    private SyllabusPredictionRepository predictionRepo;

    @Autowired
    private AttendanceSessionRepository sessionRepo;

    @Autowired
    private TimetableEntryRepository timetableRepo;

    @Autowired
    private HODHistoryRepository hodHistoryRepo;

    @Autowired
    private com.example.complaint.service.HistoryService historyService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        User hod = userRepo.findByEmail(auth.getName()).orElseThrow();
        
        List<User> teachers = userRepo.findByHodAndRole(hod, "ROLE_TEACHER");
        List<User> students = userRepo.findByHodAndRole(hod, "ROLE_STUDENT");
        
        long totalDiaryEntries = teachers.stream()
                .flatMap(t -> diaryRepo.findByTeacher(t).stream())
                .count();

        // Fetch syllabus plans and predictions for teachers under this HOD
        List<SyllabusPlan> deptSyllabusPlans = new ArrayList<>();
        List<SyllabusPrediction> deptPredictions = new ArrayList<>();
        for (User teacher : teachers) {
            deptSyllabusPlans.addAll(syllabusPlanRepo.findByTeacher(teacher));
            deptPredictions.addAll(predictionRepo.findByTeacher(teacher));
        }

        long totalSubjects = deptSyllabusPlans.stream().map(SyllabusPlan::getSubject).distinct().count();
        long activeQrSessions = sessionRepo.findByDepartment(hod.getDepartment()).stream()
                .filter(s -> "Active".equalsIgnoreCase(s.getStatus()))
                .count();

        // Filter timetable entries for today's lectures
        String dayOfWeek = LocalDate.now().getDayOfWeek().name();
        String dayStr = dayOfWeek.substring(0,1) + dayOfWeek.substring(1).toLowerCase(); // e.g. Monday
        List<TimetableEntry> todayLectures = timetableRepo.findAll().stream()
                .filter(e -> e.getDepartment() != null && e.getDepartment().equalsIgnoreCase(hod.getDepartment()) && dayStr.equalsIgnoreCase(e.getDay()))
                .collect(Collectors.toList());

        model.addAttribute("hod", hod);
        model.addAttribute("teachers", teachers);
        model.addAttribute("students", students);
        model.addAttribute("totalTeachers", teachers.size());
        model.addAttribute("totalStudents", students.size());
        model.addAttribute("totalSubjects", totalSubjects);
        model.addAttribute("activeQrSessions", activeQrSessions);
        model.addAttribute("todayLectures", todayLectures);
        model.addAttribute("totalDiaryEntries", totalDiaryEntries);
        model.addAttribute("deptSyllabusPlans", deptSyllabusPlans);
        model.addAttribute("deptPredictions", deptPredictions);
        
        return "hod-dashboard";
    }

    @GetMapping("/profile")
    public String profilePage(Model model, Authentication auth) {
        User hod = userRepo.findByEmail(auth.getName()).orElseThrow();
        List<User> teachers = userRepo.findByHodAndRole(hod, "ROLE_TEACHER");
        List<User> students = userRepo.findByHodAndRole(hod, "ROLE_STUDENT");

        model.addAttribute("hod", hod);
        model.addAttribute("teachersCount", teachers.size());
        model.addAttribute("studentsCount", students.size());
        return "hod-profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String firstName,
                                @RequestParam String lastName,
                                @RequestParam(required = false) String middleName,
                                @RequestParam String phone,
                                @RequestParam String designation,
                                @RequestParam String employeeId,
                                @RequestParam String email,
                                Authentication auth) {
        User hod = userRepo.findByEmail(auth.getName()).orElseThrow();
        hod.setFirstName(firstName);
        hod.setLastName(lastName);
        hod.setMiddleName(middleName);
        hod.setPhone(phone);
        hod.setDesignation(designation);
        hod.setEmployeeId(employeeId);
        hod.setEmail(email);
        userRepo.save(hod);

        historyService.logHODActivity(hod, "Profile details updated", "Updated HOD personal details and profile data.", hod.getDepartment());
        return "redirect:/hod/profile?updated";
    }

    @GetMapping("/settings")
    public String settingsPage(Model model, Authentication auth) {
        User hod = userRepo.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("hod", hod);
        return "hod-settings";
    }

    @GetMapping("/notifications")
    public String notificationsPage(Model model, Authentication auth) {
        User hod = userRepo.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("hod", hod);
        return "hod-notifications";
    }

    @PostMapping("/settings/save")
    public String saveSettings(Authentication auth) {
        User hod = userRepo.findByEmail(auth.getName()).orElseThrow();
        historyService.logHODActivity(hod, "Settings updated", "Saved academic and system preferences settings.", hod.getDepartment());
        return "redirect:/hod/settings?saved";
    }

    @GetMapping("/teachers")
    public String teachersPage(Model model, Authentication auth) {
        User hod = userRepo.findByEmail(auth.getName()).orElseThrow();
        List<User> teachers = userRepo.findByHodAndRole(hod, "ROLE_TEACHER");
        model.addAttribute("hod", hod);
        model.addAttribute("teachers", teachers);
        return "hod-teachers";
    }

    @GetMapping("/students")
    public String studentsPage(Model model, Authentication auth) {
        User hod = userRepo.findByEmail(auth.getName()).orElseThrow();
        List<User> students = userRepo.findByHodAndRole(hod, "ROLE_STUDENT");
        model.addAttribute("hod", hod);
        model.addAttribute("students", students);
        return "hod-students";
    }

    @GetMapping("/departments")
    public String departmentsPage(Model model, Authentication auth) {
        User hod = userRepo.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("hod", hod);
        return "hod-departments";
    }

    @GetMapping("/subjects")
    public String subjectsPage(Model model, Authentication auth) {
        User hod = userRepo.findByEmail(auth.getName()).orElseThrow();
        List<SyllabusPlan> plans = syllabusPlanRepo.findAll().stream()
                .filter(p -> p.getDepartment() != null && p.getDepartment().equalsIgnoreCase(hod.getDepartment()))
                .collect(Collectors.toList());
        model.addAttribute("hod", hod);
        model.addAttribute("plans", plans);
        return "hod-subjects";
    }

    @GetMapping("/classes")
    public String classesPage(Model model, Authentication auth) {
        User hod = userRepo.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("hod", hod);
        return "hod-classes";
    }

    @GetMapping("/history")
    public String historyPage(Model model, Authentication auth) {
        User hod = userRepo.findByEmail(auth.getName()).orElseThrow();
        List<HODHistory> list = hodHistoryRepo.findByUserAndDeleted(hod, false);
        model.addAttribute("hod", hod);
        model.addAttribute("historyList", list);
        return "hod-history";
    }

    @GetMapping("/teacher/{id}/diary")
    public String viewTeacherDiary(@PathVariable Long id, Model model, Authentication auth) {
        User hod = userRepo.findByEmail(auth.getName()).orElseThrow();
        User teacher = userRepo.findById(id).orElseThrow();
        
        // Security check
        if (teacher.getHod() == null || !teacher.getHod().getId().equals(hod.getId())) {
            return "redirect:/hod/dashboard?unauthorized";
        }
        
        List<DiaryEntry> diaryEntries = diaryRepo.findByTeacher(teacher);
        model.addAttribute("hod", hod);
        model.addAttribute("teacher", teacher);
        model.addAttribute("diaryEntries", diaryEntries);

        historyService.logHODActivity(hod, "Syllabus reviewed", "Reviewed Digital Diary of teacher " + teacher.getDisplayName(), hod.getDepartment());

        return "hod-teacher-diary";
    }

    @PostMapping("/teacher/{id}/deactivate")
    public String deactivateTeacher(@PathVariable Long id, Authentication auth) {
        User hod = userRepo.findByEmail(auth.getName()).orElseThrow();
        User teacher = userRepo.findById(id).orElseThrow();
        
        if (teacher.getHod() != null && teacher.getHod().getId().equals(hod.getId())) {
            teacher.setEnabled(false);
            userRepo.save(teacher);
            historyService.logHODActivity(hod, "Teacher approved", "Deactivated teacher account for " + teacher.getDisplayName(), hod.getDepartment());
        }
        return "redirect:/hod/dashboard?deactivated";
    }
}
