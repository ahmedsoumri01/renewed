package com.eya.pfe2.eyapfe2.Controllers;

import com.eya.pfe2.eyapfe2.Models.Compte;
import com.eya.pfe2.eyapfe2.Models.CompteType;
import com.eya.pfe2.eyapfe2.Models.DTO.CompteDTO;
import com.eya.pfe2.eyapfe2.Models.DTO.GetMondatDTO;
import com.eya.pfe2.eyapfe2.Models.DTO.MandatDTO;
import com.eya.pfe2.eyapfe2.Models.DTO.UserDTO;
import com.eya.pfe2.eyapfe2.Models.Mandat;
import com.eya.pfe2.eyapfe2.Models.User;
import com.eya.pfe2.eyapfe2.Repository.CompteRepository;
import com.eya.pfe2.eyapfe2.Repository.MandatRepository;
import com.eya.pfe2.eyapfe2.Repository.UserRepository;
import com.eya.pfe2.eyapfe2.Service.EmailService;
import com.eya.pfe2.eyapfe2.config.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientController {
    private final UserRepository userRepository;
    private final CompteRepository compteRepository;
    private final MandatRepository mandatRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getClientProfile() {
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
        System.out.println("Data received from frontend: " + updates);

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
            if (updates.containsKey("telephone")) {
                user.setTelephone(updates.get("telephone"));
            }
            if (updates.containsKey("email")) {
                user.setEmail(updates.get("email"));
            }
            if (updates.containsKey("password")) {
                String newPassword = updates.get("password");
                if (!newPassword.isEmpty()) {
                    user.setPassword(passwordEncoder.encode(newPassword));
                }
            }

            userRepository.save(user);
            String newToken = jwtTokenProvider.generateToken(user.getEmail());
            return ResponseEntity.ok(Map.of("message", "Profile updated successfully", "token", newToken));
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/create-compte")
    public ResponseEntity<Map<String, String>> createCompte(@RequestBody Map<String, String> request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            CompteType compteType = CompteType.valueOf(request.get("compteType"));
            Compte compte = new Compte();
            compte.setNumCompte(generateNumCompte());
            compte.setSolde(0);
            compte.setEtat("ACTIVE");
            compte.setDateOuverture(new Date());
            compte.setCompteType(compteType);
            compte.setUser(user);
            Compte savedCompte = compteRepository.save(compte);
            return ResponseEntity.ok(Map.of("numCompte", savedCompte.getNumCompte()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/accounts")
    public ResponseEntity<List<CompteDTO>> getAllAccounts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            List<Compte> comptes = compteRepository.findByUser(user);
            List<CompteDTO> compteDTOs = comptes.stream()
                    .map(this::convertToCompteDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(compteDTOs);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/create-mandat")
    public ResponseEntity<Map<String, String>> createMandat(@RequestBody MandatDTO mandatDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<Compte> compteOptional = compteRepository.findByNumCompte(mandatDTO.getNumCompte());

            if (compteOptional.isPresent()) {
                Compte compte = compteOptional.get();
                if (compte.getSolde() >= mandatDTO.getMontant()) {
                    compte.setSolde(compte.getSolde() - mandatDTO.getMontant());
                    compteRepository.save(compte);

                    Mandat mandat = new Mandat();
                    mandat.setCode(generateCode());
                    mandat.setDate(new Date());
                    mandat.setMontant(mandatDTO.getMontant());
                    mandat.setCinEmetteur(mandatDTO.getCinEmetteur());
                    mandat.setCinRecepteur(mandatDTO.getCinRecepteur());
                    mandat.setNomRecepteur(mandatDTO.getNomRecepteur());
                    mandat.setPrenomRecepteur(mandatDTO.getPrenomRecepteur());
                    mandat.setEmailRecepteur(mandatDTO.getEmailRecepteur());
                    mandat.setCompte(compte);
                    Mandat savedMandat = mandatRepository.save(mandat);

                    String emailContent = generateEmailContent(mandatDTO, savedMandat.getCode());

                    emailService.sendEmail(
                            mandatDTO.getEmailRecepteur(),
                            "You have received a Mandat",
                            emailContent
                    );

                    return ResponseEntity.ok(Map.of("code", savedMandat.getCode()));
                } else {
                    return ResponseEntity.badRequest().body(Map.of("error", "Insufficient balance"));
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private String generateEmailContent(MandatDTO mandatDTO, String code) {
        return "<div style='font-family: Arial, sans-serif;'>" +
                "<p>Bonjour Monsieur/Madame <strong>" + mandatDTO.getNomRecepteur() + " " + mandatDTO.getPrenomRecepteur() + "</strong>,</p>" +
                "<p>Vous avez reçu un mandat d'un montant de <strong>" + mandatDTO.getMontant() + " TND</strong> de la part de CIN: " + mandatDTO.getCinEmetteur() + ".</p>" +
                "<p>Vous pouvez retirer ce mandat dans n'importe quelle agence BNA.</p>" +
                "<div style='border: 1px solid gray; padding: 10px; margin: 20px 0;'>" +
                "<p style='color: red;'><strong>Code du Mandat: " + code + "</strong></p>" +
                "<p><em>Note: Ne partagez ce code avec personne.</em></p>" +
                "</div>" +
                "<p>Merci,</p>" +
                "<p><strong>eBank Team</strong></p>" +
                "</div>";
    }
    @GetMapping("/mandats")
    public ResponseEntity<List<GetMondatDTO>> getAllMandats() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            List<Compte> comptes = compteRepository.findByUser(user);
            List<Mandat> mandats = mandatRepository.findByCompteIn(comptes);
            List<GetMondatDTO> mandatDTOs = mandats.stream()
                    .map(this::convertToMandatDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(mandatDTOs);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    private GetMondatDTO convertToMandatDTO(Mandat mandat) {
        GetMondatDTO mandatDTO = new GetMondatDTO();
        mandatDTO.setId(mandat.getId());
        mandatDTO.setCode(mandat.getCode());
        mandatDTO.setDate(mandat.getDate());
        mandatDTO.setMontant(mandat.getMontant());
        mandatDTO.setCinEmetteur(mandat.getCinEmetteur());
        mandatDTO.setCinRecepteur(mandat.getCinRecepteur());
        mandatDTO.setNomRecepteur(mandat.getNomRecepteur());
        mandatDTO.setPrenomRecepteur(mandat.getPrenomRecepteur());
        mandatDTO.setEmailRecepteur(mandat.getEmailRecepteur());
        return mandatDTO;
    }
    private String generateNumCompte() {
        SecureRandom random = new SecureRandom();
        StringBuilder numCompte = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            numCompte.append(random.nextInt(10));
        }
        return numCompte.toString();
    }
    private UserDTO convertToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setNom(user.getNom());
        userDTO.setPrenom(user.getPrenom());
        userDTO.setDateNaissance(user.getDateNaissance());
        userDTO.setTelephone(user.getTelephone());
        userDTO.setEmail(user.getEmail());
        List<CompteDTO> compteDTOs = user.getComptes().stream()
                .map(this::convertToCompteDTO)
                .collect(Collectors.toList());
        userDTO.setComptes(compteDTOs);
        return userDTO;
    }

    private CompteDTO convertToCompteDTO(Compte compte) {
        CompteDTO compteDTO = new CompteDTO();
        compteDTO.setId(compte.getId());
        compteDTO.setNumCompte(compte.getNumCompte());
        compteDTO.setSolde(compte.getSolde());
        compteDTO.setEtat(compte.getEtat());
        compteDTO.setDateOuverture(compte.getDateOuverture());
        compteDTO.setCompteType(compte.getCompteType());
        return compteDTO;
    }
    private String generateCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}
