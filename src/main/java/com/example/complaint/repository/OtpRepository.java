package com.example.complaint.repository;

import com.example.complaint.entity.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpToken, Long> {
    Optional<OtpToken> findByEmail(String email);
    void deleteByEmail(String email);
}
