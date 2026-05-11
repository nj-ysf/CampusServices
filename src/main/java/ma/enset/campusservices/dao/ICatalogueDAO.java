package ma.enset.campusservices.dao;

import ma.enset.campusservices.model.Catalogue;

import java.util.List;
import java.util.Optional;

public interface ICatalogueDAO {
    List<Catalogue>     findAll();
    Optional<Catalogue> findById(int id);
    Catalogue           save(Catalogue catalogue);
}
