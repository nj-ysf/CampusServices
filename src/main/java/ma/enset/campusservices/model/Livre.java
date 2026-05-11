package ma.enset.campusservices.model;

/**
 * Un livre est une ressource de type physique avec un ISBN, un nombre de pages et un éditeur.
 */
public class Livre extends Ressource {

    private String isbn;
    private int nbPages;
    private String editeur;

    public Livre(int id, String titre, String auteur, boolean disponible,
                 Catalogue catalogue, String isbn, int nbPages, String editeur) {
        super(id, titre, auteur, disponible, catalogue);
        this.isbn = isbn;
        this.nbPages = nbPages;
        this.editeur = editeur;
    }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public int getNbPages() { return nbPages; }
    public void setNbPages(int nbPages) { this.nbPages = nbPages; }

    public String getEditeur() { return editeur; }
    public void setEditeur(String editeur) { this.editeur = editeur; }

    @Override
    public String getType() { return "Livre"; }

    @Override
    public String getDescription() {
        return "ISBN: " + isbn + " | " + nbPages + " pages | " + editeur;
    }
}
