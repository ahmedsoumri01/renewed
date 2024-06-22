package com.eya.pfe2.eyapfe2.Models.DTO;

import com.eya.pfe2.eyapfe2.Models.Facture;
import lombok.Data;

@Data
public class SendedFactureDTO {
    private Long id;
    private double montant;
    private String reference;
    private boolean paye;
    private String libelle;
    private Long userId;

    public SendedFactureDTO(Facture facture) {
        this.id = facture.getId();
        this.montant = facture.getMontant();
        this.reference = facture.getReference();
        this.paye = facture.isPaye();
        this.libelle = facture.getLibelle().name();
        this.userId = facture.getUser().getId();
    }
}
