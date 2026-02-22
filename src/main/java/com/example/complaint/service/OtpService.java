package com.example.complaint.service;

import com.example.complaint.entity.OtpToken;
import com.example.complaint.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepo;

    @Autowired
    private EmailService emailService;

    public void generateAndSendOtp(String email) throws Exception {

        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

        OtpToken token = new OtpToken();
        token.setEmail(email);
        token.setOtp(otp);
        token.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        otpRepo.save(token);

        emailService.sendOtpEmail(email, otp);
    }

    public boolean validateOtp(String email, String otp){

        OtpToken token = otpRepo.findByEmail(email).orElse(null);

        if(token != null &&
                token.getOtp().equals(otp) &&
                token.getExpiryTime().isAfter(LocalDateTime.now())) {

            otpRepo.delete(token);
            return true;
        }

        return false;
    }
}
