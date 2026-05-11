package ma.enset.campusservices.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ma.enset.campusservices.dao.IDemandeDAO;
import ma.enset.campusservices.dao.impl.DemandeDAOImpl;
import ma.enset.campusservices.model.DemandeAdministrative;
import ma.enset.campusservices.model.enums.StatutDemande;
import ma.enset.campusservices.service.NotificationService;

public class AdminDemandesController {

    @FXML private TableView<DemandeAdministrative>         tableDem;
    @FXML private TableColumn<DemandeAdministrative,String> colId;
    @FXML private TableColumn<DemandeAdministrative,String> colEtudiant;
    @FXML private TableColumn<DemandeAdministrative,String> colType;
    @FXML private TableColumn<DemandeAdministrative,String> colStatut;
    @FXML private TableColumn<DemandeAdministrative,String> colDate;
    @FXML private TableColumn<DemandeAdministrative,String> colDescription;
    @FXML private ComboBox<String>                          filtreStatut;
    @FXML private TextArea                                  commentaireArea;
    @FXML private Button                                    btnValider;
    @FXML private Button                                    btnRefuser;
    @FXML private Button                                    btnEnTraitement;
    @FXML private Label                                     statusLabel;

    private final IDemandeDAO         demandeDAO = new DemandeDAOImpl();
    private final NotificationService notifSvc   = NotificationService.getInstance();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c          -> new SimpleStringProperty("#" + c.getValue().getId()));
        colEtudiant.setCellValueFactory(c    -> new SimpleStringProperty(c.getValue().getEtudiant().getNomComplet()));
        colType.setCellValueFactory(c        -> new SimpleStringProperty(c.getValue().getType().getLibelle()));
        colStatut.setCellValueFactory(c      -> new SimpleStringProperty(c.getValue().getStatut().getLibelle()));
        colDate.setCellValueFactory(c        -> new SimpleStringProperty(c.getValue().getDateCreationFormatee()));
        colDescription.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescription()));

        colorerStatut(colStatut);

        filtreStatut.setItems(FXCollections.observableArrayList(
                "Toutes", "Soumise", "En traitement", "Validée", "Refusée"));
        filtreStatut.setValue("Toutes");
        filtreStatut.setOnAction(e -> charger());

        boolean[] dis = {true};
        btnValider.setDisable(true);
        btnRefuser.setDisable(true);
        btnEnTraitement.setDisable(true);

        tableDem.getSelectionModel().selectedItemProperty().addListener((o, old, sel) -> {
            boolean active = sel != null
                    && (sel.getStatut() == StatutDemande.SOUMISE
                    ||  sel.getStatut() == StatutDemande.EN_TRAITEMENT);
            btnValider.setDisable(!active);
            btnRefuser.setDisable(!active);
            btnEnTraitement.setDisable(sel == null || sel.getStatut() != StatutDemande.SOUMISE);
            if (sel != null && sel.getCommentaireAdmin() != null)
                commentaireArea.setText(sel.getCommentaireAdmin());
            else
                commentaireArea.clear();
        });

        charger();
    }

    private void charger() {
        String filtre = filtreStatut.getValue();
        var list = demandeDAO.findAll().stream()
                .filter(d -> "Toutes".equals(filtre)
                        || d.getStatut().getLibelle().equals(filtre))
                .toList();
        tableDem.setItems(FXCollections.observableArrayList(list));
        statusLabel.setText(list.size() + " demande(s)");
        statusLabel.setStyle("");
    }

    @FXML private void handleEnTraitement() {
        DemandeAdministrative sel = tableDem.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        demandeDAO.updateStatut(sel.getId(), StatutDemande.EN_TRAITEMENT, null);
        notifSvc.creerNotification(sel.getEtudiant(),
                "Votre demande \"" + sel.getType().getLibelle() + "\" est en cours de traitement.", "demande");
        charger();
        statusLabel.setText("Demande #" + sel.getId() + " mise en traitement.");
        statusLabel.setStyle("-fx-text-fill: #3498db;");
    }

    @FXML private void handleValider() {
        DemandeAdministrative sel = tableDem.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        String commentaire = commentaireArea.getText().trim();
        demandeDAO.updateStatut(sel.getId(), StatutDemande.VALIDEE, commentaire);
        notifSvc.creerNotification(sel.getEtudiant(),
                "Votre demande \"" + sel.getType().getLibelle() + "\" a été validée ✔", "demande");
        charger();
        statusLabel.setText("Demande #" + sel.getId() + " validée.");
        statusLabel.setStyle("-fx-text-fill: #27ae60;");
    }

    @FXML private void handleRefuser() {
        DemandeAdministrative sel = tableDem.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        String commentaire = commentaireArea.getText().trim();
        if (commentaire.isBlank()) {
            statusLabel.setText("Veuillez saisir un motif de refus.");
            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }
        demandeDAO.updateStatut(sel.getId(), StatutDemande.REFUSEE, commentaire);
        notifSvc.creerNotification(sel.getEtudiant(),
                "Votre demande \"" + sel.getType().getLibelle() + "\" a été refusée. Motif : " + commentaire, "demande");
        charger();
        statusLabel.setText("Demande #" + sel.getId() + " refusée.");
        statusLabel.setStyle("-fx-text-fill: #e74c3c;");
    }

    private static void colorerStatut(TableColumn<DemandeAdministrative,String> col) {
        col.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle(switch (item) {
                    case "Validée"      -> "-fx-text-fill: #27ae60; -fx-font-weight: bold;";
                    case "Refusée"      -> "-fx-text-fill: #e74c3c; -fx-font-weight: bold;";
                    case "En traitement"-> "-fx-text-fill: #3498db; -fx-font-weight: bold;";
                    default             -> "-fx-text-fill: #e67e22; -fx-font-weight: bold;";
                });
            }
        });
    }
}
