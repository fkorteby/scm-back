-- ==============================================
-- Flyway Migration V11 : Insert ParametreDefinition
-- ==============================================

-- Table: Duree
INSERT INTO parametre_definition (type, column_name, column_label, column_type)
VALUES ('Duree', 'duree', 'Durée', 'String');

-- Table: Conduite
INSERT INTO parametre_definition (type, column_name, column_label, column_type)
VALUES ('Conduite', 'conduite', 'Texte', 'String');

-- Table: Medicament
INSERT INTO parametre_definition (type, column_name, column_label, column_type)
VALUES ('Medicament', 'nomCommerciale', 'Nom Commerciale', 'String'),
       ('Medicament', 'dci', 'DCI', 'String'),
       ('Medicament', 'dosage', 'Dosage', 'String'),
       ('Medicament', 'conditionnement', 'Conditionnement', 'String'),
       ('Medicament', 'laboMedicament', 'Laboratoire', 'String'),
       ('Medicament', 'remMedicament', 'Remarques', 'String'),
    ('Medicament', 'forme', 'Forme', 'String'),
    ('Medicament', 'posologie', 'Posologie', 'String'),
    ('Medicament', 'duree', 'Duree', 'String');

-- Table: ExamenParacliniqueType
INSERT INTO parametre_definition (type, column_name, column_label, column_type)
VALUES ('ExamenParacliniqueType', 'nomExamenParaclinique', 'Nom Examen Paraclinique', 'String'),
       ('ExamenParacliniqueType', 'text', 'Texte', 'String');

-- Table: Forme
INSERT INTO parametre_definition (type, column_name, column_label, column_type)
VALUES ('Forme', 'forme', 'Forme', 'String'),
       ('Forme', 'abreviation', 'Abréviation', 'String');

-- Table: Motif

INSERT INTO parametre_definition (type, column_name, column_label, column_type)
VALUES ('Motif', 'motif', 'Motif', 'String');

-- Table: OptionParaclinique
INSERT INTO parametre_definition (type, column_name, column_label, column_type)
VALUES ('OptionParaclinique', 'idOptionParaclinique', 'ID Option Paraclinique', 'Long'),
       ('OptionParaclinique', 'option', 'Option', 'String');

-- Table: Paraclinique
INSERT INTO parametre_definition (type, column_name, column_label, column_type)
VALUES ('Paraclinique', 'examen', 'Examen', 'String'),
       ('Paraclinique', 'type', 'Type', 'String');

-- Table: Posologie
INSERT INTO parametre_definition (type, column_name, column_label, column_type)
VALUES ('Posologie', 'posologie', 'Posologie', 'String');
