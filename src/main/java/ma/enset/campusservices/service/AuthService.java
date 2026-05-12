package ma.enset.campusservices.service;

import ma.enset.campusservices.dao.IAdminDAO;
import ma.enset.campusservices.dao.IEtudiantDAO;
import ma.enset.campusservices.dao.impl.AdminDAOImpl;
import ma.enset.campusservices.dao.impl.EtudiantDAOImpl;
import ma.enset.campusservices.model.Admin;
import ma.enset.campusservices.model.Etudiant;
import ma.enset.campusservices.security.PasswordHash;

/**
 * Service d'authentification — étudiants ET personnel admin.
 * Utilise EtudiantDAO et AdminDAO (PostgreSQL).
 */
public class AuthService implements IAuthentification {

    private static AuthService instance;

    private Etudiant etudiantCourant;
    private Admin    adminCourant;

    private final IEtudiantDAO etudiantDAO;
    private final IAdminDAO    adminDAO;

    private AuthService() {
        this.etudiantDAO = new EtudiantDAOImpl();
        this.adminDAO    = new AdminDAOImpl();
    }

    public static AuthService getInstance() {
        if (instance == null) instance = new AuthService();
        return instance;
    }

    @Override
    public Etudiant login(String email, String motDePasse) {
        if (email == null || motDePasse == null) return null;
        return etudiantDAO.findByEmail(email.trim())
                .filter(e -> e.isActif() && verifierMotDePasse(e, motDePasse))
                .map(e -> { this.etudiantCourant = e; return e; })
                .orElse(null);
    }

    public Admin loginAdmin(String email, String motDePasse) {
        if (email == null || motDePasse == null) return null;
        return adminDAO.findByEmail(email.trim())
                .filter(a -> verifierMotDePasse(a, motDePasse))
                .map(a -> { this.adminCourant = a; return a; })
                .orElse(null);
    }

    private boolean verifierMotDePasse(Etudiant etudiant, String motDePasse) {
        boolean matches = PasswordHash.matches(motDePasse, etudiant.getMotDePasse());
        if (matches && PasswordHash.needsHash(etudiant.getMotDePasse())) {
            etudiantDAO.updateMotDePasse(etudiant.getId(), PasswordHash.hash(motDePasse));
        }
        return matches;
    }

    private boolean verifierMotDePasse(Admin admin, String motDePasse) {
        boolean matches = PasswordHash.matches(motDePasse, admin.getMotDePasse());
        if (matches && PasswordHash.needsHash(admin.getMotDePasse())) {
            adminDAO.updateMotDePasse(admin.getId(), PasswordHash.hash(motDePasse));
        }
        return matches;
    }

    @Override
    public void logout() {
        this.etudiantCourant = null;
        this.adminCourant    = null;
    }

    @Override
    public boolean isAuthentifie() {
        return etudiantCourant != null || adminCourant != null;
    }

    @Override
    public Etudiant getEtudiantCourant() { return etudiantCourant; }

    public Admin    getAdminCourant()    { return adminCourant; }

    public boolean  isAdmin()            { return adminCourant != null; }
}
