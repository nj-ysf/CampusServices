package ma.enset.campusservices.repository;

import ma.enset.campusservices.model.*;
import ma.enset.campusservices.model.enums.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton — base de données in-memory.
 */
public class DataStore {

    private static DataStore instance;
    private static final String ADMIN_PASSWORD_HASH =
            "$2a$12$qv/kQ/FKkS2j23zyuS1cF.BLEA8BYMYXOzSboCJcXf8PCncaSfIUe";
    private static final String BIBLIO_PASSWORD_HASH =
            "$2a$12$8QgzQL1YcGxYplwYR194weUXzbAdR8T9oJFhES6Bn3OtJsUIeEWWS";
    private static final String SALLES_PASSWORD_HASH =
            "$2a$12$vr2xu/yxblUhaaAD2p8swewkkdLt0cIhSCLtiyXs52n1RF27ClXVu";
    private static final String ETUDIANT_PASSWORD_HASH =
            "$2a$12$FcnMlSmTwArHJeArqfFQKO9gda8fOdS8wahRMzRTtMBxIL2y5RCDe";

    private final List<Etudiant>              etudiants     = new ArrayList<>();
    private final List<Admin>                 admins        = new ArrayList<>();
    private final List<Catalogue>             catalogues    = new ArrayList<>();
    private final List<Ressource>             ressources    = new ArrayList<>();
    private final List<Emprunt>               emprunts      = new ArrayList<>();
    private final List<Salle>                 salles        = new ArrayList<>();
    private final List<ReservationSalle>      reservations  = new ArrayList<>();
    private final List<DemandeAdministrative> demandes      = new ArrayList<>();
    private final List<Notification>          notifications = new ArrayList<>();

    private int nextEtudiantId    = 1;
    private int nextRessourceId   = 1;
    private int nextEmpruntId     = 1;
    private int nextReservationId = 1;
    private int nextDemandeId     = 1;
    private int nextNotifId       = 1;

    private DataStore() { initialiserDonnees(); }

    public static DataStore getInstance() {
        if (instance == null) instance = new DataStore();
        return instance;
    }

