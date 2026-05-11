package ma.enset.campusservices.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ma.enset.campusservices.model.*;
import ma.enset.campusservices.service.*;

import java.util.List;

public class BibliothequeController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> typeFilter;
    @FXML private CheckBox disponibleOnly;
    @FXML private TableView<Ressource> tableRessources;
    @FXML private TableColumn<Ressource, String> colType;
    @FXML private TableColumn<Ressource, String> colTitre;
    @FXML private TableColumn<Ressource, String> colAuteur;
    @FXML private TableColumn<Ressource, String> colCatalogue;
    @FXML private TableColumn<Ressource, String> colDisponible;
    @FXML private TableColumn<Ressource, String> colDescription;
    @FXML private Button btnEmprunter;
    @FXML private Label statusLabel;

    private final BibliothequeService bibliothequeService = BibliothequeService.getInstance();
    private final AuthService authService = AuthService.getInstance();

    @FXML
    public void initialize() {
        typeFilter.setItems(FXCollections.observableArrayList("Tous", "Livre", "Mémoire"));
        typeFilter.setValue("Tous");

        colType.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getType()));
        colTitre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitre()));
        colAuteur.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAuteur()));
        colCatalogue.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCatalogueNom()));
        colDisponible.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().isDisponible() ? "✔ Disponible" : "✘ Indisponible"));
        colDescription.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescription()));

        colDisponible.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle(item.startsWith("✔")
                            ? "-fx-text-fill: #27ae60; -fx-font-weight: bold;"
                            : "-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                }
            }
        });

        btnEmprunter.setDisable(true);
        tableRessources.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newVal) -> btnEmprunter.setDisable(newVal == null || !newVal.isDisponible()));

        chargerRessources();
    }

    @FXML
    private void handleRechercher() {
        chargerRessources();
    }

    private void chargerRessources() {
        String motCle = searchField.getText();
        String type = "Tous".equals(typeFilter.getValue()) ? null : typeFilter.getValue();
        boolean disponible = disponibleOnly.isSelected();

        List<Ressource> resultats = bibliothequeService.rechercherRessources(
                motCle, type, disponible ? true : null);
        tableRessources.setItems(FXCollections.observableArrayList(resultats));
        statusLabel.setText(resultats.size() + " ressource(s) trouvée(s)");
    }

    @FXML
    private void handleEmprunter() {
        Ressource selected = tableRessources.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Emprunter \"" + selected.getTitre() + "\" ?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmer l'emprunt");
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                try {
                    Etudiant etudiant = authService.getEtudiantCourant();
                    bibliothequeService.emprunterRessource(etudiant, selected);
                    chargerRessources();
                    showInfo("Emprunt enregistré avec succès !");
                } catch (IllegalStateException e) {
                    showError(e.getMessage());
                }
            }
        });
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
