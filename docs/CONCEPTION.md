# 📐 CampusServices — Diagrammes de conception

> Ensemble complet des diagrammes UML & conception du projet **CampusServices** (ENSET Mohammedia).
> Tous les diagrammes sont au format **Mermaid** (rendu natif GitHub / VS Code / Notion).

## 📋 Sommaire

1. [Diagramme de cas d'utilisation](#1-diagramme-de-cas-dutilisation)
2. [Diagramme de classes — Domaine métier](#2-diagramme-de-classes--domaine-métier)
3. [Diagramme de classes — Architecture en couches](#3-diagramme-de-classes--architecture-en-couches)
4. [Diagramme de packages](#4-diagramme-de-packages)
5. [Diagramme de séquence — Authentification](#5-diagramme-de-séquence--authentification)
6. [Diagramme de séquence — Emprunt d'une ressource](#6-diagramme-de-séquence--emprunt-dune-ressource)
7. [Diagramme de séquence — Retour d'une ressource](#7-diagramme-de-séquence--retour-dune-ressource)
8. [Diagramme de séquence — Réservation d'une salle](#8-diagramme-de-séquence--réservation-dune-salle)
9. [Diagramme de séquence — Soumission d'une demande administrative](#9-diagramme-de-séquence--soumission-dune-demande-administrative)
10. [Diagramme de séquence — Traitement de la demande (admin)](#10-diagramme-de-séquence--traitement-de-la-demande-admin)
11. [Diagramme d'états — Réservation de salle](#11-diagramme-détats--réservation-de-salle)
12. [Diagramme d'états — Demande administrative](#12-diagramme-détats--demande-administrative)
13. [Diagramme d'états — Emprunt](#13-diagramme-détats--emprunt)
14. [Diagramme d'activité — Workflow d'une demande](#14-diagramme-dactivité--workflow-dune-demande)
15. [Diagramme entité-association (MCD)](#15-diagramme-entité-association-mcd)
16. [Diagramme de déploiement](#16-diagramme-de-déploiement)

---

## 1. Diagramme de cas d'utilisation

```mermaid
flowchart LR
    %% Acteurs
    E(("👤<br/>Étudiant"))
    A(("🧑‍💼<br/>Administrateur"))
    B(("📚<br/>Bibliothécaire"))
    R(("🏛<br/>Resp. Salles"))

    subgraph SYS["🎓 Système CampusServices"]
        direction TB

        UC1(["Se connecter"])
        UC2(["Consulter le catalogue"])
        UC3(["Emprunter une ressource"])
        UC4(["Retourner une ressource"])
        UC5(["Réserver une salle"])
        UC6(["Annuler une réservation"])
        UC7(["Soumettre une demande"])
        UC8(["Consulter ses notifications"])

        UC9(["Gérer les étudiants"])
        UC10(["Activer / Désactiver un étudiant"])
        UC11(["Gérer le catalogue"])
        UC12(["Ajouter livre / mémoire"])
        UC13(["Valider une réservation"])
        UC14(["Traiter une demande"])
        UC15(["Envoyer une notification"])
    end

    %% Liaisons étudiant
    E --- UC1
    E --- UC2
    E --- UC3
    E --- UC4
    E --- UC5
    E --- UC6
    E --- UC7
    E --- UC8

    %% Liaisons admin général
    A --- UC1
    A --- UC9
    A --- UC15

    %% Bibliothécaire
    B --- UC1
    B --- UC11

    %% Responsable salles
    R --- UC1
    R --- UC13

    %% Admin traite aussi les demandes
    A --- UC14

    %% Includes / Extends
    UC9 -.->|«include»| UC10
    UC11 -.->|«include»| UC12

    classDef actor fill:#0d6efd,stroke:#0a58ca,color:#fff,stroke-width:2px
    classDef uc fill:#e7f1ff,stroke:#0d6efd,color:#084298
    class E,A,B,R actor
    class UC1,UC2,UC3,UC4,UC5,UC6,UC7,UC8,UC9,UC10,UC11,UC12,UC13,UC14,UC15 uc
```

**Acteurs :**
- 👤 **Étudiant** — utilise les services courants (biblio, salles, demandes).
- 🧑‍💼 **Administrateur** — supervise et gère les étudiants & demandes.
- 📚 **Bibliothécaire** — gère le catalogue (livres, mémoires).
- 🏛 **Responsable salles** — valide ou refuse les réservations.

---

## 2. Diagramme de classes — Domaine métier

```mermaid
classDiagram
    direction LR

    class Etudiant {
        -int id
        -String nom
        -String prenom
        -String email
        -String motDePasse
        -String filiere
        -StatutEtudiant statut
        +getNomComplet() String
        +isActif() boolean
    }

    class Admin {
        -int id
        -String nom
        -String prenom
        -String email
        -String motDePasse
        -Role role
        +getNomComplet() String
    }

    class Ressource {
        <<abstract>>
        -int id
        -String titre
        -String auteur
        -boolean disponible
        -Catalogue catalogue
        +getType()* String
        +getDescription()* String
    }

    class Livre {
        -String isbn
        -int nbPages
        -String editeur
        +getType() String
    }

    class Memoire {
        -String specialite
        -int annee
        -String encadrant
        +getType() String
    }

    class Catalogue {
        -int id
        -String nom
        -String description
    }

    class Salle {
        -int id
        -String nom
        -int capacite
        -String type
    }

    class Emprunt {
        -int id
        -Etudiant etudiant
        -Ressource ressource
        -LocalDate dateEmprunt
        -LocalDate dateRetour
        +isEnCours() boolean
    }

    class ReservationSalle {
        -int id
        -Etudiant etudiant
        -Salle salle
        -LocalDateTime dateDebut
        -LocalDateTime dateFin
        -StatutReservation statut
        +isAnnulable() boolean
    }

    class DemandeAdministrative {
        -int id
        -Etudiant etudiant
        -TypeDemande type
        -String description
        -StatutDemande statut
        -LocalDateTime dateCreation
    }

    class Notification {
        -int id
        -Etudiant etudiant
        -String message
        -String type
        -boolean lu
        -LocalDateTime dateCreation
        +marquerCommeLu() void
    }

    class Role {
        <<enumeration>>
        ADMINISTRATEUR
        BIBLIOTHECAIRE
        RESPONSABLE_SALLES
    }

    class StatutEtudiant {
        <<enumeration>>
        ACTIF
        INACTIF
    }

    class StatutReservation {
        <<enumeration>>
        EN_ATTENTE
        CONFIRMEE
        ANNULEE
    }

    class StatutDemande {
        <<enumeration>>
        SOUMISE
        EN_TRAITEMENT
        VALIDEE
        REFUSEE
    }

    class TypeDemande {
        <<enumeration>>
        CERTIFICAT_SCOLARITE
        ATTESTATION_REUSSITE
        RELEVE_NOTES
        AUTRE
    }

    %% Héritage
    Ressource <|-- Livre
    Ressource <|-- Memoire

    %% Associations
    Ressource "*" --> "1" Catalogue : appartient à
    Emprunt "*" --> "1" Etudiant : effectué par
    Emprunt "*" --> "1" Ressource : concerne
    ReservationSalle "*" --> "1" Etudiant : demandée par
    ReservationSalle "*" --> "1" Salle : porte sur
    DemandeAdministrative "*" --> "1" Etudiant : soumise par
    Notification "*" --> "1" Etudiant : destinée à

    Etudiant ..> StatutEtudiant
    Admin ..> Role
    ReservationSalle ..> StatutReservation
    DemandeAdministrative ..> StatutDemande
    DemandeAdministrative ..> TypeDemande
```

---

## 3. Diagramme de classes — Architecture en couches

```mermaid
classDiagram
    direction TB

    class MainApp {
        +start(Stage) void
        +afficherLogin()$ void
        +afficherPrincipal()$ void
        +afficherAdmin()$ void
    }

    class LoginController {
        -AuthService authService
        +handleLogin() void
    }

    class MainController
    class AdminMainController
    class BibliothequeController
    class ReservationsController
    class DemandesController
    class AdminEtudiantsController
    class AdminCatalogueController

    class IAuthentification {
        <<interface>>
        +login(email, mdp) Etudiant
        +logout() void
        +isAuthentifie() boolean
    }

    class IGestionable~T~ {
        <<interface>>
        +creer(T) T
        +trouverParId(int) Optional~T~
        +trouverTous() List~T~
        +supprimer(int) void
    }

    class AuthService {
        -Etudiant etudiantCourant
        -Admin adminCourant
        +getInstance()$ AuthService
        +login() Etudiant
        +loginAdmin() Admin
        +isAdmin() boolean
    }

    class BibliothequeService {
        +emprunterRessource() Emprunt
        +retournerRessource() void
        +rechercherRessources() List
    }

    class ReservationService {
        +creerReservation() ReservationSalle
        +annulerReservation() void
    }

    class DemandeService {
        +soumettreDemandeAdministrative() DemandeAdministrative
    }

    class NotificationService {
        +creerNotification() Notification
        +marquerCommeLue() void
        +compterNonLues() long
    }

    class IEtudiantDAO {
        <<interface>>
    }
    class IAdminDAO {
        <<interface>>
    }
    class IRessourceDAO {
        <<interface>>
    }
    class IEmpruntDAO {
        <<interface>>
    }
    class IReservationDAO {
        <<interface>>
    }
    class IDemandeDAO {
        <<interface>>
    }
    class INotificationDAO {
        <<interface>>
    }

    class EtudiantDAOImpl
    class AdminDAOImpl
    class RessourceDAOImpl
    class EmpruntDAOImpl
    class ReservationDAOImpl
    class DemandeDAOImpl
    class NotificationDAOImpl

    class DatabaseConfig {
        +getConnection()$ Connection
    }

    class PasswordHash {
        +hash(String)$ String
        +matches(String, String)$ boolean
    }

    %% Couche UI → Contrôleurs
    MainApp --> LoginController
    MainApp --> MainController
    MainApp --> AdminMainController
    MainController --> BibliothequeController
    MainController --> ReservationsController
    MainController --> DemandesController
    AdminMainController --> AdminEtudiantsController
    AdminMainController --> AdminCatalogueController

    %% Contrôleurs → Services
    LoginController --> AuthService
    BibliothequeController --> BibliothequeService
    ReservationsController --> ReservationService
    DemandesController --> DemandeService

    %% Services implémentent interfaces
    AuthService ..|> IAuthentification
    BibliothequeService ..|> IGestionable
    ReservationService ..|> IGestionable
    DemandeService ..|> IGestionable

    %% Services → DAOs
    AuthService --> IEtudiantDAO
    AuthService --> IAdminDAO
    AuthService --> PasswordHash
    BibliothequeService --> IRessourceDAO
    BibliothequeService --> IEmpruntDAO
    BibliothequeService --> NotificationService
    ReservationService --> IReservationDAO
    ReservationService --> NotificationService
    DemandeService --> IDemandeDAO
    DemandeService --> NotificationService
    NotificationService --> INotificationDAO

    %% DAOs implémentation
    IEtudiantDAO <|.. EtudiantDAOImpl
    IAdminDAO <|.. AdminDAOImpl
    IRessourceDAO <|.. RessourceDAOImpl
    IEmpruntDAO <|.. EmpruntDAOImpl
    IReservationDAO <|.. ReservationDAOImpl
    IDemandeDAO <|.. DemandeDAOImpl
    INotificationDAO <|.. NotificationDAOImpl

    %% Tous les DAOs utilisent DatabaseConfig
    EtudiantDAOImpl --> DatabaseConfig
    AdminDAOImpl --> DatabaseConfig
    RessourceDAOImpl --> DatabaseConfig
    EmpruntDAOImpl --> DatabaseConfig
    ReservationDAOImpl --> DatabaseConfig
    DemandeDAOImpl --> DatabaseConfig
    NotificationDAOImpl --> DatabaseConfig
```

---

## 4. Diagramme de packages

```mermaid
flowchart TB
    subgraph UI["🎨 Couche Présentation"]
        FXML["📄 *.fxml (15 vues)"]
        CSS["🎨 css/style.css"]
    end

    subgraph CTRL["🎮 controller"]
        C1["LoginController"]
        C2["Main + AdminMain Controllers"]
        C3["Vues spécifiques (Biblio, Reserv,<br/>Demandes, Notif, Etudiants, Catalogue…)"]
    end

    subgraph SVC["⚙️ service"]
        S1["AuthService"]
        S2["BibliothequeService"]
        S3["ReservationService"]
        S4["DemandeService"]
        S5["NotificationService"]
        SI1["«interface»<br/>IAuthentification"]
        SI2["«interface»<br/>IGestionable<T>"]
    end

    subgraph DAO["💾 dao + dao.impl"]
        D1["IEtudiantDAO / Impl"]
        D2["IAdminDAO / Impl"]
        D3["IRessourceDAO / Impl"]
        D4["ICatalogueDAO / Impl"]
        D5["IEmpruntDAO / Impl"]
        D6["IReservationDAO / Impl"]
        D7["ISalleDAO / Impl"]
        D8["IDemandeDAO / Impl"]
        D9["INotificationDAO / Impl"]
    end

    subgraph MOD["📦 model (POJO) + enums"]
        M1["Etudiant, Admin"]
        M2["Ressource (abs),<br/>Livre, Memoire, Catalogue"]
        M3["Salle, Emprunt,<br/>ReservationSalle"]
        M4["DemandeAdministrative,<br/>Notification"]
        M5["enums.Role, Statut*,<br/>TypeDemande"]
    end

    subgraph INFRA["🔧 Infrastructure"]
        DB["database.DatabaseConfig"]
        SEC["security.PasswordHash<br/>(BCrypt)"]
        REP["repository.DataStore"]
    end

    PG[("🐘 PostgreSQL")]

    UI --> CTRL
    CTRL --> SVC
    SVC --> DAO
    SVC --> MOD
    DAO --> MOD
    DAO --> INFRA
    SVC --> SEC
    SVC --> REP
    INFRA --> PG

    classDef ui fill:#fef3c7,stroke:#d97706
    classDef ctrl fill:#dbeafe,stroke:#1d4ed8
    classDef svc fill:#dcfce7,stroke:#15803d
    classDef dao fill:#fce7f3,stroke:#be185d
    classDef mod fill:#e0e7ff,stroke:#4338ca
    classDef infra fill:#f3f4f6,stroke:#374151
    class UI,FXML,CSS ui
    class CTRL,C1,C2,C3 ctrl
    class SVC,S1,S2,S3,S4,S5,SI1,SI2 svc
    class DAO,D1,D2,D3,D4,D5,D6,D7,D8,D9 dao
    class MOD,M1,M2,M3,M4,M5 mod
    class INFRA,DB,SEC,REP infra
```

---

## 5. Diagramme de séquence — Authentification

```mermaid
sequenceDiagram
    autonumber
    actor U as Utilisateur
    participant V as login.fxml
    participant LC as LoginController
    participant AS as AuthService
    participant AD as AdminDAOImpl
    participant ED as EtudiantDAOImpl
    participant PH as PasswordHash
    participant DB as PostgreSQL
    participant MA as MainApp

    U->>V: saisit email + mot de passe
    U->>V: clique « Se connecter »
    V->>LC: handleLogin()
    LC->>AS: loginAdmin(email, mdp)
    AS->>AD: findByEmail(email)
    AD->>DB: SELECT * FROM admins WHERE email=?
    DB-->>AD: ResultSet
    AD-->>AS: Optional<Admin>

    alt Admin trouvé
        AS->>PH: matches(mdp, hash)
        PH-->>AS: true
        AS-->>LC: Admin
        LC->>MA: afficherAdmin()
        MA-->>U: 🎛 Tableau de bord Admin
    else Pas un admin
        AS-->>LC: null
        LC->>AS: login(email, mdp)
        AS->>ED: findByEmail(email)
        ED->>DB: SELECT * FROM etudiants WHERE email=?
        DB-->>ED: ResultSet
        ED-->>AS: Optional<Etudiant>
        AS->>PH: matches(mdp, hash)
        PH-->>AS: true / false

        alt Étudiant actif & mdp correct
            AS-->>LC: Etudiant
            LC->>MA: afficherPrincipal()
            MA-->>U: 🏠 Tableau de bord Étudiant
        else Échec
            AS-->>LC: null
            LC-->>V: showError(« Email ou mot de passe incorrect »)
            V-->>U: ⚠️ Message d'erreur
        end
    end
```

---

## 6. Diagramme de séquence — Emprunt d'une ressource

```mermaid
sequenceDiagram
    autonumber
    actor E as Étudiant
    participant V as bibliotheque.fxml
    participant BC as BibliothequeController
    participant BS as BibliothequeService
    participant RD as RessourceDAOImpl
    participant ED as EmpruntDAOImpl
    participant NS as NotificationService
    participant ND as NotificationDAOImpl
    participant DB as PostgreSQL

    E->>V: sélectionne une ressource
    E->>V: clique « Emprunter »
    V->>BC: handleEmprunter()
    BC->>BS: emprunterRessource(etudiant, ressource)

    alt Ressource non disponible
        BS-->>BC: IllegalStateException
        BC-->>V: showError(« Indisponible »)
        V-->>E: ⚠️ Alerte
    else Ressource disponible
        BS->>ED: save(emprunt)
        ED->>DB: INSERT INTO emprunts ...
        DB-->>ED: id généré
        BS->>RD: updateDisponibilite(ressourceId, false)
        RD->>DB: UPDATE ressources SET disponible=false
        BS->>NS: creerNotification(etudiant, "Emprunt enregistré")
        NS->>ND: save(notification)
        ND->>DB: INSERT INTO notifications ...
        BS-->>BC: Emprunt
        BC-->>V: rafraîchir la liste
        V-->>E: ✅ Confirmation
    end
```

---

## 7. Diagramme de séquence — Retour d'une ressource

```mermaid
sequenceDiagram
    autonumber
    actor E as Étudiant
    participant V as emprunts.fxml
    participant EC as EmpruntsController
    participant BS as BibliothequeService
    participant ED as EmpruntDAOImpl
    participant RD as RessourceDAOImpl
    participant NS as NotificationService
    participant DB as PostgreSQL

    E->>V: sélectionne emprunt en cours
    E->>V: clique « Retourner »
    V->>EC: handleRetour()
    EC->>BS: retournerRessource(emprunt)
    BS->>ED: retourner(id, today)
    ED->>DB: UPDATE emprunts SET date_retour=?
    BS->>RD: updateDisponibilite(ressourceId, true)
    RD->>DB: UPDATE ressources SET disponible=true
    BS->>NS: creerNotification(« Retour enregistré »)
    BS-->>EC: ok
    EC-->>V: rafraîchir
    V-->>E: ✅ Confirmation
```

---

## 8. Diagramme de séquence — Réservation d'une salle

```mermaid
sequenceDiagram
    autonumber
    actor E as Étudiant
    participant V as reservations.fxml
    participant RC as ReservationsController
    participant RS as ReservationService
    participant RD as ReservationDAOImpl
    participant NS as NotificationService
    participant DB as PostgreSQL

    E->>V: choisit salle + créneau (début, fin)
    E->>V: clique « Réserver »
    V->>RC: handleReservation()
    RC->>RS: creerReservation(etudiant, salle, debut, fin)
    RS->>RD: findBySalle(salleId)
    RD->>DB: SELECT * FROM reservations_salles WHERE salle_id=?
    DB-->>RD: ResultSet
    RD-->>RS: List<ReservationSalle>

    Note over RS: Vérifie chevauchement de créneaux<br/>(statut ≠ ANNULEE)

    alt Conflit détecté
        RS-->>RC: IllegalStateException
        RC-->>V: showError(« Salle déjà réservée »)
        V-->>E: ⚠️ Alerte
    else Pas de conflit
        RS->>RD: save(new ReservationSalle(EN_ATTENTE))
        RD->>DB: INSERT INTO reservations_salles ...
        RS->>NS: creerNotification(« Demande soumise »)
        RS-->>RC: ReservationSalle
        RC-->>V: ajouter à la liste
        V-->>E: ✅ « En attente de validation »
    end
```

---

## 9. Diagramme de séquence — Soumission d'une demande administrative

```mermaid
sequenceDiagram
    autonumber
    actor E as Étudiant
    participant V as demandes.fxml
    participant DC as DemandesController
    participant DS as DemandeService
    participant DD as DemandeDAOImpl
    participant NS as NotificationService
    participant DB as PostgreSQL

    E->>V: choisit type de demande<br/>(certificat / attestation / relevé / autre)
    E->>V: saisit description / motif
    E->>V: clique « Soumettre »
    V->>DC: handleSoumettre()
    DC->>DS: soumettreDemandeAdministrative(etudiant, type, description)

    alt Description vide
        DS-->>DC: IllegalArgumentException
        DC-->>V: showError(« Description requise »)
    else Données valides
        DS->>DD: save(new DemandeAdministrative(SOUMISE))
        DD->>DB: INSERT INTO demandes_administratives ...
        DS->>NS: creerNotification(« Demande soumise »)
        DS-->>DC: DemandeAdministrative
        DC-->>V: ajouter à la liste
        V-->>E: ✅ Confirmation
    end
```

---

## 10. Diagramme de séquence — Traitement de la demande (admin)

```mermaid
sequenceDiagram
    autonumber
    actor A as Administrateur
    participant V as admin-demandes.fxml
    participant AC as AdminDemandesController
    participant DS as DemandeService
    participant DD as DemandeDAOImpl
    participant NS as NotificationService
    participant DB as PostgreSQL

    A->>V: filtre les demandes (SOUMISE)
    V->>AC: chargerDemandes()
    AC->>DS: trouverTous()
    DS->>DD: findAll()
    DD->>DB: SELECT * FROM demandes_administratives
    DD-->>AC: List<DemandeAdministrative>
    V-->>A: tableau affiché

    A->>V: sélectionne une demande + action
    alt Mettre en traitement
        V->>AC: handleMettreEnTraitement()
        AC->>DD: updateStatut(id, EN_TRAITEMENT)
        DD->>DB: UPDATE ...
        AC->>NS: creerNotification(étudiant, « En traitement »)
    else Valider
        V->>AC: handleValider()
        AC->>DD: updateStatut(id, VALIDEE)
        DD->>DB: UPDATE ...
        AC->>NS: creerNotification(étudiant, « Validée »)
    else Refuser
        V->>AC: handleRefuser(motif)
        AC->>DD: updateStatut(id, REFUSEE)
        DD->>DB: UPDATE ...
        AC->>NS: creerNotification(étudiant, « Refusée : » + motif)
    end
    V-->>A: ✅ Tableau rafraîchi
```

---

## 11. Diagramme d'états — Réservation de salle

```mermaid
stateDiagram-v2
    [*] --> EN_ATTENTE : creerReservation()

    EN_ATTENTE --> CONFIRMEE : validation admin
    EN_ATTENTE --> ANNULEE : annulation étudiant<br/>ou refus admin
    CONFIRMEE --> ANNULEE : annulation étudiant<br/>(avant date_debut)

    CONFIRMEE --> [*] : créneau terminé
    ANNULEE --> [*]

    note right of EN_ATTENTE
        Demande créée,
        en attente de
        validation
    end note
```

---

## 12. Diagramme d'états — Demande administrative

```mermaid
stateDiagram-v2
    [*] --> SOUMISE : soumettreDemandeAdministrative()

    SOUMISE --> EN_TRAITEMENT : admin prend en charge
    SOUMISE --> REFUSEE : refus immédiat
    EN_TRAITEMENT --> VALIDEE : validation
    EN_TRAITEMENT --> REFUSEE : refus motivé

    VALIDEE --> [*]
    REFUSEE --> [*]

    note right of SOUMISE
        Statut initial
        à la création
    end note
```

---

## 13. Diagramme d'états — Emprunt

```mermaid
stateDiagram-v2
    [*] --> EN_COURS : emprunterRessource()
    EN_COURS --> RETOURNE : retournerRessource()
    RETOURNE --> [*]

    note right of EN_COURS
        date_retour = null<br/>
        ressource.disponible = false
    end note

    note right of RETOURNE
        date_retour = today<br/>
        ressource.disponible = true
    end note
```

---

## 14. Diagramme d'activité — Workflow d'une demande

```mermaid
flowchart TD
    Start([Début]) --> Login{Étudiant<br/>authentifié ?}
    Login -->|Non| Auth[Se connecter] --> Login
    Login -->|Oui| Choix[Ouvre l'onglet « Demandes »]

    Choix --> Type[Choisit le type<br/>de demande]
    Type --> Desc[Saisit la description]
    Desc --> Submit{Description<br/>non vide ?}
    Submit -->|Non| Err[⚠️ Erreur de saisie] --> Desc
    Submit -->|Oui| Save[(INSERT demande<br/>statut = SOUMISE)]
    Save --> Notif1[🔔 Notification<br/>« Demande soumise »]

    Notif1 --> WaitAdmin[⏳ En attente admin]
    WaitAdmin --> Action{Action<br/>admin ?}

    Action -->|Prendre en charge| EnT[(statut = EN_TRAITEMENT)]
    EnT --> Notif2[🔔 « En traitement »]
    Notif2 --> WaitAdmin

    Action -->|Valider| Val[(statut = VALIDEE)]
    Val --> Notif3[🔔 « Validée »] --> End1([Fin])

    Action -->|Refuser| Ref[(statut = REFUSEE)]
    Ref --> Notif4[🔔 « Refusée + motif »] --> End2([Fin])

    classDef admin fill:#fee2e2,stroke:#b91c1c
    classDef student fill:#dbeafe,stroke:#1d4ed8
    classDef data fill:#fef3c7,stroke:#d97706
    class Type,Desc,Choix,Auth student
    class EnT,Val,Ref,Action admin
    class Save,Notif1,Notif2,Notif3,Notif4 data
```

---

## 15. Diagramme entité-association (MCD)

```mermaid
erDiagram
    ETUDIANTS ||--o{ EMPRUNTS              : effectue
    ETUDIANTS ||--o{ RESERVATIONS_SALLES   : reserve
    ETUDIANTS ||--o{ DEMANDES_ADMIN        : soumet
    ETUDIANTS ||--o{ NOTIFICATIONS         : recoit

    CATALOGUES ||--o{ RESSOURCES           : contient
    RESSOURCES ||--o{ EMPRUNTS             : empruntee_dans
    SALLES     ||--o{ RESERVATIONS_SALLES  : reservee_dans

    ETUDIANTS {
        int id PK
        string nom
        string prenom
        string email UK
        string mot_de_passe
        string filiere
        string statut "ACTIF/INACTIF"
    }

    ADMINS {
        int id PK
        string nom
        string prenom
        string email UK
        string mot_de_passe
        string role "ADMINISTRATEUR/BIBLIOTHECAIRE/RESPONSABLE_SALLES"
    }

    CATALOGUES {
        int id PK
        string nom
        string description
    }

    RESSOURCES {
        int id PK
        string type "LIVRE/MEMOIRE"
        string titre
        string auteur
        boolean disponible
        int catalogue_id FK
        string isbn "livre"
        int nb_pages "livre"
        string editeur "livre"
        string specialite "memoire"
        int annee "memoire"
        string encadrant "memoire"
    }

    EMPRUNTS {
        int id PK
        int etudiant_id FK
        int ressource_id FK
        date date_emprunt
        date date_retour "nullable"
    }

    SALLES {
        int id PK
        string nom
        int capacite
        string type
    }

    RESERVATIONS_SALLES {
        int id PK
        int etudiant_id FK
        int salle_id FK
        datetime date_debut
        datetime date_fin
        string statut "EN_ATTENTE/CONFIRMEE/ANNULEE"
    }

    DEMANDES_ADMIN {
        int id PK
        int etudiant_id FK
        string type "CERTIFICAT/ATTESTATION/RELEVE/AUTRE"
        string description
        string statut "SOUMISE/EN_TRAITEMENT/VALIDEE/REFUSEE"
        datetime date_creation
    }

    NOTIFICATIONS {
        int id PK
        int etudiant_id FK
        string message
        string type
        boolean lu
        datetime date_creation
    }
```

---

## 16. Diagramme de déploiement

```mermaid
flowchart LR
    subgraph Client["💻 Poste client (étudiant / admin)"]
        direction TB
        JRE["☕ JRE 19+"]
        APP["📦 campus-services-fat.jar<br/>(JavaFX 19.0.2 embarqué)"]
        CONF["📄 database.properties"]
        JRE --> APP
        APP --> CONF
    end

    subgraph Server["🖥 Serveur de base de données"]
        direction TB
        PG[("🐘 PostgreSQL 15+")]
        SCHEMA["📜 schema.sql<br/>(9 tables)"]
        PG --> SCHEMA
    end

    APP <-.->|JDBC 42.7.3<br/>TCP 5432| PG

    classDef node fill:#dbeafe,stroke:#1d4ed8
    classDef db fill:#fce7f3,stroke:#be185d
    class Client,JRE,APP,CONF node
    class Server,PG,SCHEMA db
```

**Détails de déploiement :**
- **Poste client** : JDK 19+, l'application est livrée en *fat-jar* (`maven-shade-plugin`), donc JavaFX n'a pas besoin d'être installé sur le poste.
- **Serveur** : PostgreSQL 15+ accessible en TCP sur le port 5432 (configurable).
- **Communication** : driver JDBC PostgreSQL 42.7.3 par-dessus TCP, identifiants dans `database.properties`.
- **Sécurité** : mots de passe stockés en **BCrypt** (jBCrypt 0.4) — jamais en clair.

---

<p align="center">
  📐 Tous les diagrammes sont au format <strong>Mermaid</strong> — éditables directement dans le fichier.<br/>
  Rendu natif sur <strong>GitHub</strong>, <strong>GitLab</strong>, <strong>VS Code</strong> (extension Mermaid Preview) et <strong>Notion</strong>.
</p>