    private void initialiserDonnees() {

        // ── Admins ─────────────────────────────────────────────────────────
        admins.add(new Admin(1, "Administrateur", "Système",
                "admin@enset.ac.ma",      ADMIN_PASSWORD_HASH,  Role.ADMIN));
        admins.add(new Admin(2, "Moudi", "Hassan",
                "biblio@enset.ac.ma",     BIBLIO_PASSWORD_HASH, Role.BIBLIOTHECAIRE));
        admins.add(new Admin(3, "Idrissi", "Mohamed",
                "salles@enset.ac.ma",     SALLES_PASSWORD_HASH, Role.RESPONSABLE_SALLES));

        // ── Étudiants ──────────────────────────────────────────────────────
        Etudiant e1 = new Etudiant(nextEtudiantId++, "Naji",        "Youssef",
                "youssef.naji@enset.ac.ma",        ETUDIANT_PASSWORD_HASH, "GLSID-BDCC",    StatutEtudiant.ACTIF);
        Etudiant e2 = new Etudiant(nextEtudiantId++, "Bousserhane", "Brahim",
                "brahim.bousserhane@enset.ac.ma",  ETUDIANT_PASSWORD_HASH, "GLSID-BDCC",    StatutEtudiant.ACTIF);
        Etudiant e3 = new Etudiant(nextEtudiantId++, "Ez-Zahery",   "Ahmed Amine",
                "ahmed.ezzahery@enset.ac.ma",      ETUDIANT_PASSWORD_HASH, "GLSID-BDCC",    StatutEtudiant.ACTIF);
        Etudiant e4 = new Etudiant(nextEtudiantId++, "Khoury",      "Sara",
                "sara.khoury@enset.ac.ma",         ETUDIANT_PASSWORD_HASH, "Génie Électrique", StatutEtudiant.ACTIF);
        Etudiant e5 = new Etudiant(nextEtudiantId++, "Benali",      "Karim",
                "karim.benali@enset.ac.ma",        ETUDIANT_PASSWORD_HASH, "Génie Électrique", StatutEtudiant.INACTIF);
        etudiants.addAll(List.of(e1, e2, e3, e4, e5));

        // ── Catalogues ─────────────────────────────────────────────────────
        Catalogue c1 = new Catalogue(1, "Sciences et Technologies");
        Catalogue c2 = new Catalogue(2, "Informatique et Réseaux");
        Catalogue c3 = new Catalogue(3, "Mathématiques et Physique");
        catalogues.addAll(List.of(c1, c2, c3));

        // ── Ressources ─────────────────────────────────────────────────────
        Livre l1 = new Livre(nextRessourceId++,
                "Introduction aux systèmes embarqués", "Ahmed Benali",
                true,  c1, "978-2-10-070221-3", 340, "Dunod");
        Livre l2 = new Livre(nextRessourceId++,
                "Algorithmique et structures de données", "Thomas Cormen",
                true,  c2, "978-2-10-079121-6", 500, "MIT Press");
        Livre l3 = new Livre(nextRessourceId++,
                "Réseaux informatiques", "Andrew Tanenbaum",
                false, c2, "978-2-10-058741-9", 960, "Pearson");
        Livre l4 = new Livre(nextRessourceId++,
                "Electronique numérique", "Hamid Mounir",
                true,  c1, "978-2-10-071190-1", 420, "Dunod");
        Livre l5 = new Livre(nextRessourceId++,
                "Analyse mathématique", "Jean Dieudonné",
                true,  c3, "978-2-10-073210-4", 380, "Eyrolles");
        Memoire m1 = new Memoire(nextRessourceId++,
                "Développement d'une application mobile pour la gestion des stages",
                "Amine Ez-Zahery", true, c2,
                "Génie Logiciel", 2024, "Pr. Hassan Moudi");
        Memoire m2 = new Memoire(nextRessourceId++,
                "Implémentation d'un système de détection d'intrusions réseau",
                "Brahim Bousserhane", true, c2,
                "Sécurité Informatique", 2024, "Pr. Fatima Zahr");
        Memoire m3 = new Memoire(nextRessourceId++,
                "Optimisation énergétique des microcontrôleurs ARM",
                "Sara Khoury", true, c1,
                "Systèmes Embarqués", 2023, "Pr. Mohamed Idrissi");

        ressources.addAll(List.of(l1, l2, l3, l4, l5, m1, m2, m3));
        c1.ajouterRessource(l1); c1.ajouterRessource(l4); c1.ajouterRessource(m3);
        c2.ajouterRessource(l2); c2.ajouterRessource(l3);
        c2.ajouterRessource(m1); c2.ajouterRessource(m2);
        c3.ajouterRessource(l5);

        // ── Salles ─────────────────────────────────────────────────────────
        salles.addAll(List.of(
                new Salle(1, "Salle A101",              30),
                new Salle(2, "Salle A102",              25),
                new Salle(3, "Salle B201",              40),
                new Salle(4, "Amphithéâtre 1",         120),
                new Salle(5, "Salle Informatique C301", 35),
                new Salle(6, "Salle de Conférence",     50)
        ));

        // ── Emprunt démo ───────────────────────────────────────────────────
        l3.setDisponible(false);
        emprunts.add(new Emprunt(nextEmpruntId++, e1, l3, LocalDate.now().minusDays(5)));

        // ── Réservations démo ──────────────────────────────────────────────
        ReservationSalle r1 = new ReservationSalle(nextReservationId++, e1, salles.get(0),
                LocalDateTime.now().plusDays(1).withHour(9).withMinute(0),
                LocalDateTime.now().plusDays(1).withHour(11).withMinute(0));
        ReservationSalle r2 = new ReservationSalle(nextReservationId++, e2, salles.get(2),
                LocalDateTime.now().plusDays(2).withHour(14).withMinute(0),
                LocalDateTime.now().plusDays(2).withHour(16).withMinute(0));
        reservations.addAll(List.of(r1, r2));

        // ── Demandes démo ──────────────────────────────────────────────────
        DemandeAdministrative d1 = new DemandeAdministrative(
                nextDemandeId++, e1, TypeDemande.CERTIFICAT_SCOLARITE,
                "Besoin d'un certificat pour la banque.");
        DemandeAdministrative d2 = new DemandeAdministrative(
                nextDemandeId++, e3, TypeDemande.RELEVE_NOTES,
                "Relevé de notes pour candidature master.");
        demandes.addAll(List.of(d1, d2));

        // ── Notifications démo ─────────────────────────────────────────────
        notifications.add(new Notification(nextNotifId++, e1,
                "Bienvenue sur CampusServices !", "info"));
        notifications.add(new Notification(nextNotifId++, e1,
                "Emprunt de \"" + l3.getTitre() + "\" enregistré.", "emprunt"));
        notifications.add(new Notification(nextNotifId++, e2,
                "Bienvenue sur CampusServices !", "info"));
        notifications.add(new Notification(nextNotifId++, e3,
                "Bienvenue sur CampusServices !", "info"));
        notifications.add(new Notification(nextNotifId++, e4,
                "Bienvenue sur CampusServices !", "info"));
    }

    // ── Accesseurs ────────────────────────────────────────────────────────
    public List<Etudiant>              getEtudiants()    { return etudiants;    }
    public List<Admin>                 getAdmins()       { return admins;       }
    public List<Catalogue>             getCatalogues()   { return catalogues;   }
    public List<Ressource>             getRessources()   { return ressources;   }
    public List<Emprunt>               getEmprunts()     { return emprunts;     }
    public List<Salle>                 getSalles()       { return salles;       }
    public List<ReservationSalle>      getReservations() { return reservations; }
    public List<DemandeAdministrative> getDemandes()     { return demandes;     }
    public List<Notification>          getNotifications(){ return notifications;}

    // ── Générateurs d'ID ─────────────────────────────────────────────────
    public int nextEmpruntId()     { return nextEmpruntId++;     }
    public int nextReservationId() { return nextReservationId++; }
    public int nextDemandeId()     { return nextDemandeId++;     }
    public int nextNotifId()       { return nextNotifId++;       }
    public int nextRessourceId()   { return nextRessourceId++;   }
}
