-- ============================================================
-- Séquence pour Patient
-- ============================================================

CREATE SEQUENCE IF NOT EXISTS patient_id_seq
    START WITH 1
    INCREMENT BY 1;

ALTER TABLE patient
    ALTER COLUMN id_patient
        SET DEFAULT nextval('patient_id_seq');

ALTER SEQUENCE patient_id_seq
    OWNED BY patient.id_patient;

SELECT setval(
               'patient_id_seq',
               COALESCE((SELECT MAX(id_patient) FROM patient), 0) + 1,
               false
       );


-- ============================================================
-- Séquence pour Consultation
-- ============================================================

CREATE SEQUENCE IF NOT EXISTS consultation_id_seq
    START WITH 1
    INCREMENT BY 1;

ALTER TABLE consultation
    ALTER COLUMN id_consultation
        SET DEFAULT nextval('consultation_id_seq');

ALTER SEQUENCE consultation_id_seq
    OWNED BY consultation.id_consultation;

SELECT setval(
               'consultation_id_seq',
               COALESCE((SELECT MAX(id_consultation) FROM consultation), 0) + 1,
               false
       );


-- ============================================================
-- Séquence pour Medicament
-- ============================================================

CREATE SEQUENCE IF NOT EXISTS medicament_id_seq
    START WITH 1
    INCREMENT BY 1;

ALTER TABLE medicament
    ALTER COLUMN id_medicament
        SET DEFAULT nextval('medicament_id_seq');

ALTER SEQUENCE medicament_id_seq
    OWNED BY medicament.id_medicament;

SELECT setval(
               'medicament_id_seq',
               COALESCE((SELECT MAX(id_medicament) FROM medicament), 0) + 1,
               false
       );


-- ============================================================
-- Séquence pour Traitement
-- ============================================================

CREATE SEQUENCE IF NOT EXISTS traitement_id_seq
    START WITH 1
    INCREMENT BY 1;

ALTER TABLE traitement
    ALTER COLUMN id_traitement
        SET DEFAULT nextval('traitement_id_seq');

ALTER SEQUENCE traitement_id_seq
    OWNED BY traitement.id_traitement;

SELECT setval(
               'traitement_id_seq',
               COALESCE((SELECT MAX(id_traitement) FROM traitement), 0) + 1,
               false
       );