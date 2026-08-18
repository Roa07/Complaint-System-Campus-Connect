package com.example.complaint.controller;

import com.example.complaint.entity.*;
import com.example.complaint.repository.*;
import com.example.complaint.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class CollegeManagementController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TimetableEntryRepository timetableRepo;

    @Autowired
    private SyllabusPlanRepository syllabusPlanRepo;

    @Autowired
    private SyllabusPredictionRepository predictionRepo;

    @Autowired
    private AttendanceSessionRepository sessionRepo;

    @Autowired
    private StudentAttendanceRepository attendanceRepo;

    @Autowired
    private HistoryService historyService;

    // --- 1. HOD Timetable Management ---

    @GetMapping("/hod/timetable")
    public String hodTimetablePage(Model model, Authentication auth) {
        User hod = userRepo.findByEmail(auth.getName()).orElseThrow();
        List<TimetableEntry> entries = timetableRepo.findAll().stream()
                .filter(e -> e.getDepartment() != null && e.getDepartment().equalsIgnoreCase(hod.getDepartment()))
                .collect(Collectors.toList());

        model.addAttribute("hod", hod);
        model.addAttribute("entries", entries);
        model.addAttribute("newEntry", new TimetableEntry());
        model.addAttribute("teachers", userRepo.findByRole("ROLE_TEACHER"));
        return "hod-timetable";
    }

    @PostMapping("/hod/timetable/add")
    public String addTimetableEntry(@ModelAttribute TimetableEntry entry,
                                    @RequestParam Long teacherId,
                                    Authentication auth) {
        User hod = userRepo.findByEmail(auth.getName()).orElseThrow();
        User teacher = userRepo.findById(teacherId).orElseThrow();
        entry.setTeacher(teacher);
        entry.setDepartment(hod.getDepartment());
        timetableRepo.save(entry);

        historyService.logHODActivity(hod, "Timetable update", "Manually added timetable entry for " + entry.getSubject() + " (Teacher: " + teacher.getDisplayName() + ")", hod.getDepartment());
        return "redirect:/hod/timetable?success";
    }

    @PostMapping("/hod/timetable/upload")
    public String uploadTimetableFile(@RequestParam("file") MultipartFile file, Authentication auth) {
        User hod = userRepo.findByEmail(auth.getName()).orElseThrow();
        
        // Mock parsing timetable from file
        List<User> teachers = userRepo.findByRole("ROLE_TEACHER");
        User dummyTeacher = teachers.isEmpty() ? hod : teachers.get(0);

        TimetableEntry entry1 = new TimetableEntry();
        entry1.setDay("Monday");
        entry1.setStartTime(LocalTime.of(10, 0));
        entry1.setEndTime(LocalTime.of(11, 0));
        entry1.setSubject("Artificial Intelligence");
        entry1.setTeacher(dummyTeacher);
        entry1.setDepartment(hod.getDepartment());
        entry1.setCourse("MSc IMCA");
        entry1.setYear("SY");
        entry1.setSemester("Semester 2");
        entry1.setDivision("A");
        entry1.setClassroom("204");
        entry1.setLectureNumber(1);
        timetableRepo.save(entry1);

        TimetableEntry entry2 = new TimetableEntry();
        entry2.setDay("Monday");
        entry2.setStartTime(LocalTime.of(11, 0));
        entry2.setEndTime(LocalTime.of(12, 0));
        entry2.setSubject("DBMS");
        entry2.setTeacher(dummyTeacher);
        entry2.setDepartment(hod.getDepartment());
        entry2.setCourse("MSc IMCA");
        entry2.setYear("SY");
        entry2.setSemester("Semester 2");
        entry2.setDivision("A");
        entry2.setClassroom("205");
        entry2.setLectureNumber(2);
        timetableRepo.save(entry2);

        historyService.logHODActivity(hod, "Timetable upload", "Uploaded timetable configuration file " + file.getOriginalFilename(), hod.getDepartment());
        return "redirect:/hod/timetable?uploadSuccess";
    }

    @PostMapping("/hod/timetable/delete/{id}")
    public String deleteTimetableEntry(@PathVariable Long id, Authentication auth) {
        User hod = userRepo.findByEmail(auth.getName()).orElseThrow();
        timetableRepo.deleteById(id);
        historyService.logHODActivity(hod, "Timetable item deleted", "Removed timetable entry ID: " + id, hod.getDepartment());
        return "redirect:/hod/timetable?deleted";
    }

    // --- 2. HOD Syllabus Planning Upload ---

    @GetMapping("/hod/syllabus")
    public String hodSyllabusPage(Model model, Authentication auth) {
        User hod = userRepo.findByEmail(auth.getName()).orElseThrow();
        List<SyllabusPlan> plans = syllabusPlanRepo.findAll().stream()
                .filter(p -> p.getDepartment() != null && p.getDepartment().equalsIgnoreCase(hod.getDepartment()))
                .collect(Collectors.toList());

        model.addAttribute("hod", hod);
        model.addAttribute("plans", plans);
        model.addAttribute("teachers", userRepo.findByRole("ROLE_TEACHER"));
        return "hod-syllabus";
    }

    @PostMapping("/hod/syllabus/upload")
    public String uploadSyllabusFile(@RequestParam("file") MultipartFile file,
                                     @RequestParam String course,
                                     @RequestParam String semester,
                                     @RequestParam String subject,
                                     @RequestParam Long teacherId,
                                     Authentication auth) {
        User hod = userRepo.findByEmail(auth.getName()).orElseThrow();
        User teacher = userRepo.findById(teacherId).orElseThrow();

        // Save a mock Syllabus plan with units, topics, subtopics, practicals, teaching hours
        SyllabusPlan plan = new SyllabusPlan();
        plan.setTeacher(teacher);
        plan.setSubject(subject);
        plan.setDepartment(hod.getDepartment());
        plan.setCourse(course);
        plan.setSemester(semester);
        plan.setUnitName("Unit 1");
        plan.setTopic("Introduction and Core Concepts");
        plan.setSubtopics("Algorithms, Neurons, Supervised Learning");
        plan.setPracticalTopics("Python AI implementation basics");
        plan.setTotalTeachingHours(15);
        plan.setLecturesRequired(10);
        plan.setLecturesConducted(0);
        plan.setStartDate(LocalDate.now());
        plan.setEndDate(LocalDate.now().plusWeeks(3));
        plan.setStatus("On Track");
        syllabusPlanRepo.save(plan);

        historyService.logHODActivity(hod, "Syllabus reviewed", "Uploaded syllabus outline details for subject: " + subject + " (Teacher: " + teacher.getDisplayName() + ")", hod.getDepartment());
        return "redirect:/hod/syllabus?success";
    }

    // --- 3. QR Attendance Generation & Validations ---

    @GetMapping("/attendance/generate")
    public String generateAttendancePage(Model model, Authentication auth) {
        User loggedUser = userRepo.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("user", loggedUser);
        model.addAttribute("timetable", timetableRepo.findByTeacher(loggedUser));
        return "attendance-generate";
    }

    @PostMapping("/api/attendance/session")
    @ResponseBody
    public ResponseEntity<?> createAttendanceSession(@RequestParam String subject,
                                                     @RequestParam String course,
                                                     @RequestParam String division,
                                                     @RequestParam String semester,
                                                     @RequestParam Integer lectureNumber,
                                                     Authentication auth) {
        User teacher = userRepo.findByEmail(auth.getName()).orElseThrow();

        String token = UUID.randomUUID().toString();
        AttendanceSession session = new AttendanceSession();
        session.setSessionToken(token);
        session.setSubject(subject);
        session.setCourse(course);
        session.setDivision(division);
        session.setSemester(semester);
        session.setLectureNumber(lectureNumber);
        session.setDepartment(teacher.getDepartment());
        session.setTeacher(teacher);
        session.setGeneratedTime(LocalDateTime.now());
        session.setExpiryTime(LocalDateTime.now().plusHours(12)); // 12 Hours Validity
        sessionRepo.save(session);

        if (teacher.getRole().equals("ROLE_TEACHER")) {
            historyService.logTeacherActivity(teacher, "Attendance marked", "Generated unique 12-hour attendance QR session for " + subject, subject, teacher.getDepartment(), course, semester);
        } else {
            historyService.logHODActivity(teacher, "Attendance marked", "Generated attendance session QR for " + subject, teacher.getDepartment());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("validUntil", session.getExpiryTime().format(DateTimeFormatter.ofPattern("hh:mm a")));
        response.put("status", "Active");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/attendance/submit")
    @ResponseBody
    public ResponseEntity<?> submitScannedAttendance(@RequestParam String token, Authentication auth) {
        User student = userRepo.findByEmail(auth.getName()).orElseThrow();

        Optional<AttendanceSession> sessionOpt = sessionRepo.findBySessionToken(token);
        if (sessionOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid QR code token."));
        }

        AttendanceSession session = sessionOpt.get();

        // 1. QR Expiry check (12 Hours)
        if (LocalDateTime.now().isAfter(session.getExpiryTime()) || !"Active".equalsIgnoreCase(session.getStatus())) {
            session.setStatus("Expired");
            sessionRepo.save(session);
            return ResponseEntity.badRequest().body(Map.of("message", "QR Expired. This attendance QR has expired."));
        }

        // 2. Validate student information matches session
        if (student.getHod() == null || !student.getHod().getId().equals(session.getTeacher().getHod() != null ? session.getTeacher().getHod().getId() : session.getTeacher().getId())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Wrong HOD / Department context."));
        }

        if (!student.getCourse().equalsIgnoreCase(session.getCourse()) || !student.getDivision().equalsIgnoreCase(session.getDivision())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Wrong Class / Division matching."));
        }

        // 3. Prevent duplicate attendance
        if (attendanceRepo.findByStudentAndSession(student, session).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Attendance Already Marked for this lecture."));
        }

        StudentAttendance attendance = new StudentAttendance();
        attendance.setStudent(student);
        attendance.setSession(session);
        attendanceRepo.save(attendance);

        // Update syllabus planning conducted lectures automatically
        List<SyllabusPlan> plans = syllabusPlanRepo.findByTeacher(session.getTeacher());
        for (SyllabusPlan plan : plans) {
            if (plan.getSubject().equalsIgnoreCase(session.getSubject())) {
                plan.setLecturesConducted(plan.getLecturesConducted() + 1);
                int progress = (int) (((double) plan.getLecturesConducted() / plan.getLecturesRequired()) * 100);
                plan.setProgressPercentage(Math.min(progress, 100));
                syllabusPlanRepo.save(plan);
            }
        }

        historyService.logStudentActivity(student, "Attendance marked", "Successfully scanned QR to mark presence for " + session.getSubject(), session.getSubject(), student.getDepartment());

        return ResponseEntity.ok(Map.of("message", "Attendance Marked Successfully!"));
    }

    // --- 4. Attendance Monitoring Panels ---

    @GetMapping("/hod/attendance")
    public String hodAttendancePage(Model model, Authentication auth) {
        User hod = userRepo.findByEmail(auth.getName()).orElseThrow();
        List<AttendanceSession> sessions = sessionRepo.findByDepartment(hod.getDepartment());
        
        model.addAttribute("hod", hod);
        model.addAttribute("sessions", sessions);
        return "hod-attendance";
    }

    @GetMapping("/teacher/attendance")
    public String teacherAttendancePage(Model model, Authentication auth) {
        User teacher = userRepo.findByEmail(auth.getName()).orElseThrow();
        List<AttendanceSession> sessions = sessionRepo.findByTeacher(teacher);

        model.addAttribute("user", teacher);
        model.addAttribute("sessions", sessions);
        return "teacher-attendance";
    }

    @GetMapping("/hod/reports")
    public String hodReportsPage(Model model, Authentication auth) {
        User hod = userRepo.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("hod", hod);
        return "hod-reports";
    }
}
