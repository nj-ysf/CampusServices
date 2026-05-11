package ma.enset.campusservices.model;

/**
 * Un mémoire de fin d'études. Hérite de Ressource.
 */
public class Memoire extends Ressource {

    private String specialite;
    private int annee;
    private String encadrant;

    public Memoire(int id, String titre, String auteur, boolean disponible,
                   Catalogue catalogue, String specialite, int annee, String encadrant) {
        super(id, titre, auteur, disponible, catalogue);
        this.specialite = specialite;
        this.annee = annee;
        this.encadrant = encadrant;
    }

    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }

    public int getAnnee() { return annee; }
    public void setAnnee(int annee) { this.annee = annee; }

    public String getEncadrant() { return encadrant; }
    public void setEncadrant(String encadrant) { this.encadrant = encadrant; }

    @Override
    public String getType() { return "Mémoire"; }

    @Override
    public String getDescription() {
        return specialite + " | " + annee + " | Encadrant : " + encadrant;
    }
}
