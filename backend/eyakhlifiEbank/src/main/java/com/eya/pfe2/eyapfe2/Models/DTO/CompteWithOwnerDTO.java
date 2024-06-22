package com.eya.pfe2.eyapfe2.Models.DTO;

import com.eya.pfe2.eyapfe2.Models.CompteType;
import lombok.Data;

import java.util.Date;
@Data
public class CompteWithOwnerDTO {
    private Long id;
    private String numCompte;
    private double solde;
    private String etat;
    private Date dateOuverture;
    private CompteType compteType;
    private String ownerName;
    private String ownerEmail;

}
