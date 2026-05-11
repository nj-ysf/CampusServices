-- ============================================================
--  CampusServices ENSET — Schéma PostgreSQL
--  Version 2.0  (MVC + DAO)
--  Exécuter dans psql :  \i schema.sql
-- ============================================================

-- Extensions
CREATE EXTENSION IF NOT EXISTS unaccent;

-- ── Étudiants ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS etudiants (
    id          SERIAL       PRIMARY KEY,
    nom         VARCHAR(100) NOT NULL,
    prenom      VARCHAR(100) NOT NULL,
    email       VARCHAR(200) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    filiere     VARCHAR(100),
    statut      VARCHAR(20)  NOT NULL DEFAULT 'ACTIF'
                CHECK (statut IN ('ACTIF','INACTIF','SUSPENDU'))
);

-- ── Personnel d'administration ─────────────────────────────
CREATE TABLE IF NOT EXISTS admins (
    id          SERIAL       PRIMARY KEY,
    nom         VARCHAR(100) NOT NULL,
    prenom      VARCHAR(100) NOT NULL,
    email       VARCHAR(200) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    role        VARCHAR(30)  NOT NULL
                CHECK (role IN ('ADMINISTRATEUR','BIBLIOTHECAIRE','RESPONSABLE_SALLES'))
);

-- ── Catalogues de bibliothèque ─────────────────────────────
CREATE TABLE IF NOT EXISTS catalogues (
    id          SERIAL       PRIMARY KEY,
    nom         VARCHAR(200) NOT NULL,
    description TEXT
);

-- ── Ressources (Livres + Mémoires, table unique) ───────────
CREATE TABLE IF NOT EXISTS ressources (
    id           SERIAL       PRIMARY KEY,
    titre        VARCHAR(300) NOT NULL,
    auteur       VARCHAR(200) NOT NULL,
    disponible   BOOLEAN      NOT NULL DEFAULT TRUE,
    catalogue_id INT          REFERENCES catalogues(id) ON DELETE SET NULL,
    type         VARCHAR(10)  NOT NULL CHECK (type IN ('LIVRE','MEMOIRE')),
    -- Champs spécifiques aux Livres
    isbn         VARCHAR(50),
    nb_pages     INT,
    editeur      VARCHAR(200),
    -- Champs spécifiques aux Mémoires
    specialite   VARCHAR(200),
    annee        INT,
    encadrant    VARCHAR(200)
);

-- ── Salles ─────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS salles (
    id        SERIAL      PRIMARY KEY,
    nom       VARCHAR(100) NOT NULL,
    capacite  INT          NOT NULL DEFAULT 0
);

-- ── Emprunts ───────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS emprunts (
    id           SERIAL  PRIMARY KEY,
    etudiant_id  INT     NOT NULL REFERENCES etudiants(id),
    ressource_id INT     NOT NULL REFERENCES ressources(id),
    date_emprunt DATE    NOT NULL DEFAULT CURRENT_DATE,
    date_retour  DATE             -- NULL = emprunt en cours
);

-- ── Réservations de salles ─────────────────────────────────
CREATE TABLE IF NOT EXISTS reservations_salles (
    id          SERIAL      PRIMARY KEY,
    etudiant_id INT         NOT NULL REFERENCES etudiants(id),
    salle_id    INT         NOT NULL REFERENCES salles(id),
    date_debut  TIMESTAMP   NOT NULL,
    date_fin    TIMESTAMP   NOT NULL,
    statut      VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE'
                CHECK (statut IN ('EN_ATTENTE','VALIDEE','REFUSEE','ANNULEE'))
);

-- ── Demandes administratives ───────────────────────────────
CREATE TABLE IF NOT EXISTS demandes_administratives (
    id               SERIAL      PRIMARY KEY,
    etudiant_id      INT         NOT NULL REFERENCES etudiants(id),
    type_demande     VARCHAR(50) NOT NULL,
    description      TEXT,
    statut           VARCHAR(20) NOT NULL DEFAULT 'SOUMISE'
                     CHECK (statut IN ('SOUMISE','EN_TRAITEMENT','VALIDEE','REFUSEE')),
    date_creation    TIMESTAMP   NOT NULL DEFAULT NOW(),
    commentaire_admin TEXT
);

