package com.eya.pfe2.eyapfe2.Models.DTO;

import com.eya.pfe2.eyapfe2.Models.Libelle;
import lombok.Data;

@Data
public class FactureDTO {
    private Long id;
    private double montant;
    private String reference;
    private boolean paye;
    private Libelle libelle;
}
