package ma.enset.campusservices.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import ma.enset.campusservices.MainApp;
import ma.enset.campusservices.model.Admin;
import ma.enset.campusservices.service.AuthService;

import java.io.IOException;
import java.util.Objects;

public class AdminMainController {

    @FXML private Label    adminNameLabel;
    @FXML private Label    adminRoleLabel;
    @FXML private StackPane contentArea;

    @FXML private Button navDashboard;
    @FXML private Button navEtudiants;
    @FXML private Button navReservations;
    @FXML private Button navDemandes;
    @FXML private Button navCatalogue;
    @FXML private Button navNotifications;

    private final AuthService authService = AuthService.getInstance();
    private Button activeNav;

    @FXML
    public void initialize() {
        Admin admin = authService.getAdminCourant();
        if (admin != null) {
            adminNameLabel.setText(admin.getNomComplet());
            adminRoleLabel.setText(admin.getRole().getLibelle());
        }
        showDashboard();
    }

    private void setActive(Button btn) {
        if (activeNav != null) activeNav.getStyleClass().remove("nav-active");
        activeNav = btn;
        activeNav.getStyleClass().add("nav-active");
    }

    private void loadView(String fxml, Button btn) {
        try {
            setActive(btn);
            Node view = FXMLLoader.load(
                    Objects.requireNonNull(
                            getClass().getResource("/ma/enset/campusservices/" + fxml)));
            contentArea.getChildren().setAll(view);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML public void showDashboard()     { loadView("admin-dashboard.fxml",     navDashboard);     }
    @FXML public void showEtudiants()     { loadView("admin-etudiants.fxml",     navEtudiants);     }
    @FXML public void showReservations()  { loadView("admin-reservations.fxml",  navReservations);  }
    @FXML public void showDemandes()      { loadView("admin-demandes.fxml",      navDemandes);      }
    @FXML public void showCatalogue()     { loadView("admin-catalogue.fxml",     navCatalogue);     }
    @FXML public void showNotifications() { loadView("admin-notifications.fxml", navNotifications); }

    @FXML
    private void handleLogout() {
        authService.logout();
        try { MainApp.afficherLogin(); } catch (Exception e) { e.printStackTrace(); }
    }
}
