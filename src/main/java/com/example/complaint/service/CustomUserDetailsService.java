package com.example.complaint.service;

import com.example.complaint.entity.User;
import com.example.complaint.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(),  // 🔥 blocks login if not verified
                true,
                true,
                true,
                Collections.singleton(
                        new org.springframework.security.core.authority
                                .SimpleGrantedAuthority(user.getRole())
                )
        );
    }
}
