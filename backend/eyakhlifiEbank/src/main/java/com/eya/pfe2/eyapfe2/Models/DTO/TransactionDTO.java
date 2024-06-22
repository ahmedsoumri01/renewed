package com.eya.pfe2.eyapfe2.Models.DTO;

import com.eya.pfe2.eyapfe2.Models.TransactionType;
import lombok.Data;

import java.util.Date;

@Data
public class TransactionDTO {
    private Long id;
    private Date date;
    private double montant;
    private TransactionType type;
    private String numCompte;
}