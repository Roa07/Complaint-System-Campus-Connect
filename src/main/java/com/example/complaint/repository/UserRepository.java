package com.example.complaint.repository;

import com.example.complaint.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(String role);
    Optional<User> findByEmployeeId(String employeeId);
    Optional<User> findByRollNumber(String rollNumber);
    List<User> findByHod(User hod);
    List<User> findByHodAndRole(User hod, String role);
}
