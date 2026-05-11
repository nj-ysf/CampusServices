package ma.enset.campusservices.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Emprunt {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private int id;
    private Etudiant etudiant;
    private Ressource ressource;
    private LocalDate dateEmprunt;
    private LocalDate dateRetour;

    public Emprunt(int id, Etudiant etudiant, Ressource ressource, LocalDate dateEmprunt) {
        this.id = id;
        this.etudiant = etudiant;
        this.ressource = ressource;
        this.dateEmprunt = dateEmprunt;
        this.dateRetour = null;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Etudiant getEtudiant() { return etudiant; }
    public void setEtudiant(Etudiant etudiant) { this.etudiant = etudiant; }

    public Ressource getRessource() { return ressource; }
    public void setRessource(Ressource ressource) { this.ressource = ressource; }

    public LocalDate getDateEmprunt() { return dateEmprunt; }
    public void setDateEmprunt(LocalDate dateEmprunt) { this.dateEmprunt = dateEmprunt; }

    public LocalDate getDateRetour() { return dateRetour; }
    public void setDateRetour(LocalDate dateRetour) { this.dateRetour = dateRetour; }

    public boolean isEnCours() { return dateRetour == null; }

    public String getStatutLibelle() {
        return isEnCours() ? "En cours" : "Retourné le " + dateRetour.format(FORMATTER);
    }

    public String getDateEmpruntFormatee() {
        return dateEmprunt != null ? dateEmprunt.format(FORMATTER) : "-";
    }

    public String getDateRetourFormatee() {
        return dateRetour != null ? dateRetour.format(FORMATTER) : "—";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Emprunt e)) return false;
        return id == e.id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Emprunt #" + id + " — " + ressource.getTitre();
    }
}
