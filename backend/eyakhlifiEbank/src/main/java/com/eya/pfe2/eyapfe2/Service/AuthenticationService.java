package com.eya.pfe2.eyapfe2.Service;

import com.eya.pfe2.eyapfe2.Models.Role;
import com.eya.pfe2.eyapfe2.Models.User;
import com.eya.pfe2.eyapfe2.Repository.UserRepository;
import com.eya.pfe2.eyapfe2.config.JwtTokenProvider;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public String authenticate(String email, String password) {
        try {
            log.info("Attempting to authenticate user with email: {}", email);
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.generateToken((UserDetails) authentication.getPrincipal());
            log.info("Authentication successful for user with email: {}", email);
            return token;
        } catch (BadCredentialsException e) {
            log.error("Authentication failed for email: {}. Bad credentials.", email, e);
            throw new RuntimeException("Invalid email or password.");
        } catch (Exception e) {
            log.error("Unexpected error during authentication for email: {}", email, e);
            throw new RuntimeException("Unexpected error during authentication.");
        }
    }

    public User register(String nom, String prenom, Date dateNaissance, String telephone, String email, String password) {
        if (userRepository.findByEmail(email).isPresent() || userRepository.findByTelephone(telephone).isPresent()) {
            throw new IllegalArgumentException("Email or telephone already in use");
        }

        User user = new User();
        user.setNom(nom);
        user.setPrenom(prenom);
        user.setDateNaissance(dateNaissance);
        user.setTelephone(telephone);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.CLIENT);

        return userRepository.save(user);
    }

    @PostConstruct
    private void createDefaultAdmin() {
        Optional<User> admin = userRepository.findByRole(Role.ADMIN);
        if (!admin.isPresent()) {
            User defaultAdmin = new User();
            defaultAdmin.setNom("Default");
            defaultAdmin.setPrenom("Admin");
            defaultAdmin.setDateNaissance(new Date());
            defaultAdmin.setTelephone("123456789");
            defaultAdmin.setEmail("admin@ebank.com");
            defaultAdmin.setPassword(passwordEncoder.encode("admin"));
            defaultAdmin.setRole(Role.ADMIN);

            userRepository.save(defaultAdmin);
        }
    }
}