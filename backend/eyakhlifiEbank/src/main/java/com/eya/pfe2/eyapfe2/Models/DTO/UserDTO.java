package com.eya.pfe2.eyapfe2.Models.DTO;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserDTO {
    private Long id;
    private String nom;
    private String prenom;
    private Date dateNaissance;
    private String telephone;
    private String email;
    private List<CompteDTO> comptes;
}