-- ── Notifications ──────────────────────────────────────────
CREATE TABLE IF NOT EXISTS notifications (
    id          SERIAL      PRIMARY KEY,
    etudiant_id INT         NOT NULL REFERENCES etudiants(id),
    message     TEXT        NOT NULL,
    type        VARCHAR(50),
    lu          BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- ============================================================
--  Données de démarrage (optionnelles)
-- ============================================================

-- Comptes admin
INSERT INTO admins (nom, prenom, email, mot_de_passe, role) VALUES
  ('Administrateur', 'Super',  'admin@enset.ac.ma',  'admin2025',  'ADMINISTRATEUR'),
  ('Bouali',         'Karim',  'biblio@enset.ac.ma', 'biblio2025', 'BIBLIOTHECAIRE'),
  ('Mansouri',       'Leila',  'salles@enset.ac.ma', 'salles2025', 'RESPONSABLE_SALLES')
ON CONFLICT (email) DO NOTHING;

-- Comptes étudiants de test
INSERT INTO etudiants (nom, prenom, email, mot_de_passe, filiere, statut) VALUES
  ('Naji',         'Youssef', 'youssef.naji@enset.ac.ma',         'enset2025', 'Génie Informatique',       'ACTIF'),
  ('Bousserhane',  'Brahim',  'brahim.bousserhane@enset.ac.ma',   'enset2025', 'Génie Électrique',         'ACTIF'),
  ('Ezzahery',     'Ahmed',   'ahmed.ezzahery@enset.ac.ma',       'enset2025', 'Génie Mécanique',          'ACTIF'),
  ('Benali',       'Sara',    'sara.benali@enset.ac.ma',          'enset2025', 'Génie Civil',              'ACTIF'),
  ('Chafik',       'Omar',    'omar.chafik@enset.ac.ma',          'enset2025', 'Génie des Systèmes',       'INACTIF')
ON CONFLICT (email) DO NOTHING;

-- Catalogues
INSERT INTO catalogues (nom, description) VALUES
  ('Informatique & Réseaux',    'Ouvrages de programmation, réseaux, IA et systèmes'),
  ('Sciences de l''Ingénieur',  'Mathématiques, physique, mécanique, électronique'),
  ('Mémoires de fin d''études', 'Mémoires PFE et master ENSET')
ON CONFLICT DO NOTHING;

-- Ressources — Livres
INSERT INTO ressources (titre, auteur, disponible, catalogue_id, type, isbn, nb_pages, editeur) VALUES
  ('Clean Code',            'Robert C. Martin', TRUE, 1, 'LIVRE', '978-0132350884', 431,  'Prentice Hall'),
  ('Design Patterns',       'GoF',              TRUE, 1, 'LIVRE', '978-0201633610', 395,  'Addison-Wesley'),
  ('Introduction aux réseaux', 'James F. Kurose', TRUE, 1, 'LIVRE', '978-2744075667', 855, 'Pearson'),
  ('Algèbre linéaire',      'Gilbert Strang',   TRUE, 2, 'LIVRE', '978-0980232714', 584,  'Wellesley'),
  ('Mécanique des fluides', 'Frank White',      TRUE, 2, 'LIVRE', '978-0073529349', 866,  'McGraw-Hill')
ON CONFLICT DO NOTHING;

-- Ressources — Mémoires
INSERT INTO ressources (titre, auteur, disponible, catalogue_id, type, specialite, annee, encadrant) VALUES
  ('Système de gestion des stages',             'Youssef Naji',    TRUE, 3, 'MEMOIRE', 'Génie Informatique', 2023, 'Pr. Alami'),
  ('Optimisation des réseaux électriques',      'Brahim Bousserhane', TRUE, 3, 'MEMOIRE', 'Génie Électrique', 2023, 'Pr. Idrissi'),
  ('Application IoT pour bâtiments intelligents','Sara Benali',    TRUE, 3, 'MEMOIRE', 'Génie des Systèmes', 2024, 'Pr. Tahiri')
ON CONFLICT DO NOTHING;

-- Salles
INSERT INTO salles (nom, capacite) VALUES
  ('Salle A101', 30), ('Salle A102', 25), ('Salle B201', 40),
  ('Salle B202', 20), ('Labo Informatique 1', 24), ('Amphi 1', 120)
ON CONFLICT DO NOTHING;
