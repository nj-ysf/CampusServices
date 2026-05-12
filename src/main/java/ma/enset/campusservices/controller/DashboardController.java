package ma.enset.campusservices.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ma.enset.campusservices.model.Etudiant;
import ma.enset.campusservices.service.*;

public class DashboardController {

    @FXML private Label lblEmpruntsEnCours;
    @FXML private Label lblReservationsActives;
    @FXML private Label lblDemandesEnCours;
    @FXML private Label lblNotifNonLues;
    @FXML private Label lblBienvenue;

    private final AuthService authService = AuthService.getInstance();
    private final BibliothequeService bibliothequeService = BibliothequeService.getInstance();
    private final ReservationService reservationService = ReservationService.getInstance();
    private final DemandeService demandeService = DemandeService.getInstance();
    private final NotificationService notificationService = NotificationService.getInstance();

    @FXML
    public void initialize() {
        Etudiant etudiant = authService.getEtudiantCourant();
        if (etudiant == null) return;

        lblBienvenue.setText("Bonjour, " + etudiant.getNomComplet() + " !");

        long emprunts = bibliothequeService.getEmpruntsActifs(etudiant).size();
        long reservations = reservationService.getReservationsEtudiant(etudiant).stream()
                .filter(r -> r.getStatut().name().equals("EN_ATTENTE")
                        || r.getStatut().name().equals("CONFIRMEE"))
                .count();
        long demandes = demandeService.getDemandesEtudiant(etudiant).stream()
                .filter(d -> d.getStatut().name().equals("SOUMISE")
                        || d.getStatut().name().equals("EN_TRAITEMENT"))
                .count();
        long notifs = notificationService.compterNonLues(etudiant);

        lblEmpruntsEnCours.setText(String.valueOf(emprunts));
        lblReservationsActives.setText(String.valueOf(reservations));
        lblDemandesEnCours.setText(String.valueOf(demandes));
        lblNotifNonLues.setText(String.valueOf(notifs));
    }
}
