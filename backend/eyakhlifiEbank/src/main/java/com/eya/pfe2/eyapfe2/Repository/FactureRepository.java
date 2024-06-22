package com.eya.pfe2.eyapfe2.Repository;


import com.eya.pfe2.eyapfe2.Models.Facture;
import com.eya.pfe2.eyapfe2.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Long> {
    List<Facture> findByUser(User user);
}
