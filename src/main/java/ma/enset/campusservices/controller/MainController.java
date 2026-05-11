package ma.enset.campusservices.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import ma.enset.campusservices.MainApp;
import ma.enset.campusservices.model.Etudiant;
import ma.enset.campusservices.service.AuthService;
import ma.enset.campusservices.service.NotificationService;

import java.io.IOException;
import java.util.Objects;

public class MainController {

    @FXML private Label studentNameLabel;
    @FXML private Label filiereLabel;
    @FXML private Label notifBadge;
    @FXML private StackPane contentArea;
    @FXML private VBox navMenu;

    @FXML private Button navDashboard;
    @FXML private Button navBibliotheque;
    @FXML private Button navEmprunts;
    @FXML private Button navReservations;
    @FXML private Button navDemandes;
    @FXML private Button navNotifications;

    private final AuthService authService = AuthService.getInstance();
    private final NotificationService notificationService = NotificationService.getInstance();

    private Button activeNav;

    @FXML
    public void initialize() {
        Etudiant etudiant = authService.getEtudiantCourant();
        if (etudiant != null) {
            studentNameLabel.setText(etudiant.getNomComplet());
            filiereLabel.setText(etudiant.getFiliere());
        }
        updateNotifBadge();
        showDashboard();
    }

    public void updateNotifBadge() {
        Etudiant etudiant = authService.getEtudiantCourant();
        if (etudiant != null) {
            long count = notificationService.compterNonLues(etudiant);
            notifBadge.setText(count > 0 ? String.valueOf(count) : "");
            notifBadge.setVisible(count > 0);
        }
    }

    private void setActiveNav(Button button) {
        if (activeNav != null) {
            activeNav.getStyleClass().remove("nav-active");
        }
        activeNav = button;
        activeNav.getStyleClass().add("nav-active");
    }

    private void loadView(String fxml, Button navButton) {
        try {
            setActiveNav(navButton);
            Node view = FXMLLoader.load(
                    Objects.requireNonNull(getClass().getResource("/ma/enset/campusservices/" + fxml)));
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML public void showDashboard() { loadView("dashboard.fxml", navDashboard); }
    @FXML public void showBibliotheque() { loadView("bibliotheque.fxml", navBibliotheque); }
    @FXML public void showEmprunts() { loadView("emprunts.fxml", navEmprunts); }
    @FXML public void showReservations() { loadView("reservations.fxml", navReservations); }
    @FXML public void showDemandes() { loadView("demandes.fxml", navDemandes); }
    @FXML public void showNotifications() {
        loadView("notifications.fxml", navNotifications);
        updateNotifBadge();
    }

    @FXML
    private void handleLogout() {
        authService.logout();
        try {
            MainApp.afficherLogin();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
