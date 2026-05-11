package ma.enset.campusservices.model;

import ma.enset.campusservices.model.enums.StatutEtudiant;

import java.util.Objects;

public class Etudiant {

    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String filiere;
    private StatutEtudiant statut;

    public Etudiant(int id, String nom, String prenom, String email,
                    String motDePasse, String filiere, StatutEtudiant statut) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.filiere = filiere;
        this.statut = statut;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getNomComplet() { return prenom + " " + nom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public String getFiliere() { return filiere; }
    public void setFiliere(String filiere) { this.filiere = filiere; }

    public StatutEtudiant getStatut() { return statut; }
    public void setStatut(StatutEtudiant statut) { this.statut = statut; }

    public boolean isActif() { return statut == StatutEtudiant.ACTIF; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Etudiant e)) return false;
        return id == e.id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return getNomComplet() + " (" + email + ")";
    }
}
