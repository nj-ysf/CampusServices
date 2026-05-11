package ma.enset.campusservices.model;

import ma.enset.campusservices.model.enums.StatutDemande;
import ma.enset.campusservices.model.enums.TypeDemande;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class DemandeAdministrative {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private int id;
    private Etudiant etudiant;
    private TypeDemande type;
    private StatutDemande statut;
    private String description;
    private LocalDateTime dateCreation;
    private LocalDateTime dateMiseAJour;
    private String commentaireAdmin;

    public DemandeAdministrative(int id, Etudiant etudiant, TypeDemande type, String description) {
        this.id = id;
        this.etudiant = etudiant;
        this.type = type;
        this.description = description;
        this.statut = StatutDemande.SOUMISE;
        this.dateCreation = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Etudiant getEtudiant() { return etudiant; }
    public void setEtudiant(Etudiant etudiant) { this.etudiant = etudiant; }

    public TypeDemande getType() { return type; }
    public void setType(TypeDemande type) { this.type = type; }

    public StatutDemande getStatut() { return statut; }
    public void setStatut(StatutDemande statut) {
        this.statut = statut;
        this.dateMiseAJour = LocalDateTime.now();
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateMiseAJour() { return dateMiseAJour; }

    public String getCommentaireAdmin() { return commentaireAdmin; }
    public void setCommentaireAdmin(String commentaireAdmin) { this.commentaireAdmin = commentaireAdmin; }

    public String getDateCreationFormatee() {
        return dateCreation != null ? dateCreation.format(FORMATTER) : "-";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DemandeAdministrative d)) return false;
        return id == d.id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Demande #" + id + " — " + type.getLibelle();
    }
}
