package ma.enset.campusservices.model;

import ma.enset.campusservices.model.enums.StatutReservation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class ReservationSalle {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private int id;
    private Etudiant etudiant;
    private Salle salle;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private StatutReservation statut;

    public ReservationSalle(int id, Etudiant etudiant, Salle salle,
                             LocalDateTime dateDebut, LocalDateTime dateFin) {
        this.id = id;
        this.etudiant = etudiant;
        this.salle = salle;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = StatutReservation.EN_ATTENTE;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Etudiant getEtudiant() { return etudiant; }
    public void setEtudiant(Etudiant etudiant) { this.etudiant = etudiant; }

    public Salle getSalle() { return salle; }
    public void setSalle(Salle salle) { this.salle = salle; }

    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }

    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }

    public StatutReservation getStatut() { return statut; }
    public void setStatut(StatutReservation statut) { this.statut = statut; }

    public String getDateDebutFormatee() {
        return dateDebut != null ? dateDebut.format(FORMATTER) : "-";
    }

    public String getDateFinFormatee() {
        return dateFin != null ? dateFin.format(FORMATTER) : "-";
    }

    public boolean isAnnulable() {
        return statut == StatutReservation.EN_ATTENTE || statut == StatutReservation.CONFIRMEE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReservationSalle r)) return false;
        return id == r.id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Réservation #" + id + " — " + salle.getNom();
    }
}
