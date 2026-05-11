package ma.enset.campusservices.dao;

import ma.enset.campusservices.model.DemandeAdministrative;
import ma.enset.campusservices.model.enums.StatutDemande;

import java.util.List;
import java.util.Optional;

public interface IDemandeDAO {
    List<DemandeAdministrative>     findAll();
    List<DemandeAdministrative>     findByEtudiant(int etudiantId);
    Optional<DemandeAdministrative> findById(int id);
    DemandeAdministrative           save(DemandeAdministrative demande);
    void                            updateStatut(int id, StatutDemande statut, String commentaire);
}
