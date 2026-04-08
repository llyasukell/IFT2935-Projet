
CREATE TYPE etat_produit AS ENUM ('neuf', 'usage', 'abime');
CREATE TYPE etat_decision AS ENUM ('en_attente', 'valide', 'rejete');


CREATE TABLE Utilisateur (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    courriel VARCHAR(255) UNIQUE NOT NULL,
    mot_de_passe VARCHAR(255) NOT NULL
);

CREATE TABLE Acheteur (
    id_utilisateur INTEGER PRIMARY KEY REFERENCES Utilisateur(id) ON DELETE CASCADE
);

CREATE TABLE Annonceur (
    id_utilisateur INTEGER PRIMARY KEY REFERENCES Utilisateur(id) ON DELETE CASCADE
);

CREATE TABLE Expert (
    id_utilisateur INTEGER PRIMARY KEY REFERENCES Utilisateur(id) ON DELETE CASCADE
);


CREATE TABLE Produit (
    id_produit SERIAL PRIMARY KEY,
    nom_produit VARCHAR(255) NOT NULL,
    etat etat_produit,
    description TEXT,
    prix_souhaite DECIMAL(12, 2) NOT NULL
);


CREATE TABLE Offre (
    id_offre SERIAL PRIMARY KEY,
    prix_propose DECIMAL(12, 2) NOT NULL,
    id_produit INTEGER NOT NULL REFERENCES Produit(id_produit) ON DELETE CASCADE
);


CREATE TABLE Estimation (
    id_estimation SERIAL PRIMARY KEY,
    prix_estimation DECIMAL(12, 2) NOT NULL,
    ville VARCHAR(255),
    id_expert INTEGER NOT NULL REFERENCES Expert(id_utilisateur),
    id_produit INTEGER NOT NULL REFERENCES Produit(id_produit)
);


CREATE TABLE Valide (
    id_annonceur INTEGER,
    id_estimation INTEGER,
    decision etat_decision DEFAULT 'en_attente',
    PRIMARY KEY (id_annonceur, id_estimation),
    FOREIGN KEY (id_annonceur) REFERENCES Annonceur(id_utilisateur) ON DELETE CASCADE,
    FOREIGN KEY (id_estimation) REFERENCES Estimation(id_estimation) ON DELETE CASCADE
);

CREATE TABLE propose (
    id_offre INTEGER,
    id_utilisateur INTEGER, 
    date_proposition DATE DEFAULT CURRENT_DATE,
    PRIMARY KEY (id_offre, id_utilisateur),
    FOREIGN KEY (id_offre) REFERENCES Offre(id_offre) ON DELETE CASCADE,
    FOREIGN KEY (id_utilisateur) REFERENCES Acheteur(id_utilisateur) ON DELETE CASCADE
);