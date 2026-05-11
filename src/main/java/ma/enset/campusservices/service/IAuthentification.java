package ma.enset.campusservices.service;

import ma.enset.campusservices.model.Etudiant;

/**
 * Interface définissant le contrat d'authentification.
 */
public interface IAuthentification {

    /**
     * Authentifie un étudiant avec son email et mot de passe.
     * @return l'étudiant si authentifié, null sinon
     */
    Etudiant login(String email, String motDePasse);

    /**
     * Déconnecte l'étudiant courant.
     */
    void logout();

    /**
     * @return true si un étudiant est actuellement connecté
     */
    boolean isAuthentifie();

    /**
     * @return l'étudiant actuellement connecté, ou null
     */
    Etudiant getEtudiantCourant();
}
