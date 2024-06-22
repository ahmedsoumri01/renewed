package com.eya.pfe2.eyapfe2.Models;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Mandat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private Date date;
    private double montant;
    private String cinEmetteur;
    private String cinRecepteur;
    private String nomRecepteur;
    private String prenomRecepteur;
    private String emailRecepteur;

    @ManyToOne
    @JoinColumn(name = "compte_id")
    private Compte compte;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public String getCinEmetteur() {
        return cinEmetteur;
    }

    public void setCinEmetteur(String cinEmetteur) {
        this.cinEmetteur = cinEmetteur;
    }

    public String getCinRecepteur() {
        return cinRecepteur;
    }

    public void setCinRecepteur(String cinRecepteur) {
        this.cinRecepteur = cinRecepteur;
    }

    public String getNomRecepteur() {
        return nomRecepteur;
    }

    public void setNomRecepteur(String nomRecepteur) {
        this.nomRecepteur = nomRecepteur;
    }

    public String getPrenomRecepteur() {
        return prenomRecepteur;
    }

    public void setPrenomRecepteur(String prenomRecepteur) {
        this.prenomRecepteur = prenomRecepteur;
    }

    public String getEmailRecepteur() {
        return emailRecepteur;
    }

    public void setEmailRecepteur(String emailRecepteur) {
        this.emailRecepteur = emailRecepteur;
    }

    public Compte getCompte() {
        return compte;
    }

    public void setCompte(Compte compte) {
        this.compte = compte;
    }
}
