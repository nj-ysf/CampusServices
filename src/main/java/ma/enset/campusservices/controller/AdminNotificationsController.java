package ma.enset.campusservices.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ma.enset.campusservices.dao.IEtudiantDAO;
import ma.enset.campusservices.dao.impl.EtudiantDAOImpl;
import ma.enset.campusservices.model.Etudiant;
import ma.enset.campusservices.service.NotificationService;

public class AdminNotificationsController {

    @FXML private ComboBox<String>  destinataireCombo;
    @FXML private ComboBox<Etudiant> etudiantCombo;
    @FXML private TextArea          messageArea;
    @FXML private Button            btnEnvoyer;
    @FXML private Label             statusLabel;
    @FXML private ListView<String>  listHistorique;

    private final IEtudiantDAO        etudiantDAO = new EtudiantDAOImpl();
    private final NotificationService notifSvc    = NotificationService.getInstance();

    @FXML
    public void initialize() {
        destinataireCombo.setItems(FXCollections.observableArrayList(
                "Tous les étudiants", "Étudiant spécifique"));
        destinataireCombo.setValue("Tous les étudiants");
        destinataireCombo.setOnAction(e -> {
            etudiantCombo.setDisable("Tous les étudiants".equals(destinataireCombo.getValue()));
        });

        etudiantCombo.setItems(FXCollections.observableArrayList(etudiantDAO.findAll()));
        etudiantCombo.setDisable(true);

        chargerHistorique();
    }

    @FXML private void handleEnvoyer() {
        String msg = messageArea.getText().trim();
        if (msg.isBlank()) {
            statusLabel.setText("Le message ne peut pas être vide.");
            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }

        if ("Tous les étudiants".equals(destinataireCombo.getValue())) {
            var tous = etudiantDAO.findAll();
            tous.forEach(e -> notifSvc.creerNotification(e, msg, "admin"));
            statusLabel.setText("Notification envoyée à " + tous.size() + " étudiant(s).");
        } else {
            Etudiant etudiant = etudiantCombo.getValue();
            if (etudiant == null) {
                statusLabel.setText("Sélectionnez un étudiant.");
                statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                return;
            }
            notifSvc.creerNotification(etudiant, msg, "admin");
            statusLabel.setText("Notification envoyée à " + etudiant.getNomComplet() + ".");
        }

        statusLabel.setStyle("-fx-text-fill: #27ae60;");
        messageArea.clear();
        chargerHistorique();
    }

    private void chargerHistorique() {
        var hist = notifSvc.findAll().stream()
                .filter(n -> "admin".equals(n.getType()))
                .map(n -> "[" + n.getCreatedAtFormatee() + "] "
                        + n.getEtudiant().getNomComplet() + " — " + n.getMessage())
                .toList();
        listHistorique.setItems(FXCollections.observableArrayList(hist));
    }
}
