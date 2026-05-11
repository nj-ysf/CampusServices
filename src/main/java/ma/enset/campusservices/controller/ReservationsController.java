package ma.enset.campusservices.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ma.enset.campusservices.model.*;
import ma.enset.campusservices.service.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ReservationsController {

    @FXML private TableView<ReservationSalle> tableReservations;
    @FXML private TableColumn<ReservationSalle, String> colId;
    @FXML private TableColumn<ReservationSalle, String> colSalle;
    @FXML private TableColumn<ReservationSalle, String> colDebut;
    @FXML private TableColumn<ReservationSalle, String> colFin;
    @FXML private TableColumn<ReservationSalle, String> colStatut;

    @FXML private ComboBox<Salle> salleCombo;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> heureDebut;
    @FXML private ComboBox<String> heureFin;
    @FXML private Button btnReserver;
    @FXML private Button btnAnnuler;
    @FXML private Label statusLabel;

    private final ReservationService reservationService = ReservationService.getInstance();
    private final AuthService authService = AuthService.getInstance();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> new SimpleStringProperty("#" + c.getValue().getId()));
        colSalle.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSalle().getNom()));
        colDebut.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDateDebutFormatee()));
        colFin.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDateFinFormatee()));
        colStatut.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatut().getLibelle()));

        colStatut.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle(switch (item) {
                    case "Validée" -> "-fx-text-fill: #27ae60; -fx-font-weight: bold;";
                    case "Refusée", "Annulée" -> "-fx-text-fill: #e74c3c; -fx-font-weight: bold;";
                    default -> "-fx-text-fill: #e67e22; -fx-font-weight: bold;";
                });
            }
        });

        salleCombo.setItems(FXCollections.observableArrayList(reservationService.getSalles()));
        datePicker.setValue(LocalDate.now().plusDays(1));

        List<String> heures = List.of("08:00","09:00","10:00","11:00","12:00",
                "13:00","14:00","15:00","16:00","17:00","18:00");
        heureDebut.setItems(FXCollections.observableArrayList(heures));
        heureFin.setItems(FXCollections.observableArrayList(heures));
        heureDebut.setValue("09:00");
        heureFin.setValue("11:00");

        btnAnnuler.setDisable(true);
        tableReservations.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newVal) -> btnAnnuler.setDisable(newVal == null || !newVal.isAnnulable()));

        chargerReservations();
    }

    private void chargerReservations() {
        Etudiant etudiant = authService.getEtudiantCourant();
        List<ReservationSalle> list = reservationService.getReservationsEtudiant(etudiant);
        tableReservations.setItems(FXCollections.observableArrayList(list));
        statusLabel.setText(list.size() + " réservation(s)");
    }

    @FXML
    private void handleReserver() {
        Salle salle = salleCombo.getValue();
        LocalDate date = datePicker.getValue();
        String hd = heureDebut.getValue();
        String hf = heureFin.getValue();

        if (salle == null || date == null || hd == null || hf == null) {
            showError("Veuillez remplir tous les champs.");
            return;
        }

        LocalDateTime debut = parseDateTime(date, hd);
        LocalDateTime fin = parseDateTime(date, hf);

        if (!fin.isAfter(debut)) {
            showError("L'heure de fin doit être après l'heure de début.");
            return;
        }
        if (debut.isBefore(LocalDateTime.now())) {
            showError("La réservation doit être dans le futur.");
            return;
        }

        try {
            Etudiant etudiant = authService.getEtudiantCourant();
            reservationService.creerReservation(etudiant, salle, debut, fin);
            chargerReservations();
            showInfo("Réservation soumise avec succès !");
        } catch (IllegalStateException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleAnnuler() {
        ReservationSalle selected = tableReservations.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Annuler la réservation pour \"" + selected.getSalle().getNom() + "\" ?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmer l'annulation");
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                reservationService.annulerReservation(selected);
                chargerReservations();
                showInfo("Réservation annulée.");
            }
        });
    }

    private LocalDateTime parseDateTime(LocalDate date, String heure) {
        String[] parts = heure.split(":");
        return date.atTime(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }

    private void showInfo(String msg) {
        statusLabel.setText(msg);
        statusLabel.setStyle("-fx-text-fill: #27ae60;");
    }

    private void showError(String msg) {
        statusLabel.setText(msg);
        statusLabel.setStyle("-fx-text-fill: #e74c3c;");
    }
}
