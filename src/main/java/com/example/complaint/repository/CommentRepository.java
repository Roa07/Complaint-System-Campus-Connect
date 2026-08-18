package com.example.complaint.repository;

import com.example.complaint.entity.Comment;
import com.example.complaint.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    void deleteByUser(User user);
}
