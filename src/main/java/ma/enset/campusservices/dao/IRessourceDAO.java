package ma.enset.campusservices.dao;

import ma.enset.campusservices.model.Catalogue;
import ma.enset.campusservices.model.Ressource;

import java.util.List;
import java.util.Optional;

public interface IRessourceDAO {
    List<Ressource>     findAll();
    List<Ressource>     findByCatalogue(int catalogueId);
    Optional<Ressource> findById(int id);
    Ressource           save(Ressource ressource);
    void                updateDisponibilite(int id, boolean disponible);
    void                delete(int id);
}
