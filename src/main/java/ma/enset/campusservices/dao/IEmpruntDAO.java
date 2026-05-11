package ma.enset.campusservices.dao;

import ma.enset.campusservices.model.Emprunt;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IEmpruntDAO {
    List<Emprunt>     findAll();
    List<Emprunt>     findByEtudiant(int etudiantId);
    List<Emprunt>     findActifsByEtudiant(int etudiantId);
    Optional<Emprunt> findById(int id);
    Emprunt           save(Emprunt emprunt);
    void              retourner(int id, LocalDate dateRetour);
}
