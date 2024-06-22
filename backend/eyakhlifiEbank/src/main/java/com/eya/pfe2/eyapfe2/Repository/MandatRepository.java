package com.eya.pfe2.eyapfe2.Repository;

import com.eya.pfe2.eyapfe2.Models.Compte;
import com.eya.pfe2.eyapfe2.Models.Mandat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MandatRepository extends JpaRepository<Mandat, Long> {
    List<Mandat> findByCompteIn(List<Compte> comptes);
}
