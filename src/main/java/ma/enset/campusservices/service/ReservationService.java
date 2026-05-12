package ma.enset.campusservices.service;

import ma.enset.campusservices.dao.IReservationDAO;
import ma.enset.campusservices.dao.ISalleDAO;
import ma.enset.campusservices.dao.impl.ReservationDAOImpl;
import ma.enset.campusservices.dao.impl.SalleDAOImpl;
import ma.enset.campusservices.model.*;
import ma.enset.campusservices.model.enums.StatutReservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service de gestion des réservations de salles.
 * Utilise IReservationDAO et ISalleDAO (PostgreSQL).
 */
public class ReservationService implements IGestionable<ReservationSalle> {

    private static ReservationService instance;

    private final IReservationDAO     reservationDAO;
    private final ISalleDAO           salleDAO;
    private final NotificationService notificationService;

    private ReservationService() {
        this.reservationDAO      = new ReservationDAOImpl();
        this.salleDAO            = new SalleDAOImpl();
        this.notificationService = NotificationService.getInstance();
    }

    public static ReservationService getInstance() {
        if (instance == null) instance = new ReservationService();
        return instance;
    }

    public List<Salle> getSalles() {
        return salleDAO.findAll();
    }

    public List<ReservationSalle> getReservationsEtudiant(Etudiant etudiant) {
        return reservationDAO.findByEtudiant(etudiant.getId());
    }

    public ReservationSalle creerReservation(Etudiant etudiant, Salle salle,
                                              LocalDateTime debut, LocalDateTime fin) {
        boolean conflit = reservationDAO.findBySalle(salle.getId()).stream()
                .filter(r -> r.getStatut() != StatutReservation.ANNULEE)
                .anyMatch(r -> debut.isBefore(r.getDateFin()) && fin.isAfter(r.getDateDebut()));

        if (conflit) {
            throw new IllegalStateException(
                    "La salle \"" + salle.getNom() + "\" est déjà réservée sur ce créneau.");
        }

        ReservationSalle reservation = new ReservationSalle(0, etudiant, salle, debut, fin);
        reservationDAO.save(reservation);

        notificationService.creerNotification(etudiant,
                "Demande de réservation pour \"" + salle.getNom() + "\" soumise avec succès.", "reservation");
        return reservation;
    }

    public void annulerReservation(ReservationSalle reservation) {
        if (!reservation.isAnnulable()) {
            throw new IllegalStateException("Cette réservation ne peut pas être annulée.");
        }
        reservationDAO.updateStatut(reservation.getId(), StatutReservation.ANNULEE);
        reservation.setStatut(StatutReservation.ANNULEE);

        notificationService.creerNotification(reservation.getEtudiant(),
                "Votre réservation pour \"" + reservation.getSalle().getNom() + "\" a été annulée.", "reservation");
    }

    @Override
    public ReservationSalle creer(ReservationSalle r) {
        return reservationDAO.save(r);
    }

    @Override
    public Optional<ReservationSalle> trouverParId(int id) {
        return reservationDAO.findById(id);
    }

    @Override
    public List<ReservationSalle> trouverTous() {
        return reservationDAO.findAll();
    }

    @Override
    public void supprimer(int id) {
        reservationDAO.updateStatut(id, StatutReservation.ANNULEE);
    }
}
