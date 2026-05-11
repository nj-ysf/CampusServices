package ma.enset.campusservices.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ma.enset.campusservices.model.*;
import ma.enset.campusservices.service.BibliothequeService;

public class AdminCatalogueController {

    // ── Tableau ressources ────────────────────────────────────────────────
    @FXML private TableView<Ressource>         tableRes;
    @FXML private TableColumn<Ressource,String> colType;
    @FXML private TableColumn<Ressource,String> colTitre;
    @FXML private TableColumn<Ressource,String> colAuteur;
    @FXML private TableColumn<Ressource,String> colCatalogue;
    @FXML private TableColumn<Ressource,String> colDispo;
    @FXML private TableColumn<Ressource,String> colDetails;

    // ── Formulaire ajout ──────────────────────────────────────────────────
    @FXML private ComboBox<String>    typeCombo;
    @FXML private TextField           titreField;
    @FXML private TextField           auteurField;
    @FXML private ComboBox<Catalogue> catalogueCombo;

    // Livre
    @FXML private TextField isbnField;
    @FXML private TextField pagesField;
    @FXML private TextField editeurField;

    // Mémoire
    @FXML private TextField specialiteField;
    @FXML private TextField anneeField;
    @FXML private TextField encadrantField;

    @FXML private Button btnAjouter;
    @FXML private Button btnSupprimer;
    @FXML private Label  statusLabel;

    private final BibliothequeService biblio = BibliothequeService.getInstance();

    @FXML
    public void initialize() {
        colType.setCellValueFactory(c      -> new SimpleStringProperty(c.getValue().getType()));
        colTitre.setCellValueFactory(c     -> new SimpleStringProperty(c.getValue().getTitre()));
        colAuteur.setCellValueFactory(c    -> new SimpleStringProperty(c.getValue().getAuteur()));
        colCatalogue.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCatalogueNom()));
        colDispo.setCellValueFactory(c     ->
                new SimpleStringProperty(c.getValue().isDisponible() ? "✔" : "✘"));
        colDetails.setCellValueFactory(c   -> new SimpleStringProperty(c.getValue().getDescription()));

        typeCombo.setItems(FXCollections.observableArrayList("Livre", "Mémoire"));
        typeCombo.setValue("Livre");
        typeCombo.setOnAction(e -> toggleChamps());

        catalogueCombo.setItems(FXCollections.observableArrayList(biblio.getCatalogues()));

        btnSupprimer.setDisable(true);
        tableRes.getSelectionModel().selectedItemProperty().addListener(
                (o, old, sel) -> btnSupprimer.setDisable(sel == null || !sel.isDisponible()));

        charger();
        toggleChamps();
    }

    private void toggleChamps() {
        boolean livre = "Livre".equals(typeCombo.getValue());
        isbnField.setDisable(!livre);
        pagesField.setDisable(!livre);
        editeurField.setDisable(!livre);
        specialiteField.setDisable(livre);
        anneeField.setDisable(livre);
        encadrantField.setDisable(livre);
    }

    private void charger() {
        var res = biblio.getRessources();
        tableRes.setItems(FXCollections.observableArrayList(res));
        statusLabel.setText(res.size() + " ressource(s)");
        statusLabel.setStyle("");
    }

    @FXML private void handleAjouter() {
        String titre   = titreField.getText().trim();
        String auteur  = auteurField.getText().trim();
        Catalogue cat  = catalogueCombo.getValue();

        if (titre.isBlank() || auteur.isBlank() || cat == null) {
            showError("Titre, auteur et catalogue sont obligatoires.");
            return;
        }

        Ressource r;
        if ("Livre".equals(typeCombo.getValue())) {
            String isbn    = isbnField.getText().trim();
            String editeur = editeurField.getText().trim();
            int pages = 0;
            try { pages = Integer.parseInt(pagesField.getText().trim()); }
            catch (NumberFormatException ignored) {}
            r = new Livre(0, titre, auteur, true, cat, isbn, pages, editeur);
        } else {
            String specialite = specialiteField.getText().trim();
            String encadrant  = encadrantField.getText().trim();
            int annee = 2024;
            try { annee = Integer.parseInt(anneeField.getText().trim()); }
            catch (NumberFormatException ignored) {}
            r = new Memoire(0, titre, auteur, true, cat, specialite, annee, encadrant);
        }

        biblio.ajouterRessource(r);
        viderFormulaire();
        charger();
        showSuccess("Ressource \"" + titre + "\" ajoutée.");
    }

    @FXML private void handleSupprimer() {
        Ressource sel = tableRes.getSelectionModel().getSelectedItem();
        if (sel == null || !sel.isDisponible()) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer \"" + sel.getTitre() + "\" du catalogue ?",
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                biblio.supprimerRessource(sel.getId());
                charger();
                showSuccess("Ressource supprimée.");
            }
        });
    }

    private void viderFormulaire() {
        titreField.clear(); auteurField.clear();
        isbnField.clear(); pagesField.clear(); editeurField.clear();
        specialiteField.clear(); anneeField.clear(); encadrantField.clear();
    }

    private void showSuccess(String msg) { statusLabel.setText(msg); statusLabel.setStyle("-fx-text-fill: #27ae60;"); }
    private void showError(String msg)   { statusLabel.setText(msg); statusLabel.setStyle("-fx-text-fill: #e74c3c;"); }
}
