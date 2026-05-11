package ma.enset.campusservices.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ma.enset.campusservices.dao.IEtudiantDAO;
import ma.enset.campusservices.dao.impl.EtudiantDAOImpl;
import ma.enset.campusservices.model.Etudiant;
import ma.enset.campusservices.model.enums.StatutEtudiant;
import ma.enset.campusservices.service.BibliothequeService;

public class AdminEtudiantsController {

    @FXML private TableView<Etudiant>         tableEtudiants;
    @FXML private TableColumn<Etudiant,String> colId;
    @FXML private TableColumn<Etudiant,String> colNom;
    @FXML private TableColumn<Etudiant,String> colEmail;
    @FXML private TableColumn<Etudiant,String> colFiliere;
    @FXML private TableColumn<Etudiant,String> colStatut;
    @FXML private TableColumn<Etudiant,String> colEmprunts;
    @FXML private TextField                    searchField;
    @FXML private Button                       btnActiver;
    @FXML private Button                       btnDesactiver;
    @FXML private Label                        statusLabel;

    private final IEtudiantDAO        etudiantDAO = new EtudiantDAOImpl();
    private final BibliothequeService biblio      = BibliothequeService.getInstance();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c      -> new SimpleStringProperty("#" + c.getValue().getId()));
        colNom.setCellValueFactory(c     -> new SimpleStringProperty(c.getValue().getNomComplet()));
        colEmail.setCellValueFactory(c   -> new SimpleStringProperty(c.getValue().getEmail()));
        colFiliere.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFiliere()));
        colStatut.setCellValueFactory(c  -> new SimpleStringProperty(c.getValue().getStatut().getLibelle()));
        colEmprunts.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(biblio.getEmpruntsActifs(c.getValue()).size())));

        colStatut.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle("Actif".equals(item)
                        ? "-fx-text-fill: #27ae60; -fx-font-weight: bold;"
                        : "-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            }
        });

        btnActiver.setDisable(true);
        btnDesactiver.setDisable(true);
        tableEtudiants.getSelectionModel().selectedItemProperty().addListener((o, old, sel) -> {
            btnActiver.setDisable(sel == null || sel.isActif());
            btnDesactiver.setDisable(sel == null || !sel.isActif());
        });

        charger();
    }

    private void charger() {
        String q = searchField.getText().toLowerCase();
        var list = etudiantDAO.findAll().stream()
                .filter(e -> q.isBlank()
                        || e.getNomComplet().toLowerCase().contains(q)
                        || e.getEmail().toLowerCase().contains(q)
                        || e.getFiliere().toLowerCase().contains(q))
                .toList();
        tableEtudiants.setItems(FXCollections.observableArrayList(list));
        statusLabel.setText(list.size() + " étudiant(s)");
    }

    @FXML private void handleRechercher() { charger(); }

    @FXML private void handleActiver() {
        Etudiant sel = tableEtudiants.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        etudiantDAO.updateStatut(sel.getId(), StatutEtudiant.ACTIF);
        charger();
        statusLabel.setText("Compte de " + sel.getNomComplet() + " activé.");
        statusLabel.setStyle("-fx-text-fill: #27ae60;");
    }

    @FXML private void handleDesactiver() {
        Etudiant sel = tableEtudiants.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        Alert c = new Alert(Alert.AlertType.CONFIRMATION,
                "Désactiver le compte de " + sel.getNomComplet() + " ?",
                ButtonType.YES, ButtonType.NO);
        c.setHeaderText(null);
        c.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                etudiantDAO.updateStatut(sel.getId(), StatutEtudiant.INACTIF);
                charger();
                statusLabel.setText("Compte de " + sel.getNomComplet() + " désactivé.");
                statusLabel.setStyle("-fx-text-fill: #e67e22;");
            }
        });
    }
}
