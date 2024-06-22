package com.eya.pfe2.eyapfe2.Controllers;

import com.eya.pfe2.eyapfe2.Models.DTO.RetraitDTO;
import com.eya.pfe2.eyapfe2.Models.DTO.TransactionDTO;
import com.eya.pfe2.eyapfe2.Models.DTO.VersementDTO;
import com.eya.pfe2.eyapfe2.Models.DTO.VirementDTO;
import com.eya.pfe2.eyapfe2.Models.Transaction;
import com.eya.pfe2.eyapfe2.Service.TransactionMapper;
import com.eya.pfe2.eyapfe2.Service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/virement")
    public ResponseEntity<String> virement(@RequestBody VirementDTO virementDTO) {
        String response = transactionService.virement(virementDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/versement")
    public ResponseEntity<String> versement(@RequestBody VersementDTO versementDTO) {
        String response = transactionService.versement(versementDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/retrait")
    public ResponseEntity<String> retrait(@RequestBody RetraitDTO retraitDTO) {
        String response = transactionService.retrait(retraitDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{numCompte}/transactions")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByCompte(@PathVariable String numCompte) {
        List<Transaction> transactions = transactionService.getTransactionsByCompte(numCompte);
        List<TransactionDTO> transactionDTOs = transactions.stream()
                .map(TransactionMapper::toTransactionDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactionDTOs);
    }
}


