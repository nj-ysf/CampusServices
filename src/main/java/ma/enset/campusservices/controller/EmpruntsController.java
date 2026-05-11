package ma.enset.campusservices.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ma.enset.campusservices.model.*;
import ma.enset.campusservices.service.*;

import java.util.List;

public class EmpruntsController {

    @FXML private TableView<Emprunt> tableEmprunts;
    @FXML private TableColumn<Emprunt, String> colId;
    @FXML private TableColumn<Emprunt, String> colRessource;
    @FXML private TableColumn<Emprunt, String> colType;
    @FXML private TableColumn<Emprunt, String> colDateEmprunt;
    @FXML private TableColumn<Emprunt, String> colStatut;
    @FXML private Button btnRetourner;
    @FXML private Label statusLabel;

    private final BibliothequeService bibliothequeService = BibliothequeService.getInstance();
    private final AuthService authService = AuthService.getInstance();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> new SimpleStringProperty("#" + c.getValue().getId()));
        colRessource.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRessource().getTitre()));
        colType.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRessource().getType()));
        colDateEmprunt.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDateEmpruntFormatee()));
        colStatut.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatutLibelle()));

        colStatut.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle(item.startsWith("En cours")
                            ? "-fx-text-fill: #e67e22; -fx-font-weight: bold;"
                            : "-fx-text-fill: #27ae60;");
                }
            }
        });

        btnRetourner.setDisable(true);
        tableEmprunts.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newVal) -> btnRetourner.setDisable(newVal == null || !newVal.isEnCours()));

        chargerEmprunts();
    }

    private void chargerEmprunts() {
        Etudiant etudiant = authService.getEtudiantCourant();
        List<Emprunt> emprunts = bibliothequeService.getEmpruntsEtudiant(etudiant);
        tableEmprunts.setItems(FXCollections.observableArrayList(emprunts));
        statusLabel.setText(emprunts.size() + " emprunt(s) au total");
    }

    @FXML
    private void handleRetourner() {
        Emprunt selected = tableEmprunts.getSelectionModel().getSelectedItem();
        if (selected == null || !selected.isEnCours()) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Retourner \"" + selected.getRessource().getTitre() + "\" ?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmer le retour");
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                bibliothequeService.retournerRessource(selected);
                chargerEmprunts();
                statusLabel.setText("Retour enregistré avec succès !");
                statusLabel.setStyle("-fx-text-fill: #27ae60;");
            }
        });
    }
}
