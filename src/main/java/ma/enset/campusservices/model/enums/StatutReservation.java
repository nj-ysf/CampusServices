package ma.enset.campusservices.model.enums;

public enum StatutReservation {
    EN_ATTENTE("En attente"),
    VALIDEE("Validée"),
    REFUSEE("Refusée"),
    ANNULEE("Annulée");

    private final String libelle;

    StatutReservation(String libelle) {
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
