package ma.enset.campusservices.model.enums;

public enum TypeDemande {
    ATTESTATION_PRESENCE("Attestation de présence"),
    CERTIFICAT_SCOLARITE("Certificat de scolarité"),
    RELEVE_NOTES("Relevé de notes"),
    AUTRE("Autre");

    private final String libelle;

    TypeDemande(String libelle) {
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
