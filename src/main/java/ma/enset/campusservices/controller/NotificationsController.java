package ma.enset.campusservices.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ma.enset.campusservices.model.*;
import ma.enset.campusservices.service.*;

import java.util.List;

public class NotificationsController {

    @FXML private ListView<Notification> listNotifications;
    @FXML private Button btnMarquerLues;
    @FXML private Label statusLabel;

    private final NotificationService notificationService = NotificationService.getInstance();
    private final AuthService authService = AuthService.getInstance();

    @FXML
    public void initialize() {
        listNotifications.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Notification item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    String prefix = item.isLu() ? "   " : "● ";
                    setText(prefix + item.getMessage() + "\n      " + item.getCreatedAtFormatee());
                    setStyle(item.isLu()
                            ? "-fx-text-fill: #888; -fx-padding: 8 12;"
                            : "-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-padding: 8 12;");
                }
            }
        });

        listNotifications.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selected) -> {
                    if (selected != null && !selected.isLu()) {
                        notificationService.marquerCommeLue(selected);
                        listNotifications.refresh();
                        updateStatus();
                    }
                });

        chargerNotifications();
    }

    private void chargerNotifications() {
        Etudiant etudiant = authService.getEtudiantCourant();
        List<Notification> list = notificationService.getNotificationsEtudiant(etudiant);
        listNotifications.setItems(FXCollections.observableArrayList(list));
        updateStatus();
    }

    private void updateStatus() {
        Etudiant etudiant = authService.getEtudiantCourant();
        long nonLues = notificationService.compterNonLues(etudiant);
        statusLabel.setText(nonLues + " notification(s) non lue(s)");
    }

    @FXML
    private void handleMarquerToutesLues() {
        Etudiant etudiant = authService.getEtudiantCourant();
        notificationService.marquerToutesCommeLues(etudiant);
        listNotifications.refresh();
        updateStatus();
    }
}
