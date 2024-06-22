package com.eya.pfe2.eyapfe2.Models;

import jakarta.persistence.*;

@Entity
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double montant;
    private String reference;
    private boolean paye;

    @Enumerated(EnumType.STRING)
    private Libelle libelle;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public boolean isPaye() {
        return paye;
    }

    public void setPaye(boolean paye) {
        this.paye = paye;
    }

    public Libelle getLibelle() {
        return libelle;
    }

    public void setLibelle(Libelle libelle) {
        this.libelle = libelle;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
