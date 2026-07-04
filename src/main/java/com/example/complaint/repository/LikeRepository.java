package com.example.complaint.repository;

import com.example.complaint.entity.ComplaintLike;
import com.example.complaint.entity.Complaint;
import com.example.complaint.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<ComplaintLike, Long> {

    boolean existsByComplaintAndUser(Complaint complaint, User user);
}