package ma.enset.campusservices.model.enums;

public enum Role {
    ETUDIANT("Étudiant"),
    ADMIN("Administrateur"),
    BIBLIOTHECAIRE("Bibliothécaire"),
    RESPONSABLE_SALLES("Responsable des salles");

    private final String libelle;

    Role(String libelle) { this.libelle = libelle; }

    public String getLibelle() { return libelle; }

    @Override
    public String toString() { return libelle; }
}
