package ma.enset.campusservices.service;

import ma.enset.campusservices.dao.*;
import ma.enset.campusservices.dao.impl.*;
import ma.enset.campusservices.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service gérant les opérations de bibliothèque.
 * Utilise IRessourceDAO, ICatalogueDAO, IEmpruntDAO (PostgreSQL).
 */
public class BibliothequeService implements IGestionable<Emprunt> {

    private static BibliothequeService instance;

    private final IRessourceDAO       ressourceDAO;
    private final ICatalogueDAO       catalogueDAO;
    private final IEmpruntDAO         empruntDAO;
    private final NotificationService notificationService;

    private BibliothequeService() {
        this.ressourceDAO       = new RessourceDAOImpl();
        this.catalogueDAO       = new CatalogueDAOImpl();
        this.empruntDAO         = new EmpruntDAOImpl();
        this.notificationService = NotificationService.getInstance();
    }

    public static BibliothequeService getInstance() {
        if (instance == null) instance = new BibliothequeService();
        return instance;
    }

    public List<Catalogue> getCatalogues() {
        return catalogueDAO.findAll();
    }

    public List<Ressource> getRessources() {
        return ressourceDAO.findAll();
    }

    public List<Ressource> rechercherRessources(String motCle, String type, Boolean disponibleSeulement) {
        return ressourceDAO.findAll().stream()
                .filter(r -> motCle == null || motCle.isBlank()
                        || r.getTitre().toLowerCase().contains(motCle.toLowerCase())
                        || r.getAuteur().toLowerCase().contains(motCle.toLowerCase()))
                .filter(r -> type == null || type.isBlank() || r.getType().equalsIgnoreCase(type))
                .filter(r -> disponibleSeulement == null || !disponibleSeulement || r.isDisponible())
                .collect(Collectors.toList());
    }

    public List<Emprunt> getEmpruntsEtudiant(Etudiant etudiant) {
        return empruntDAO.findByEtudiant(etudiant.getId());
    }

    public List<Emprunt> getEmpruntsActifs(Etudiant etudiant) {
        return empruntDAO.findActifsByEtudiant(etudiant.getId());
    }

    public Emprunt emprunterRessource(Etudiant etudiant, Ressource ressource) {
        if (!ressource.isDisponible()) {
            throw new IllegalStateException("La ressource \"" + ressource.getTitre() + "\" n'est pas disponible.");
        }
        Emprunt emprunt = new Emprunt(0, etudiant, ressource, LocalDate.now());
        empruntDAO.save(emprunt);
        ressourceDAO.updateDisponibilite(ressource.getId(), false);
        ressource.setDisponible(false);

        notificationService.creerNotification(etudiant,
                "Emprunt de \"" + ressource.getTitre() + "\" enregistré avec succès.", "emprunt");
        return emprunt;
    }

    public void retournerRessource(Emprunt emprunt) {
        if (!emprunt.isEnCours()) {
            throw new IllegalStateException("Cet emprunt a déjà été retourné.");
        }
        LocalDate today = LocalDate.now();
        empruntDAO.retourner(emprunt.getId(), today);
        emprunt.setDateRetour(today);
        ressourceDAO.updateDisponibilite(emprunt.getRessource().getId(), true);
        emprunt.getRessource().setDisponible(true);

        notificationService.creerNotification(emprunt.getEtudiant(),
                "Retour de \"" + emprunt.getRessource().getTitre() + "\" enregistré.", "emprunt");
    }

    public void ajouterRessource(Ressource ressource) {
        ressourceDAO.save(ressource);
    }

    public void supprimerRessource(int id) {
        ressourceDAO.delete(id);
    }

    @Override
    public Emprunt creer(Emprunt emprunt) {
        return empruntDAO.save(emprunt);
    }

    @Override
    public Optional<Emprunt> trouverParId(int id) {
        return empruntDAO.findById(id);
    }

    @Override
    public List<Emprunt> trouverTous() {
        return empruntDAO.findAll();
    }

    @Override
    public void supprimer(int id) {
        // non exposé côté étudiant
    }
}
