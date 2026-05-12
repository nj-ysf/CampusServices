# 🎓 CampusServices — ENSET

> Application JavaFX de gestion des services étudiants : bibliothèque, réservation de salles, demandes administratives et notifications.

![Java](https://img.shields.io/badge/Java-19-007396?logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-19.0.2-1A73E8?logo=java&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.6%2B-C71A36?logo=apachemaven&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15%2B-336791?logo=postgresql&logoColor=white)
![License](https://img.shields.io/badge/license-Academic-blue)

---

## 📋 Table des matières

- [Présentation](#-présentation)
- [Fonctionnalités](#-fonctionnalités)
- [Architecture](#-architecture)
- [Stack technique](#-stack-technique)
- [Prérequis](#-prérequis)
- [Installation](#-installation)
- [Configuration de la base de données](#-configuration-de-la-base-de-données)
- [Lancement](#-lancement)
- [Comptes de démonstration](#-comptes-de-démonstration)
- [Structure du projet](#-structure-du-projet)
- [Modèle de données](#-modèle-de-données)
- [Captures & design](#-captures--design)
- [Auteur](#-auteur)

---

## 🎯 Présentation

**CampusServices** est une application desktop développée en **JavaFX 19** pour l'**ENSET Mohammedia**. Elle centralise les principaux services destinés aux étudiants et au personnel administratif :

- 📚 Consultation et emprunt de ressources de la **bibliothèque** (livres et mémoires PFE)
- 🏫 **Réservation de salles** de cours, labos et amphis
- 📋 Soumission et suivi de **demandes administratives** (certificats, attestations, relevés…)
- 🔔 **Notifications** ciblées et globales
- 👥 **Gestion des étudiants** côté administration (activation, suspension, suivi des emprunts)

L'application propose deux espaces distincts :

| Espace | Public visé | Accès |
|---|---|---|
| **Étudiant** | Étudiants ENSET | Consulter le catalogue, emprunter, réserver des salles, soumettre des demandes, voir ses notifications |
| **Administration** | Administrateur, Bibliothécaire, Responsable salles | Gérer étudiants, catalogue, réservations, demandes et envoyer des notifications |

---

## ✨ Fonctionnalités

### Espace Étudiant

- 🔐 Connexion sécurisée par email + mot de passe (BCrypt)
- 📊 Tableau de bord avec les indicateurs clés (emprunts en cours, réservations actives, demandes en cours, notifications non lues)
- 📚 Bibliothèque : recherche par titre/auteur, filtre par type (Livre / Mémoire), filtre disponibilité
- 📖 Mes emprunts : suivi et retour des ressources
- 🏫 Réservations : création de créneaux, annulation, suivi du statut (en attente / confirmée / annulée)
- 📋 Demandes administratives : soumission avec motif, suivi du statut
- 🔔 Centre de notifications avec badge de non-lus

### Espace Administration

- 📊 Tableau de bord global (étudiants inscrits, emprunts actifs, réservations à valider, demandes à traiter)
- 👥 Gestion des étudiants : recherche, activation / désactivation des comptes
- 🏫 Validation ou refus des réservations de salles, avec filtre par statut
- 📋 Traitement des demandes administratives (mise en traitement / validation / refus avec motif)
- 📚 Gestion du catalogue : ajout de livres et de mémoires, suppression de ressources
- 🔔 Envoi de notifications ciblées (un étudiant) ou globales (tous les étudiants)

---

## 🏗 Architecture

L'application suit une architecture **MVC + DAO** en couches :

```
┌─────────────────────────────────────────────────────────┐
│              VUE — JavaFX / FXML / CSS                  │
│        (login.fxml, dashboard.fxml, admin-*.fxml)       │
└──────────────────────────┬──────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────┐
│   CONTRÔLEURS — ma.enset.campusservices.controller      │
│      LoginController, MainController, AdminMain…        │
└──────────────────────────┬──────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────┐
│       SERVICES — ma.enset.campusservices.service        │
│  AuthService, BibliothequeService, ReservationService…  │
└──────────────────────────┬──────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────┐
│   DAO (Interfaces + Impl) — ma.enset.campusservices.dao │
│        IEtudiantDAO, IRessourceDAO, IEmpruntDAO…        │
└──────────────────────────┬──────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────┐
│            PostgreSQL  (schema.sql, JDBC)               │
└─────────────────────────────────────────────────────────┘
```

**Principes appliqués :**
- Séparation stricte UI / logique métier / accès aux données
- Programmation par **interface** (chaque DAO et service expose un contrat)
- Modèle objet riche avec héritage : `Ressource` ← `Livre` / `Memoire`
- Énumérations pour les statuts : `Role`, `StatutEtudiant`, `StatutDemande`, `StatutReservation`, `TypeDemande`
- Hachage **BCrypt** des mots de passe (aucun mot de passe en clair en base)

---

## 🛠 Stack technique

| Couche | Technologie |
|---|---|
| Langage | **Java 19** |
| UI | **JavaFX 19.0.2** (Controls + FXML) |
| Design | CSS personnalisé inspiré de **Bootstrap 5** |
| Build | **Maven** (javafx-maven-plugin, maven-shade-plugin) |
| Base de données | **PostgreSQL 15+** |
| Driver JDBC | `org.postgresql:postgresql:42.7.3` |
| Hachage mot de passe | **jBCrypt** `0.4` |

---

## ✅ Prérequis

Avant de lancer le projet, assurez-vous d'avoir :

- ☕ **JDK 19** (ou supérieur) — `java -version`
- 🔧 **Maven 3.6+** — `mvn -version`
- 🐘 **PostgreSQL 15+** installé et démarré
- 🧰 (Optionnel) Un client SQL comme **psql**, **DBeaver** ou **pgAdmin**

---

## 🚀 Installation

### 1. Cloner le dépôt

```bash
git clone <url-du-repo>
cd campus-services
```

### 2. Installer les dépendances

```bash
mvn clean install
```

Maven télécharge automatiquement :
- JavaFX Controls + FXML 19.0.2
- Le driver PostgreSQL 42.7.3
- jBCrypt 0.4

---

## 🗄 Configuration de la base de données

### 1. Créer la base PostgreSQL

```bash
psql -U postgres
```

```sql
CREATE DATABASE campus_services;
\c campus_services
\i schema.sql
```

Le fichier [`schema.sql`](schema.sql) crée toutes les tables :

- `etudiants`, `admins`
- `catalogues`, `ressources`
- `salles`, `reservations_salles`
- `emprunts`
- `demandes_administratives`
- `notifications`

Il insère également des **données de démarrage** : 3 comptes admin, 5 étudiants de test, 3 catalogues, 8 ressources et 6 salles.

### 2. Configurer la connexion

Éditer [`src/main/resources/database.properties`](src/main/resources/database.properties) si nécessaire :

```properties
db.host=localhost
db.port=5432
db.name=campus_services
db.user=postgres
db.password=votre_mot_de_passe
```

> ⚠️ Ne commitez **jamais** votre vrai mot de passe. Pour un usage en équipe, utilisez plutôt une variable d'environnement.

---

## ▶️ Lancement

### En développement (via Maven)

**Linux / macOS :**
```bash
./run.sh
```

**Windows :**
```bat
run.bat
```

Ou directement :
```bash
mvn javafx:run
```

### Construire un JAR exécutable (fat JAR)

```bash
mvn clean package
```

Le JAR est généré dans `target/campus-services-fat.jar`. Il embarque JavaFX, on peut donc le lancer sur n'importe quelle machine ayant un JDK 19+ :

```bash
java -jar target/campus-services-fat.jar
```

---

## 👤 Comptes de démonstration

Créés automatiquement par `schema.sql`.

### Comptes administration

| Email | Mot de passe | Rôle |
|---|---|---|
| `admin@enset.ac.ma`  | `admin2025`  | Administrateur |
| `biblio@enset.ac.ma` | `biblio2025` | Bibliothécaire |
| `salles@enset.ac.ma` | `salles2025` | Responsable salles |

### Comptes étudiants

| Email | Mot de passe | Filière | Statut |
|---|---|---|---|
| `youssef.naji@enset.ac.ma` | `enset2025` | Génie Informatique | Actif |
| `brahim.bousserhane@enset.ac.ma` | `enset2025` | Génie Électrique | Actif |
| `ahmed.ezzahery@enset.ac.ma` | `enset2025` | Génie Mécanique | Actif |
| `sara.benali@enset.ac.ma` | `enset2025` | Génie Civil | Actif |
| `omar.chafik@enset.ac.ma` | `enset2025` | Génie des Systèmes | Inactif |

---

## 📂 Structure du projet

```
campus-services/
├── pom.xml                    # Configuration Maven
├── schema.sql                 # Création de la base PostgreSQL + jeu de données
├── run.sh / run.bat           # Scripts de lancement
├── README.md
└── src/main/
    ├── java/ma/enset/campusservices/
    │   ├── MainApp.java             # Point d'entrée JavaFX (gère les écrans)
    │   ├── Launcher.java            # Lanceur pour le JAR exécutable
    │   ├── controller/              # 15 contrôleurs JavaFX (étudiant + admin)
    │   │   ├── LoginController.java
    │   │   ├── MainController.java          # Conteneur étudiant
    │   │   ├── DashboardController.java
    │   │   ├── BibliothequeController.java
    │   │   ├── EmpruntsController.java
    │   │   ├── ReservationsController.java
    │   │   ├── DemandesController.java
    │   │   ├── NotificationsController.java
    │   │   ├── AdminMainController.java     # Conteneur admin
    │   │   ├── AdminDashboardController.java
    │   │   ├── AdminEtudiantsController.java
    │   │   ├── AdminCatalogueController.java
    │   │   ├── AdminReservationsController.java
    │   │   ├── AdminDemandesController.java
    │   │   └── AdminNotificationsController.java
    │   ├── service/                 # Couche métier
    │   │   ├── IAuthentification.java, AuthService.java
    │   │   ├── IGestionable.java
    │   │   ├── BibliothequeService.java
    │   │   ├── ReservationService.java
    │   │   ├── DemandeService.java
    │   │   └── NotificationService.java
    │   ├── dao/                     # Interfaces DAO + implémentations JDBC
    │   │   ├── IEtudiantDAO.java, IAdminDAO.java
    │   │   ├── IRessourceDAO.java, ICatalogueDAO.java
    │   │   ├── ISalleDAO.java, IReservationDAO.java
    │   │   ├── IEmpruntDAO.java, IDemandeDAO.java
    │   │   ├── INotificationDAO.java
    │   │   └── impl/*.java
    │   ├── model/                   # POJO métier
    │   │   ├── Etudiant.java, Admin.java
    │   │   ├── Ressource.java (abstract), Livre.java, Memoire.java
    │   │   ├── Catalogue.java, Salle.java
    │   │   ├── Emprunt.java, ReservationSalle.java
    │   │   ├── DemandeAdministrative.java
    │   │   ├── Notification.java
    │   │   └── enums/Role.java, Statut*.java, TypeDemande.java
    │   ├── database/
    │   │   └── DatabaseConfig.java  # Connexion JDBC PostgreSQL
    │   ├── repository/
    │   │   └── DataStore.java       # Session utilisateur courante
    │   └── security/
    │       └── PasswordHash.java    # Wrapper BCrypt
    └── resources/
        ├── database.properties
        └── ma/enset/campusservices/
            ├── login.fxml
            ├── main.fxml, dashboard.fxml,
            ├── bibliotheque.fxml, emprunts.fxml,
            ├── reservations.fxml, demandes.fxml, notifications.fxml,
            ├── admin-main.fxml, admin-dashboard.fxml,
            ├── admin-etudiants.fxml, admin-catalogue.fxml,
            ├── admin-reservations.fxml, admin-demandes.fxml,
            ├── admin-notifications.fxml
            └── css/style.css        # Thème Bootstrap 5 inspired
```

---

## 🗃 Modèle de données

| Table | Description |
|---|---|
| `etudiants` | Comptes étudiants (nom, prénom, email, filière, statut, mot de passe BCrypt) |
| `admins` | Personnel administratif (avec rôle : `ADMINISTRATEUR`, `BIBLIOTHECAIRE`, `RESPONSABLE_SALLES`) |
| `catalogues` | Regroupements thématiques de ressources |
| `ressources` | Table unique pour `LIVRE` et `MEMOIRE` (champs spécifiques séparés) |
| `emprunts` | Lien étudiant ↔ ressource avec dates d'emprunt et de retour |
| `salles` | Salles de cours, labos et amphis (avec capacité) |
| `reservations_salles` | Demandes de réservation avec statut `EN_ATTENTE` / `CONFIRMEE` / `ANNULEE` |
| `demandes_administratives` | Certificats, attestations, relevés… avec statut `SOUMISE` / `EN_TRAITEMENT` / `VALIDEE` / `REFUSEE` |
| `notifications` | Messages adressés à un étudiant, indicateur lu / non-lu |

Contraintes principales :
- Clés étrangères avec intégrité référentielle
- Contraintes `CHECK` sur tous les champs `statut` / `role` / `type`
- Index implicites sur les clés primaires et email unique

---

## 🎨 Captures & design

Le design est inspiré de **Bootstrap 5** :

- 🎨 Palette officielle BS5 (`#0d6efd` primaire, `#198754` succès, `#dc3545` danger, `#ffc107` warning)
- 🌐 Login avec gradient mesh et carte flottante
- 🧭 Sidebar sombre style `navbar bg-dark` avec onglet actif en bleu
- 🃏 Cartes statistiques avec bandeau coloré sur le bord supérieur
- 📋 Tables avec lignes alternées, hover bleu et états sélectionnés
- 🔵 Anneau de focus bleu sur tous les champs (signature Bootstrap)
- ⚪ Boutons arrondis avec ombre douce au hover

Le stylesheet unique se trouve dans :
```
src/main/resources/ma/enset/campusservices/css/style.css
```

---

## 👨‍💻 Auteur

**Youssef Naji** — Étudiant ENSET Mohammedia  
📧 youssef.naji7-etu@etu.univh2c.ma

Projet réalisé dans le cadre du module *Programmation orientée objet / Conception logicielle* — **ENSET Mohammedia**.

---

## 📄 Licence

Projet académique — usage pédagogique uniquement.

---

<p align="center">
  Fait avec ❤️ et beaucoup de ☕ à l'ENSET Mohammedia
</p>
