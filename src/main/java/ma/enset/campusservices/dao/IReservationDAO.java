package ma.enset.campusservices.dao;

import ma.enset.campusservices.model.ReservationSalle;
import ma.enset.campusservices.model.enums.StatutReservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IReservationDAO {
    List<ReservationSalle>     findAll();
    List<ReservationSalle>     findByEtudiant(int etudiantId);
    List<ReservationSalle>     findBySalle(int salleId);
    Optional<ReservationSalle> findById(int id);
    ReservationSalle           save(ReservationSalle reservation);
    void                       updateStatut(int id, StatutReservation statut);
}
