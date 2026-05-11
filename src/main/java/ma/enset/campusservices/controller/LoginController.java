package ma.enset.campusservices.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import ma.enset.campusservices.MainApp;
import ma.enset.campusservices.model.Admin;
import ma.enset.campusservices.model.Etudiant;
import ma.enset.campusservices.service.AuthService;

public class LoginController {

    @FXML private TextField     emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button        loginButton;
    @FXML private Label         errorLabel;
    @FXML private Label         versionLabel;

    private final AuthService authService = AuthService.getInstance();

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        versionLabel.setText("CampusServices v1.0 — ENSET");

        passwordField.setOnKeyPressed(ev -> {
            if (ev.getCode() == KeyCode.ENTER) handleLogin();
        });
        emailField.setOnKeyPressed(ev -> {
            if (ev.getCode() == KeyCode.ENTER) passwordField.requestFocus();
        });
    }

    @FXML
    private void handleLogin() {
        String email    = emailField.getText();
        String password = passwordField.getText();

        errorLabel.setVisible(false);

        if (email.isBlank() || password.isBlank()) {
            showError("Veuillez remplir tous les champs.");
            return;
        }

        // 1. Essayer admin / personnel
        Admin admin = authService.loginAdmin(email.trim(), password);
        if (admin != null) {
            try {
                MainApp.afficherAdmin();
            } catch (Exception ex) {
                showError("Erreur chargement panneau admin.");
                ex.printStackTrace();
            }
            return;
        }

        // 2. Essayer étudiant
        Etudiant etudiant = authService.login(email.trim(), password);
        if (etudiant != null) {
            try {
                MainApp.afficherPrincipal();
            } catch (Exception ex) {
                showError("Erreur lors du chargement de l'application.");
                ex.printStackTrace();
            }
        } else {
            showError("Email ou mot de passe incorrect.");
            passwordField.clear();
            passwordField.requestFocus();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
