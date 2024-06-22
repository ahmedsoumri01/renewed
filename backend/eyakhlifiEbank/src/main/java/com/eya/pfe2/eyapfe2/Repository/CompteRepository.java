package com.eya.pfe2.eyapfe2.Repository;

import com.eya.pfe2.eyapfe2.Models.Compte;
import com.eya.pfe2.eyapfe2.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompteRepository  extends JpaRepository<Compte, Long> {
    List<Compte> findByUser(User user);
    Optional<Compte> findByNumCompte(String numCompte);
}
