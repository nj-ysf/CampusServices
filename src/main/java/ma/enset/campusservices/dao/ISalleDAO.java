package ma.enset.campusservices.dao;

import ma.enset.campusservices.model.Salle;

import java.util.List;
import java.util.Optional;

public interface ISalleDAO {
    List<Salle>     findAll();
    Optional<Salle> findById(int id);
}
