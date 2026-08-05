-- Création d’un client par défaut
-- Création d’un client par défaut
INSERT INTO client (
    nom_client,
    nom_client_en_arabe,
    email,
    contact,
    adresse,
    telephone,
    mobile,
    site,
    description,
    status,
    image,
    ville,
    pays,
    code_postal,  -- CORRECTION ICI (était codePostal)
    rue,
    client_config_id
)
VALUES (
           'Client par défaut',
           'العميل الإفتراضي',
           'default@client.com',
           'Contact par défaut',
           'Adresse par défaut',
           '0100000000',
           '0600000000',
           'www.default.com',
           'Client de démonstration inséré via Flyway',
           'ACTIVE',
           NULL,
           'Ville par défaut',
           'Pays par défaut',
           '00000',
           'Rue par défaut',
           NULL
       );

-- Le reste du script (INSERT INTO client_config et UPDATE) reste inchangé...

-- Création de la configuration du client
INSERT INTO client_config (heure_debut_travail,
                           heure_fin_travail,
                           duree_rendez_vous,
                           use_default_medicament,
                           use_default_forms,
                           use_default_duree,
                           use_default_posologie,
                           use_default_motifs,
                           use_default_conduit,
                           use_default_par_clinique,
                           use_default_option_pat_clinique
--     id_utilisateur,
--     client_creator_id,
--     date_creation,
--     date_modification
)
VALUES ('08:00',
        '17:00',
        30,
        TRUE,
        TRUE,
        TRUE,
        TRUE,
        TRUE,
        TRUE,
        TRUE,
        TRUE
--              1,   -- id_utilisateur
--              1,   -- client_creator_id = idClient
--              NOW(),
--              NOW()
       );

-- Mise à jour du client pour lier sa config
UPDATE client
SET client_config_id = 1
WHERE id_client = 1;
