package com.eya.pfe2.eyapfe2.Models.DTO;

import lombok.Data;
import com.eya.pfe2.eyapfe2.Models.CompteType;

import java.util.Date;

@Data
public class CompteDTO {
    private Long id;
    private String numCompte;
    private double solde;
    private String etat;
    private Date dateOuverture;
    private CompteType compteType;
}