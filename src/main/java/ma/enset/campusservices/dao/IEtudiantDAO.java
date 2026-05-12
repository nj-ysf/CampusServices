package ma.enset.campusservices.dao;

import ma.enset.campusservices.model.Etudiant;
import ma.enset.campusservices.model.enums.StatutEtudiant;

import java.util.List;
import java.util.Optional;

public interface IEtudiantDAO {
    Optional<Etudiant> findByEmail(String email);
    Optional<Etudiant> findById(int id);
    List<Etudiant>     findAll();
    Etudiant           save(Etudiant etudiant);
    void               updateMotDePasse(int id, String motDePasseHash);
    void               updateStatut(int id, StatutEtudiant statut);
    void               delete(int id);
}
