package com.example.complaint.service;

import com.example.complaint.entity.*;
import com.example.complaint.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HistoryService {

    @Autowired
    private TeacherHistoryRepository teacherHistoryRepo;

    @Autowired
    private StudentHistoryRepository studentHistoryRepo;

    @Autowired
    private HODHistoryRepository hodHistoryRepo;

    @Autowired
    private AdminHistoryRepository adminHistoryRepo;

    public void logTeacherActivity(User user, String activityType, String description, String subject, String department, String className, String semester) {
        TeacherHistory history = new TeacherHistory();
        history.setUser(user);
        history.setActivityType(activityType);
        history.setDescription(description);
        history.setSubject(subject);
        history.setDepartment(department);
        history.setClassName(className);
        history.setSemester(semester);
        teacherHistoryRepo.save(history);
    }

    public void logStudentActivity(User user, String activityType, String description, String subject, String department) {
        StudentHistory history = new StudentHistory();
        history.setUser(user);
        history.setActivityType(activityType);
        history.setDescription(description);
        history.setSubject(subject);
        history.setDepartment(department);
        studentHistoryRepo.save(history);
    }

    public void logHODActivity(User user, String activityType, String description, String department) {
        HODHistory history = new HODHistory();
        history.setUser(user);
        history.setActivityType(activityType);
        history.setDescription(description);
        history.setDepartment(department);
        hodHistoryRepo.save(history);
    }

    public void logAdminActivity(User user, String activityType, String description, String department, String userRole) {
        AdminHistory history = new AdminHistory();
        history.setUser(user);
        history.setActivityType(activityType);
        history.setDescription(description);
        history.setDepartment(department);
        history.setUserRole(userRole);
        adminHistoryRepo.save(history);
    }
}
