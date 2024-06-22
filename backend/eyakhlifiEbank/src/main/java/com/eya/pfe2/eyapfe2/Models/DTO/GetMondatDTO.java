package com.eya.pfe2.eyapfe2.Models.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class GetMondatDTO {
    private Long id;
    private String code;
    private Date date;
    private double montant;
    private String cinEmetteur;
    private String cinRecepteur;
    private String nomRecepteur;
    private String prenomRecepteur;
    private String emailRecepteur;
}
