package com.eya.pfe2.eyapfe2.Service;

import com.eya.pfe2.eyapfe2.Models.Compte;
import com.eya.pfe2.eyapfe2.Models.DTO.RetraitDTO;
import com.eya.pfe2.eyapfe2.Models.DTO.VersementDTO;
import com.eya.pfe2.eyapfe2.Models.DTO.VirementDTO;
import com.eya.pfe2.eyapfe2.Models.Transaction;
import com.eya.pfe2.eyapfe2.Models.TransactionType;
import com.eya.pfe2.eyapfe2.Repository.CompteRepository;
import com.eya.pfe2.eyapfe2.Repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final CompteRepository compteRepository;
    private final TransactionRepository transactionRepository;

    public String virement(VirementDTO virementDTO) {
        Compte fromCompte = compteRepository.findByNumCompte(virementDTO.getFromCompte())
                .orElseThrow(() -> new IllegalArgumentException("Sender account not found."));
        Compte toCompte = compteRepository.findByNumCompte(virementDTO.getToCompte())
                .orElseThrow(() -> new IllegalArgumentException("Receiver account not found."));

        if (!isActive(fromCompte) || !isActive(toCompte)) {
            return "One or both accounts are inactive.";
        }

        if (fromCompte.getSolde() < virementDTO.getMontant()) {
            return "Insufficient balance.";
        }

        fromCompte.setSolde(fromCompte.getSolde() - virementDTO.getMontant());
        toCompte.setSolde(toCompte.getSolde() + virementDTO.getMontant());

        compteRepository.save(fromCompte);
        compteRepository.save(toCompte);

        saveTransaction(fromCompte, virementDTO.getMontant(), TransactionType.VIREMENT);
        saveTransaction(toCompte, virementDTO.getMontant(), TransactionType.VIREMENT);

        return "Virement successful.";
    }

    public String versement(VersementDTO versementDTO) {
        Compte compte = compteRepository.findByNumCompte(versementDTO.getNumCompte())
                .orElseThrow(() -> new IllegalArgumentException("Account not found."));

        if (!isActive(compte)) {
            return "Account is inactive.";
        }

        compte.setSolde(compte.getSolde() + versementDTO.getMontant());
        compteRepository.save(compte);

        saveTransaction(compte, versementDTO.getMontant(), TransactionType.VERSEMENT);

        return "Versement successful.";
    }

    public String retrait(RetraitDTO retraitDTO) {
        Compte compte = compteRepository.findByNumCompte(retraitDTO.getNumCompte())
                .orElseThrow(() -> new IllegalArgumentException("Account not found."));

        if (!isActive(compte)) {
            return "Account is inactive.";
        }

        if (compte.getSolde() < retraitDTO.getMontant()) {
            return "Insufficient balance.";
        }

        compte.setSolde(compte.getSolde() - retraitDTO.getMontant());
        compteRepository.save(compte);

        saveTransaction(compte, retraitDTO.getMontant(), TransactionType.RETRAIT);

        return "Retrait successful.";
    }

    public List<Transaction> getTransactionsByCompte(String numCompte) {
        Compte compte = compteRepository.findByNumCompte(numCompte)
                .orElseThrow(() -> new IllegalArgumentException("Account not found."));
        return transactionRepository.findByCompte(compte);
    }

    private boolean isActive(Compte compte) {
        return "ACTIVE".equalsIgnoreCase(compte.getEtat());
    }

    private void saveTransaction(Compte compte, double montant, TransactionType type) {
        Transaction transaction = new Transaction();
        transaction.setDate(new Date());
        transaction.setMontant(montant);
        transaction.setType(type);
        transaction.setCompte(compte);
        transactionRepository.save(transaction);
    }
}
