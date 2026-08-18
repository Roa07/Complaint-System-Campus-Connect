package com.example.complaint.controller;

import com.example.complaint.entity.DiaryEntry;
import com.example.complaint.entity.User;
import com.example.complaint.repository.DiaryRepository;
import com.example.complaint.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/diary")
public class DiaryRestController {

    @Autowired
    private DiaryRepository diaryRepo;

    @Autowired
    private UserRepository userRepo;

    @PostMapping("/autosave")
    public ResponseEntity<?> autosaveDraft(@RequestBody Map<String, Object> payload, Authentication auth) {
        User teacher = userRepo.findByEmail(auth.getName()).orElseThrow();
        
        DiaryEntry entry = new DiaryEntry();
        if (payload.get("id") != null) {
            Long entryId = Long.valueOf(payload.get("id").toString());
            entry = diaryRepo.findById(entryId).orElse(entry);
        }
        
        entry.setTeacher(teacher);
        entry.setDate(LocalDate.now());
        entry.setSubject((String) payload.get("subject"));
        entry.setTopicCovered((String) payload.get("topicCovered"));
        entry.setLearningOutcome((String) payload.get("learningOutcome"));
        entry.setClassName((String) payload.get("className"));
        entry.setDivision((String) payload.get("division"));
        entry.setSemester((String) payload.get("semester"));
        entry.setRemarks((String) payload.get("remarks"));
        entry.setDraft(true);
        
        DiaryEntry saved = diaryRepo.save(entry);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("id", saved.getId());
        response.put("message", "Draft auto-saved successfully!");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/predict")
    public ResponseEntity<?> getSyllabusPrediction(@RequestParam String subject, Authentication auth) {
        // Mocking AI predict output as requested:
        // "Current progress indicates the syllabus will finish 5 days late. Conducting two additional lectures can bring the schedule back on track."
        Map<String, Object> response = new HashMap<>();
        response.put("subject", subject);
        response.put("syllabusCompletedPercentage", 72);
        response.put("status", "At Risk (5 Days Late)");
        response.put("predictionText", "Current progress indicates the syllabus will finish 5 days late. Conducting two additional lectures can bring the schedule back on track.");
        response.put("aiRiskLevel", "Medium");
        return ResponseEntity.ok(response);
    }
}
