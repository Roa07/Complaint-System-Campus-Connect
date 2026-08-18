package com.example.complaint.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomAuthenticationSuccessHandler successHandler;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 1. Publicly accessible pages
                        .requestMatchers("/register", "/login", "/verify", "/forgot", "/api/auth/forgot/**", "/css/**", "/js/**").permitAll()

                        .requestMatchers("/home2").authenticated()
                        .requestMatchers("/profile").authenticated()
                        // 2. Role-based access control
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/teacher/**").hasRole("TEACHER")
                        .requestMatchers("/hod/**").hasRole("HOD")
                        .requestMatchers("/student/**").hasAnyRole("STUDENT", "USER")
                        .requestMatchers("/home").authenticated()
                        .requestMatchers("/dashboard").authenticated()

                        // 3. Everything else requires login
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(successHandler)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .rememberMe(remember -> remember
                        .key("uniqueAndSecretSmartDiary")
                        .tokenValiditySeconds(86400 * 30) // 30 Days validity
                        .rememberMeParameter("remember-me")
                );

        return http.build();
    }
}