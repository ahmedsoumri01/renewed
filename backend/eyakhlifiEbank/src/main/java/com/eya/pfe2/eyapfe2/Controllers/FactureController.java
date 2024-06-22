package com.eya.pfe2.eyapfe2.Controllers;

import com.eya.pfe2.eyapfe2.Models.DTO.FactureDTO;
import com.eya.pfe2.eyapfe2.Models.DTO.SendedFactureDTO;
import com.eya.pfe2.eyapfe2.Service.FactureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/factures")
@RequiredArgsConstructor
@Slf4j
public class FactureController {
    private final FactureService factureService;

    @GetMapping
    public ResponseEntity<List<FactureDTO>> getClientFactures() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        List<FactureDTO> factures = factureService.getClientFactures(email);
        return ResponseEntity.ok(factures);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FactureDTO> getFactureDetails(@PathVariable Long id) {
        FactureDTO facture = factureService.getFactureDetails(id);
        if (facture != null) {
            return ResponseEntity.ok(facture);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/all")
    public ResponseEntity<List<FactureDTO>> getAllFactures() {
        List<FactureDTO> allFactures = factureService.getAllFactures();
        return ResponseEntity.ok(allFactures);
    }

    @PostMapping("/pay/{id}")
    public ResponseEntity<SendedFactureDTO> payFacture(@PathVariable Long id, @RequestParam String compteId) {
        System.out.println("Facture ID: " + id);
        System.out.println("Compte ID: " + compteId);

        SendedFactureDTO facture = factureService.payFacture(id, compteId);
        if (facture != null) {
            return ResponseEntity.ok(facture);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<FactureDTO> createFakeFacture(@RequestBody FactureDTO factureDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        FactureDTO createdFacture = factureService.createFakeFacture(factureDTO, email);
        return ResponseEntity.ok(createdFacture);
    }
}
