package ma.enset.campusservices.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Catalogue {

    private int id;
    private String nom;
    private List<Ressource> ressources;

    public Catalogue(int id, String nom) {
        this.id = id;
        this.nom = nom;
        this.ressources = new ArrayList<>();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public List<Ressource> getRessources() { return ressources; }

    public void ajouterRessource(Ressource r) {
        ressources.add(r);
    }

    public void retirerRessource(Ressource r) {
        ressources.remove(r);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Catalogue c)) return false;
        return id == c.id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return nom; }
}
