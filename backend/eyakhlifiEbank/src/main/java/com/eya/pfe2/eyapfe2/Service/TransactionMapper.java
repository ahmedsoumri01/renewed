package com.eya.pfe2.eyapfe2.Service;

import com.eya.pfe2.eyapfe2.Models.DTO.TransactionDTO;
import com.eya.pfe2.eyapfe2.Models.Transaction;

public class TransactionMapper {
    public static TransactionDTO toTransactionDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setDate(transaction.getDate());
        dto.setMontant(transaction.getMontant());
        dto.setType(transaction.getType());
        dto.setNumCompte(transaction.getCompte().getNumCompte());
        return dto;
    }
}
