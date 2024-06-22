package com.eya.pfe2.eyapfe2.Models.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class MandatDTO {
    private double montant;
    private String cinEmetteur;
    private String cinRecepteur;
    private String nomRecepteur;
    private String prenomRecepteur;
    private String emailRecepteur;
    private String numCompte;
}