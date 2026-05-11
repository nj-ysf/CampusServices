package ma.enset.campusservices.model;

import java.util.Objects;

/**
 * Classe abstraite représentant une ressource de la bibliothèque.
 * Les sous-classes concrètes sont : Livre et Memoire.
 */
public abstract class Ressource {

    private int id;
    private String titre;
    private String auteur;
    private boolean disponible;
    private Catalogue catalogue;

    public Ressource(int id, String titre, String auteur, boolean disponible, Catalogue catalogue) {
        this.id = id;
        this.titre = titre;
        this.auteur = auteur;
        this.disponible = disponible;
        this.catalogue = catalogue;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getAuteur() { return auteur; }
    public void setAuteur(String auteur) { this.auteur = auteur; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public Catalogue getCatalogue() { return catalogue; }
    public void setCatalogue(Catalogue catalogue) { this.catalogue = catalogue; }

    public String getCatalogueNom() {
        return catalogue != null ? catalogue.getNom() : "-";
    }

    /**
     * Retourne le type de ressource (à implémenter dans les sous-classes).
     */
    public abstract String getType();

    /**
     * Retourne une description courte de la ressource.
     */
    public abstract String getDescription();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ressource r)) return false;
        return id == r.id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return titre + " — " + auteur; }
}
