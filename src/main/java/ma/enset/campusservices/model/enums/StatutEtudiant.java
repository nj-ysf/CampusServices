package ma.enset.campusservices.model.enums;

public enum StatutEtudiant {
    ACTIF("Actif"),
    INACTIF("Inactif");

    private final String libelle;

    StatutEtudiant(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }

    @Override
    public String toString() {
        return libelle;
    }
}
