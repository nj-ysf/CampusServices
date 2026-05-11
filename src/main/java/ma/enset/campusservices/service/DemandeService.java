package ma.enset.campusservices.service;

import ma.enset.campusservices.dao.IDemandeDAO;
import ma.enset.campusservices.dao.impl.DemandeDAOImpl;
import ma.enset.campusservices.model.*;
import ma.enset.campusservices.model.enums.TypeDemande;

import java.util.List;
import java.util.Optional;

/**
 * Service de gestion des demandes administratives.
 * Utilise IDemandeDAO (PostgreSQL).
 */
public class DemandeService implements IGestionable<DemandeAdministrative> {

    private static DemandeService instance;

    private final IDemandeDAO         demandeDAO;
    private final NotificationService notificationService;

    private DemandeService() {
        this.demandeDAO          = new DemandeDAOImpl();
        this.notificationService = NotificationService.getInstance();
    }

    public static DemandeService getInstance() {
        if (instance == null) instance = new DemandeService();
        return instance;
    }

    public List<DemandeAdministrative> getDemandesEtudiant(Etudiant etudiant) {
        return demandeDAO.findByEtudiant(etudiant.getId());
    }

    public DemandeAdministrative soumettreDemandeAdministrative(Etudiant etudiant,
                                                                 TypeDemande type,
                                                                 String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("La description ne peut pas être vide.");
        }
        DemandeAdministrative demande = new DemandeAdministrative(
                0, etudiant, type, description.trim());
        demandeDAO.save(demande);

        notificationService.creerNotification(etudiant,
                "Votre demande de \"" + type.getLibelle() + "\" a été soumise avec succès.", "demande");
        return demande;
    }

    @Override
    public DemandeAdministrative creer(DemandeAdministrative d) {
        return demandeDAO.save(d);
    }

    @Override
    public Optional<DemandeAdministrative> trouverParId(int id) {
        return demandeDAO.findById(id);
    }

    @Override
    public List<DemandeAdministrative> trouverTous() {
        return demandeDAO.findAll();
    }

    @Override
    public void supprimer(int id) {
        // suppression non exposée
    }
}
