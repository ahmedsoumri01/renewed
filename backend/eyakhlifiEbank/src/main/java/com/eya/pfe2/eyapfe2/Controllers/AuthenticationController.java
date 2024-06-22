package com.eya.pfe2.eyapfe2.Controllers;

import com.eya.pfe2.eyapfe2.Models.User;
import com.eya.pfe2.eyapfe2.Service.AuthenticationService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> authenticate(@RequestBody Map<String, String> request) {
        try {
            System.out.println("eureur here " + request);
            String token = authenticationService.authenticate(request.get("email"), request.get("password"));
            return ResponseEntity.ok(Map.of("token", token));
        } catch (RuntimeException e) {
            log.error("Login failed for email: {}", request.get("email"), e);
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody Map<String, String> request) {
        try {

            User user = authenticationService.register(
                    request.get("nom"),
                    request.get("prenom"),
                    new Date(Long.parseLong(request.get("dateNaissance"))),
                    request.get("telephone"),
                    request.get("email"),
                    request.get("password")
            );
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            log.error("Registration failed for email: {}", request.get("email"), e);
            return ResponseEntity.badRequest().body(null);
        }
    }
}