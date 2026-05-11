package ma.enset.campusservices.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ma.enset.campusservices.model.*;
import ma.enset.campusservices.model.enums.TypeDemande;
import ma.enset.campusservices.service.*;

import java.util.List;

public class DemandesController {

    @FXML private TableView<DemandeAdministrative> tableDemandes;
    @FXML private TableColumn<DemandeAdministrative, String> colId;
    @FXML private TableColumn<DemandeAdministrative, String> colType;
    @FXML private TableColumn<DemandeAdministrative, String> colStatut;
    @FXML private TableColumn<DemandeAdministrative, String> colDate;
    @FXML private TableColumn<DemandeAdministrative, String> colDescription;

    @FXML private ComboBox<TypeDemande> typeCombo;
    @FXML private TextArea descriptionArea;
    @FXML private Button btnSoumettre;
    @FXML private Label statusLabel;

    private final DemandeService demandeService = DemandeService.getInstance();
    private final AuthService authService = AuthService.getInstance();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> new SimpleStringProperty("#" + c.getValue().getId()));
        colType.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getType().getLibelle()));
        colStatut.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatut().getLibelle()));
        colDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDateCreationFormatee()));
        colDescription.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescription()));

        colStatut.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle(switch (item) {
                    case "Validée" -> "-fx-text-fill: #27ae60; -fx-font-weight: bold;";
                    case "Refusée" -> "-fx-text-fill: #e74c3c; -fx-font-weight: bold;";
                    case "En traitement" -> "-fx-text-fill: #3498db; -fx-font-weight: bold;";
                    default -> "-fx-text-fill: #e67e22; -fx-font-weight: bold;";
                });
            }
        });

        typeCombo.setItems(FXCollections.observableArrayList(TypeDemande.values()));
        typeCombo.setValue(TypeDemande.CERTIFICAT_SCOLARITE);

        chargerDemandes();
    }

    private void chargerDemandes() {
        Etudiant etudiant = authService.getEtudiantCourant();
        List<DemandeAdministrative> list = demandeService.getDemandesEtudiant(etudiant);
        tableDemandes.setItems(FXCollections.observableArrayList(list));
        statusLabel.setText(list.size() + " demande(s)");
    }

    @FXML
    private void handleSoumettre() {
        TypeDemande type = typeCombo.getValue();
        String description = descriptionArea.getText();

        if (type == null || description == null || description.isBlank()) {
            showError("Veuillez sélectionner un type et saisir une description.");
            return;
        }

        try {
            Etudiant etudiant = authService.getEtudiantCourant();
            demandeService.soumettreDemandeAdministrative(etudiant, type, description);
            descriptionArea.clear();
            chargerDemandes();
            showInfo("Demande soumise avec succès !");
        } catch (Exception e) {
            showError(e.getMessage());
        }
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
