package ma.enset.campusservices.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ma.enset.campusservices.dao.IReservationDAO;
import ma.enset.campusservices.dao.impl.ReservationDAOImpl;
import ma.enset.campusservices.model.ReservationSalle;
import ma.enset.campusservices.model.enums.StatutReservation;
import ma.enset.campusservices.service.NotificationService;

public class AdminReservationsController {

    @FXML private TableView<ReservationSalle>         tableRes;
    @FXML private TableColumn<ReservationSalle,String> colId;
    @FXML private TableColumn<ReservationSalle,String> colEtudiant;
    @FXML private TableColumn<ReservationSalle,String> colSalle;
    @FXML private TableColumn<ReservationSalle,String> colDebut;
    @FXML private TableColumn<ReservationSalle,String> colFin;
    @FXML private TableColumn<ReservationSalle,String> colStatut;
    @FXML private ComboBox<String>                     filtreStatut;
    @FXML private Button                               btnValider;
    @FXML private Button                               btnRefuser;
    @FXML private Label                                statusLabel;

    private final IReservationDAO    reservationDAO = new ReservationDAOImpl();
    private final NotificationService notifSvc      = NotificationService.getInstance();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c       -> new SimpleStringProperty("#" + c.getValue().getId()));
        colEtudiant.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEtudiant().getNomComplet()));
        colSalle.setCellValueFactory(c    -> new SimpleStringProperty(c.getValue().getSalle().getNom()));
        colDebut.setCellValueFactory(c    -> new SimpleStringProperty(c.getValue().getDateDebutFormatee()));
        colFin.setCellValueFactory(c      -> new SimpleStringProperty(c.getValue().getDateFinFormatee()));
        colStatut.setCellValueFactory(c   -> new SimpleStringProperty(c.getValue().getStatut().getLibelle()));

        colorerStatut(colStatut);

        filtreStatut.setItems(FXCollections.observableArrayList(
                "Toutes", "En attente", "Confirmée", "Annulée"));
        filtreStatut.setValue("Toutes");
        filtreStatut.setOnAction(e -> charger());

        btnValider.setDisable(true);
        btnRefuser.setDisable(true);
        tableRes.getSelectionModel().selectedItemProperty().addListener((o, old, sel) -> {
            boolean attente = sel != null && sel.getStatut() == StatutReservation.EN_ATTENTE;
            btnValider.setDisable(!attente);
            btnRefuser.setDisable(!attente);
        });

        charger();
    }

    private void charger() {
        String filtre = filtreStatut.getValue();
        var list = reservationDAO.findAll().stream()
                .filter(r -> "Toutes".equals(filtre)
                        || r.getStatut().getLibelle().equals(filtre))
                .toList();
        tableRes.setItems(FXCollections.observableArrayList(list));
        statusLabel.setText(list.size() + " réservation(s)");
        statusLabel.setStyle("");
    }

    @FXML private void handleValider() {
        ReservationSalle sel = tableRes.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        reservationDAO.updateStatut(sel.getId(), StatutReservation.CONFIRMEE);
        notifSvc.creerNotification(sel.getEtudiant(),
                "Votre réservation pour \"" + sel.getSalle().getNom() + "\" a été confirmée.", "reservation");
        charger();
        statusLabel.setText("Réservation #" + sel.getId() + " confirmée.");
        statusLabel.setStyle("-fx-text-fill: #27ae60;");
    }

    @FXML private void handleRefuser() {
        ReservationSalle sel = tableRes.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        reservationDAO.updateStatut(sel.getId(), StatutReservation.ANNULEE);
        notifSvc.creerNotification(sel.getEtudiant(),
                "Votre réservation pour \"" + sel.getSalle().getNom() + "\" a été annulée.", "reservation");
        charger();
        statusLabel.setText("Réservation #" + sel.getId() + " annulée.");
        statusLabel.setStyle("-fx-text-fill: #e74c3c;");
    }

    private static void colorerStatut(TableColumn<ReservationSalle,String> col) {
        col.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle(switch (item) {
                    case "Confirmée" -> "-fx-text-fill: #27ae60; -fx-font-weight: bold;";
                    case "Annulée"   -> "-fx-text-fill: #e74c3c; -fx-font-weight: bold;";
                    default          -> "-fx-text-fill: #e67e22; -fx-font-weight: bold;";
                });
            }
        });
    }
}
