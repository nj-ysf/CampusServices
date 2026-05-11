package ma.enset.campusservices.model.enums;

public enum StatutDemande {
    SOUMISE("Soumise"),
    EN_TRAITEMENT("En traitement"),
    VALIDEE("Validée"),
    REFUSEE("Refusée");

    private final String libelle;

    StatutDemande(String libelle) {
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
