package com.eya.pfe2.eyapfe2.Service;

import com.eya.pfe2.eyapfe2.Models.Compte;
import com.eya.pfe2.eyapfe2.Models.DTO.FactureDTO;
import com.eya.pfe2.eyapfe2.Models.DTO.SendedFactureDTO;
import com.eya.pfe2.eyapfe2.Models.Facture;
import com.eya.pfe2.eyapfe2.Models.User;
import com.eya.pfe2.eyapfe2.Repository.CompteRepository;
import com.eya.pfe2.eyapfe2.Repository.FactureRepository;
import com.eya.pfe2.eyapfe2.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FactureService {
    private final FactureRepository factureRepository;
    private final UserRepository userRepository;
    private final CompteRepository compteRepository;
    private final EmailService emailService;

    public List<FactureDTO> getClientFactures(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return factureRepository.findByUser(user).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public FactureDTO getFactureDetails(Long id) {
        return factureRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }
    public List<FactureDTO> getAllFactures() {
        return factureRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public SendedFactureDTO  payFacture(Long factureId, String compteId) {
        Optional<Facture> factureOptional = factureRepository.findById(factureId);
        Optional<Compte> compteOptional = compteRepository.findByNumCompte(compteId);

        if (factureOptional.isPresent() && compteOptional.isPresent()) {
            Facture facture = factureOptional.get();
            Compte compte = compteOptional.get();

            if (compte.getSolde() >= facture.getMontant()) {
                compte.setSolde(compte.getSolde() - facture.getMontant());
                facture.setPaye(true);

                // Save updated compte and facture
                compteRepository.save(compte);
                factureRepository.save(facture);

                // Send email notification
                sendFacturePaidEmail(facture);

                return new SendedFactureDTO(facture);
            }
        }
        return null;
    }

    private void sendFacturePaidEmail(Facture facture) {
        User user = facture.getUser();
        String subject = "Votre facture a été payée";
        String recipientEmail = user.getEmail();

        String emailContent = generateEmailContent(user, facture);

        emailService.sendEmail(recipientEmail, subject, emailContent);
    }

    private String generateEmailContent(User user, Facture facture) {
        return String.format(
                "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px;'>" +
                        "<div style='background-color: #f7f7f7; padding: 10px; border-bottom: 1px solid #ddd; text-align: center;'>" +
                        "<h2>Facture Paid</h2>" +
                        "</div>" +
                        "<div style='padding: 20px;'>" +
                        "<p>Bonjour %s %s,</p>" +
                        "<p>Nous vous informons que votre facture a été payée avec succès. Voici les détails de la facture :</p>" +
                        "<div style='margin-top: 20px;'>" +
                        "<table style='width: 100%%; border-collapse: collapse;'>" +
                        "<tr><th style='border: 1px solid #ddd; padding: 8px; background-color: #f2f2f2;'>Montant</th><td style='border: 1px solid #ddd; padding: 8px;'>%.2f EUR</td></tr>" +
                        "<tr><th style='border: 1px solid #ddd; padding: 8px; background-color: #f2f2f2;'>Référence</th><td style='border: 1px solid #ddd; padding: 8px;'>%s</td></tr>" +
                        "<tr><th style='border: 1px solid #ddd; padding: 8px; background-color: #f2f2f2;'>Libellé</th><td style='border: 1px solid #ddd; padding: 8px;'>%s</td></tr>" +
                        "</table>" +
                        "</div>" +
                        "</div>" +
                        "<div style='margin-top: 20px; padding: 10px; border-top: 1px solid #ddd; text-align: center; font-size: 12px; color: #888;'>" +
                        "<p>Merci de ne pas partager les informations de cette facture avec qui que ce soit.</p>" +
                        "<p>&copy; 2024 eBank</p>" +
                        "</div>" +
                        "</div>",
                user.getNom(), user.getPrenom(), facture.getMontant(), facture.getReference(), facture.getLibelle().name()
        );
    }

    public FactureDTO createFakeFacture(FactureDTO factureDTO, String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Facture facture = new Facture();
            facture.setMontant(factureDTO.getMontant());
            facture.setReference(factureDTO.getReference());
            facture.setPaye(false);
            facture.setLibelle(factureDTO.getLibelle());
            facture.setUser(user);
            Facture savedFacture = factureRepository.save(facture);
            return convertToDTO(savedFacture);
        }
        return null;
    }

    private FactureDTO convertToDTO(Facture facture) {
        FactureDTO dto = new FactureDTO();
        dto.setId(facture.getId());
        dto.setMontant(facture.getMontant());
        dto.setReference(facture.getReference());
        dto.setPaye(facture.isPaye());
        dto.setLibelle(facture.getLibelle());
        return dto;
    }
}
