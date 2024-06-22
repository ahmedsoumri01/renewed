package com.eya.pfe2.eyapfe2.Controllers;

import com.eya.pfe2.eyapfe2.Models.Compte;
import com.eya.pfe2.eyapfe2.Models.DTO.CompteWithOwnerDTO;
import com.eya.pfe2.eyapfe2.Models.DTO.UserDTO;
import com.eya.pfe2.eyapfe2.Models.User;
import com.eya.pfe2.eyapfe2.Repository.CompteRepository;
import com.eya.pfe2.eyapfe2.Repository.UserRepository;
import com.eya.pfe2.eyapfe2.config.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserRepository userRepository;
    private final CompteRepository compteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getAdminProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            UserDTO userDTO = convertToUserDTO(user);
            return ResponseEntity.ok(userDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update-profile")
    public ResponseEntity<Map<String, String>> updateProfile(@RequestBody Map<String, String> updates) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (updates.containsKey("nom")) {
                user.setNom(updates.get("nom"));
            }
            if (updates.containsKey("prenom")) {
                user.setPrenom(updates.get("prenom"));
            }
            if (updates.containsKey("dateNaissance")) {
                user.setDateNaissance(new Date(Long.parseLong(updates.get("dateNaissance"))));
            }
            if (updates.containsKey("telephone")) {
                user.setTelephone(updates.get("telephone"));
            }
            if (updates.containsKey("email")) {
                user.setEmail(updates.get("email"));
            }
            if (updates.containsKey("password")) {
                user.setPassword(passwordEncoder.encode(updates.get("password")));
            }

            userRepository.save(user);

            String token = jwtTokenProvider.generateToken(user.getEmail());
            return ResponseEntity.ok(Map.of("message", "Profile updated successfully.", "token", token));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Admin not found."));
        }
    }
    @GetMapping("/comptes")
    public ResponseEntity<List<CompteWithOwnerDTO>> getAllComptes() {
        List<Compte> comptes = compteRepository.findAll();
        List<CompteWithOwnerDTO> compteWithOwnerDTOs = comptes.stream()
                .map(this::convertToCompteWithOwnerDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(compteWithOwnerDTOs);
    }

    private UserDTO convertToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setNom(user.getNom());
        userDTO.setPrenom(user.getPrenom());
        userDTO.setDateNaissance(user.getDateNaissance());
        userDTO.setTelephone(user.getTelephone());
        userDTO.setEmail(user.getEmail());
        return userDTO;
    }

    private CompteWithOwnerDTO convertToCompteWithOwnerDTO(Compte compte) {
        CompteWithOwnerDTO dto = new CompteWithOwnerDTO();
        dto.setId(compte.getId());
        dto.setNumCompte(compte.getNumCompte());
        dto.setSolde(compte.getSolde());
        dto.setEtat(compte.getEtat());
        dto.setDateOuverture(compte.getDateOuverture());
        dto.setCompteType(compte.getCompteType());
        dto.setOwnerName(compte.getUser().getNom() + " " + compte.getUser().getPrenom());
        dto.setOwnerEmail(compte.getUser().getEmail());
        return dto;
    }
}
