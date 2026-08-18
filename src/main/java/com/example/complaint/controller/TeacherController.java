package com.example.complaint.controller;

import com.example.complaint.entity.Complaint;
import com.example.complaint.entity.DiaryEntry;
import com.example.complaint.entity.SyllabusPlan;
import com.example.complaint.entity.SyllabusPrediction;
import com.example.complaint.entity.TimetableEntry;
import com.example.complaint.entity.User;
import com.example.complaint.enums.ComplaintStatus;
import com.example.complaint.repository.ComplaintRepository;
import com.example.complaint.repository.DiaryRepository;
import com.example.complaint.repository.SyllabusPlanRepository;
import com.example.complaint.repository.SyllabusPredictionRepository;
import com.example.complaint.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private SyllabusPlanRepository syllabusPlanRepo;

    @Autowired
    private SyllabusPredictionRepository predictionRepo;

    @Autowired
    private com.example.complaint.service.HistoryService historyService;

    @Autowired
    private com.example.complaint.repository.TimetableEntryRepository timetableRepo;

    @GetMapping("/dashboard")
    public String teacherDashboard(Model model, Authentication authentication,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                   @RequestParam(required = false) String subject) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        
        List<Complaint> complaints = complaintRepository.findAll();
        long activeComplaints = complaints.stream().filter(c -> c.getStatus() == ComplaintStatus.PENDING).count();
        long resolvedComplaints = complaints.stream().filter(c -> c.getStatus() == ComplaintStatus.APPROVED).count();
        
        List<DiaryEntry> diaryEntries;
        if (date != null) {
            diaryEntries = diaryRepository.findByTeacherAndDate(user, date);
        } else if (subject != null && !subject.trim().isEmpty()) {
            diaryEntries = diaryRepository.findByTeacherAndSubjectContainingIgnoreCase(user, subject);
        } else {
            diaryEntries = diaryRepository.findByTeacher(user);
        }
        
        long totalLecturesThisMonth = diaryEntries.stream().filter(d -> d.getDate() != null && d.getDate().getMonth() == LocalDate.now().getMonth()).count();
        List<SyllabusPlan> syllabusPlans = syllabusPlanRepo.findByTeacher(user);
        List<SyllabusPrediction> predictions = predictionRepo.findByTeacher(user);

        // Fetch scheduled lectures from timetable for this teacher
        List<TimetableEntry> scheduledLectures = timetableRepo.findByTeacher(user);

        // Filter for today's lectures
        String dayOfWeek = LocalDate.now().getDayOfWeek().name();
        String dayStr = dayOfWeek.substring(0, 1) + dayOfWeek.substring(1).toLowerCase(); // e.g. Monday
        List<TimetableEntry> todayLectures = scheduledLectures.stream()
                .filter(e -> dayStr.equalsIgnoreCase(e.getDay()))
                .collect(Collectors.toList());

        // Simple Syllabus Planning Analytics
        long totalRequired = syllabusPlans.stream().mapToLong(SyllabusPlan::getLecturesRequired).sum();
        long totalConducted = syllabusPlans.stream().mapToLong(SyllabusPlan::getLecturesConducted).sum();
        long availableLectures = scheduledLectures.size() * 12; // Assuming 12 weeks remaining in semester
        long delay = Math.max(0, totalRequired - totalConducted - availableLectures);

        model.addAttribute("user", user);
        model.addAttribute("complaints", complaints);
        model.addAttribute("activeComplaints", activeComplaints);
        model.addAttribute("resolvedComplaints", resolvedComplaints);
        
        model.addAttribute("diaryEntries", diaryEntries);
        model.addAttribute("totalLecturesThisMonth", totalLecturesThisMonth);
        model.addAttribute("newDiary", new DiaryEntry());
        model.addAttribute("syllabusPlans", syllabusPlans);
        model.addAttribute("predictions", predictions);
        model.addAttribute("todayLectures", todayLectures);
        model.addAttribute("scheduledLectures", scheduledLectures);
        model.addAttribute("availableLectures", availableLectures);
        model.addAttribute("requiredLectures", totalRequired);
        model.addAttribute("delayLectures", delay);

        return "teacher-dashboard";
    }

    @PostMapping("/diary/add")
    public String addDiaryEntry(@ModelAttribute DiaryEntry diaryEntry, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        diaryEntry.setTeacher(user);
        diaryEntry.setDraft(false);
        diaryRepository.save(diaryEntry);

        historyService.logTeacherActivity(user, "Diary entry submitted", "Logged lecture topic: " + diaryEntry.getTopicCovered(), diaryEntry.getSubject(), user.getDepartment(), diaryEntry.getClassName(), diaryEntry.getSemester());
        historyService.logTeacherActivity(user, "Lecture completed", "Conducted lecture for topic " + diaryEntry.getTopicCovered(), diaryEntry.getSubject(), user.getDepartment(), diaryEntry.getClassName(), diaryEntry.getSemester());
        historyService.logTeacherActivity(user, "Attendance marked", "Recorded " + diaryEntry.getAttendance() + " students present", diaryEntry.getSubject(), user.getDepartment(), diaryEntry.getClassName(), diaryEntry.getSemester());
        
        // Auto-increment conducted lecture count in the syllabus plan matching the subject
        List<SyllabusPlan> plans = syllabusPlanRepo.findByTeacher(user);
        for (SyllabusPlan plan : plans) {
            if (plan.getSubject().equalsIgnoreCase(diaryEntry.getSubject()) && !plan.getStatus().equalsIgnoreCase("Completed")) {
                plan.setLecturesConducted(plan.getLecturesConducted() + 1);
                int progress = (int) (((double) plan.getLecturesConducted() / plan.getLecturesRequired()) * 100);
                plan.setProgressPercentage(Math.min(progress, 100));
                if (plan.getProgressPercentage() >= 100) {
                    plan.setStatus("Completed");
                }
                syllabusPlanRepo.save(plan);
            }
        }
        return "redirect:/teacher/dashboard?success";
    }

    @PostMapping("/diary/delete/{id}")
    public String deleteDiaryEntry(@PathVariable Long id, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        DiaryEntry entry = diaryRepository.findById(id).orElseThrow();
        if (entry.getTeacher().getId().equals(user.getId())) {
            diaryRepository.delete(entry);
        }
        return "redirect:/teacher/dashboard?deleted";
    }

    @PostMapping("/syllabus/upload")
    public String uploadSyllabus(@RequestParam("file") MultipartFile file,
                                 @RequestParam("subject") String subject,
                                 Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        
        // Mock parsing file and saving 3 Units
        SyllabusPlan plan1 = new SyllabusPlan();
        plan1.setTeacher(user);
        plan1.setSubject(subject);
        plan1.setUnitName("Unit 1");
        plan1.setTopic("Introduction & Core Fundamentals");
        plan1.setLecturesRequired(8);
        plan1.setLecturesConducted(0);
        plan1.setStartDate(LocalDate.now());
        plan1.setEndDate(LocalDate.now().plusWeeks(2));
        syllabusPlanRepo.save(plan1);

        SyllabusPlan plan2 = new SyllabusPlan();
        plan2.setTeacher(user);
        plan2.setSubject(subject);
        plan2.setUnitName("Unit 2");
        plan2.setTopic("Advanced Implementations & Workflows");
        plan2.setLecturesRequired(12);
        plan2.setLecturesConducted(0);
        plan2.setStartDate(LocalDate.now().plusWeeks(2));
        plan2.setEndDate(LocalDate.now().plusWeeks(5));
        syllabusPlanRepo.save(plan2);

        // Save AI prediction suggestion
        SyllabusPrediction prediction = new SyllabusPrediction();
        prediction.setTeacher(user);
        prediction.setSubject(subject);
        prediction.setPredictedDelayDays(5);
        prediction.setRiskLevel("Medium");
        prediction.setRecoveryPlan("Conducting two additional lectures can bring the schedule back on track.");
        predictionRepo.save(prediction);

        historyService.logTeacherActivity(user, "Syllabus uploaded", "Uploaded syllabus outline for " + subject, subject, user.getDepartment(), "N/A", "N/A");

        return "redirect:/teacher/dashboard?uploadSuccess";
    }

    @PostMapping("/syllabus/update-status/{id}")
    public String updateSyllabusStatus(@PathVariable Long id, @RequestParam String status) {
        SyllabusPlan plan = syllabusPlanRepo.findById(id).orElseThrow();
        plan.setStatus(status);
        if (status.equalsIgnoreCase("Completed")) {
            plan.setLecturesConducted(plan.getLecturesRequired());
            plan.setProgressPercentage(100);
        }
        syllabusPlanRepo.save(plan);

        historyService.logTeacherActivity(plan.getTeacher(), "Syllabus reviewed", "Changed topic progress status to " + status + " for " + plan.getTopic(), plan.getSubject(), plan.getTeacher().getDepartment(), "N/A", "N/A");

        return "redirect:/teacher/dashboard?statusUpdated";
    }
}
