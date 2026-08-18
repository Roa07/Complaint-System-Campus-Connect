package com.example.complaint.controller;

import com.example.complaint.entity.*;
import com.example.complaint.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/history")
public class HistoryRestController {

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

    @PostMapping("/archive")
    public ResponseEntity<?> archiveHistory(@RequestParam String role, @RequestParam Long id, Authentication auth) {
        User loggedUser = userRepo.findByEmail(auth.getName()).orElseThrow();
        
        if (role.equals("ROLE_TEACHER")) {
            TeacherHistory th = teacherHistoryRepo.findById(id).orElseThrow();
            if (th.getUser().getId().equals(loggedUser.getId()) || loggedUser.getRole().equals("ROLE_ADMIN")) {
                th.setStatus("Archived");
                teacherHistoryRepo.save(th);
            }
        } else if (role.equals("ROLE_STUDENT")) {
            StudentHistory sh = studentHistoryRepo.findById(id).orElseThrow();
            if (sh.getUser().getId().equals(loggedUser.getId()) || loggedUser.getRole().equals("ROLE_ADMIN")) {
                sh.setStatus("Archived");
                studentHistoryRepo.save(sh);
            }
        } else if (role.equals("ROLE_HOD")) {
            HODHistory hh = hodHistoryRepo.findById(id).orElseThrow();
            if (hh.getUser().getId().equals(loggedUser.getId()) || loggedUser.getRole().equals("ROLE_ADMIN")) {
                hh.setStatus("Archived");
                hodHistoryRepo.save(hh);
            }
        } else if (role.equals("ROLE_ADMIN")) {
            AdminHistory ah = adminHistoryRepo.findById(id).orElseThrow();
            if (loggedUser.getRole().equals("ROLE_ADMIN")) {
                ah.setStatus("Archived");
                adminHistoryRepo.save(ah);
            }
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("message", "History entry archived successfully");
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/restore")
    public ResponseEntity<?> restoreHistory(@RequestParam String role, @RequestParam Long id, Authentication auth) {
        User loggedUser = userRepo.findByEmail(auth.getName()).orElseThrow();
        
        if (role.equals("ROLE_TEACHER")) {
            TeacherHistory th = teacherHistoryRepo.findById(id).orElseThrow();
            if (th.getUser().getId().equals(loggedUser.getId()) || loggedUser.getRole().equals("ROLE_ADMIN")) {
                th.setStatus("Active");
                teacherHistoryRepo.save(th);
            }
        } else if (role.equals("ROLE_STUDENT")) {
            StudentHistory sh = studentHistoryRepo.findById(id).orElseThrow();
            if (sh.getUser().getId().equals(loggedUser.getId()) || loggedUser.getRole().equals("ROLE_ADMIN")) {
                sh.setStatus("Active");
                studentHistoryRepo.save(sh);
            }
        } else if (role.equals("ROLE_HOD")) {
            HODHistory hh = hodHistoryRepo.findById(id).orElseThrow();
            if (hh.getUser().getId().equals(loggedUser.getId()) || loggedUser.getRole().equals("ROLE_ADMIN")) {
                hh.setStatus("Active");
                hodHistoryRepo.save(hh);
            }
        } else if (role.equals("ROLE_ADMIN")) {
            AdminHistory ah = adminHistoryRepo.findById(id).orElseThrow();
            if (loggedUser.getRole().equals("ROLE_ADMIN")) {
                ah.setStatus("Active");
                adminHistoryRepo.save(ah);
            }
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteHistory(@RequestParam String role, @RequestParam Long id, Authentication auth) {
        User loggedUser = userRepo.findByEmail(auth.getName()).orElseThrow();
        
        if (role.equals("ROLE_TEACHER")) {
            TeacherHistory th = teacherHistoryRepo.findById(id).orElseThrow();
            if (th.getUser().getId().equals(loggedUser.getId()) || loggedUser.getRole().equals("ROLE_ADMIN")) {
                th.setDeleted(true);
                teacherHistoryRepo.save(th);
            }
        } else if (role.equals("ROLE_STUDENT")) {
            StudentHistory sh = studentHistoryRepo.findById(id).orElseThrow();
            if (sh.getUser().getId().equals(loggedUser.getId()) || loggedUser.getRole().equals("ROLE_ADMIN")) {
                sh.setDeleted(true);
                studentHistoryRepo.save(sh);
            }
        } else if (role.equals("ROLE_HOD")) {
            HODHistory hh = hodHistoryRepo.findById(id).orElseThrow();
            if (hh.getUser().getId().equals(loggedUser.getId()) || loggedUser.getRole().equals("ROLE_ADMIN")) {
                hh.setDeleted(true);
                hodHistoryRepo.save(hh);
            }
        } else if (role.equals("ROLE_ADMIN")) {
            AdminHistory ah = adminHistoryRepo.findById(id).orElseThrow();
            if (loggedUser.getRole().equals("ROLE_ADMIN")) {
                ah.setDeleted(true);
                adminHistoryRepo.save(ah);
            }
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/bulk-delete")
    public ResponseEntity<?> bulkDelete(@RequestBody List<Long> ids, @RequestParam String role, Authentication auth) {
        for (Long id : ids) {
            deleteHistory(role, id, auth);
        }
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/bulk-archive")
    public ResponseEntity<?> bulkArchive(@RequestBody List<Long> ids, @RequestParam String role, Authentication auth) {
        for (Long id : ids) {
            archiveHistory(role, id, auth);
        }
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        return ResponseEntity.ok(resp);
    }
}
