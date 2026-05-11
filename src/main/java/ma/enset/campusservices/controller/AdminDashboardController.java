package ma.enset.campusservices.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ma.enset.campusservices.model.enums.StatutDemande;
import ma.enset.campusservices.model.enums.StatutReservation;
import ma.enset.campusservices.repository.DataStore;
import ma.enset.campusservices.service.AuthService;

public class AdminDashboardController {

    @FXML private Label lblTotalEtudiants;
    @FXML private Label lblEmpruntsActifs;
    @FXML private Label lblReservationsAttente;
    @FXML private Label lblDemandesAttente;
    @FXML private Label lblAdminName;

    private final DataStore   dataStore   = DataStore.getInstance();
    private final AuthService authService = AuthService.getInstance();

    @FXML
    public void initialize() {
        if (authService.getAdminCourant() != null)
            lblAdminName.setText("Bonjour, " + authService.getAdminCourant().getNomComplet() + " !");

        lblTotalEtudiants.setText(String.valueOf(dataStore.getEtudiants().size()));

        long emprunts = dataStore.getEmprunts().stream()
                .filter(e -> e.isEnCours()).count();
        lblEmpruntsActifs.setText(String.valueOf(emprunts));

        long reservations = dataStore.getReservations().stream()
                .filter(r -> r.getStatut() == StatutReservation.EN_ATTENTE).count();
        lblReservationsAttente.setText(String.valueOf(reservations));

        long demandes = dataStore.getDemandes().stream()
                .filter(d -> d.getStatut() == StatutDemande.SOUMISE
                          || d.getStatut() == StatutDemande.EN_TRAITEMENT).count();
        lblDemandesAttente.setText(String.valueOf(demandes));
    }
}
