package com.eya.pfe2.eyapfe2.Controllers;

import com.eya.pfe2.eyapfe2.Models.Compte;
import com.eya.pfe2.eyapfe2.Models.User;
import com.eya.pfe2.eyapfe2.Repository.CompteRepository;
import com.eya.pfe2.eyapfe2.Service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/compte")
@RequiredArgsConstructor
@Slf4j
public class CompteController {

    private final CompteRepository compteRepository;
    private final EmailService emailService;
    @GetMapping("/{numCompte}/solde")
    public ResponseEntity<Double> checkSolde(@PathVariable String numCompte) {
        Optional<Compte> compteOptional = compteRepository.findByNumCompte(numCompte);
        return compteOptional.map(compte -> ResponseEntity.ok(compte.getSolde()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{numCompte}/desactivate")
    public ResponseEntity<String> desactivateAccount(@PathVariable String numCompte) {
        Optional<Compte> compteOptional = compteRepository.findByNumCompte(numCompte);

        if (compteOptional.isPresent()) {
            Compte compte = compteOptional.get();
            User user = compte.getUser();
            compte.setEtat("INACTIVE");
            compteRepository.save(compte);

            sendDeactivationEmail(user, compte);

            return ResponseEntity.ok("Account deactivated successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    private void sendDeactivationEmail(User user, Compte compte) {
        String subject = "Votre compte a été désactivé";
        String recipientEmail = user.getEmail();

        String emailContent = generateEmailContent(user, compte);

        emailService.sendEmail(recipientEmail, subject, emailContent);
    }

    private String generateEmailContent(User user, Compte compte) {
        return String.format(
                "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px;'>" +
                        "<div style='background-color: #f7f7f7; padding: 10px; border-bottom: 1px solid #ddd; text-align: center;'>" +
                        "<h2>Compte Désactivé</h2>" +
                        "</div>" +
                        "<div style='padding: 20px;'>" +
                        "<p>Bonjour %s %s,</p>" +
                        "<p>Nous vous informons que votre compte bancaire numéro <strong>%s</strong> a été désactivé par l'administration. Veuillez vous rendre dans n'importe quelle agence BNA pour vérifier la raison de cette désactivation.</p>" +
                        "<p>Merci de votre compréhension.</p>" +
                        "</div>" +
                        "<div style='margin-top: 20px; padding: 10px; border-top: 1px solid #ddd; text-align: center; font-size: 12px; color: #888;'>" +
                        "<p>&copy; 2024 eBank</p>" +
                        "</div>" +
                        "</div>",
                user.getNom(), user.getPrenom(), compte.getNumCompte()
        );
    }
    @PostMapping("/{numCompte}/activate")
    public ResponseEntity<String> activateAccount(@PathVariable String numCompte) {
        Optional<Compte> compteOptional = compteRepository.findByNumCompte(numCompte);

        if (compteOptional.isPresent()) {
            Compte compte = compteOptional.get();
            compte.setEtat("ACTIVE");
            compteRepository.save(compte);
            return ResponseEntity.ok("Account activated successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}