package com.simple_cabinet_medical.Backend.service.MdbImport;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parse le champ traitement brut de l'ancienne BDD (format texte libre)
 * Exemple d'entrée :
 *   AMLOR[5MG] gel     (QSP 1 mois
 *   1 gél/jr.
 *
 *   LOPRIL[50MG] cp.     (QSP 1 mois
 *   1 cp./jr.
 */
public class TraitementParser {

    /**
     * Résultat du parsing d'un seul médicament dans le champ traitement
     */
    public static class ParsedTraitement {
        public String nomCommerciale;   // ex: "AMLOR"
        public String dosage;           // ex: "5MG"
        public String forme;            // ex: "gel"
        public String duree;            // ex: "QSP 1 mois"
        public String posologie;        // ex: "1 gél/jr."

        @Override
        public String toString() {
            return "ParsedTraitement{" +
                    "nom='" + nomCommerciale + '\'' +
                    ", dosage='" + dosage + '\'' +
                    ", forme='" + forme + '\'' +
                    ", duree='" + duree + '\'' +
                    ", posologie='" + posologie + '\'' +
                    '}';
        }
    }

    /**
     * Pattern principal :
     *   NOM_MEDICAMENT[DOSAGE] FORME   (DUREE
     *   POSOLOGIE
     *
     * Groupe 1 : nom commercial (lettres, chiffres, tirets, espaces)
     * Groupe 2 : dosage (ex: 5MG, 100MG/5ML, 800MG/160MG)
     * Groupe 3 : forme (ex: gel, cp., susp.bvle., gttes auric.)
     * Groupe 4 : durée (tout ce qui suit "(")
     */
    private static final Pattern MEDICAMENT_PATTERN = Pattern.compile(
            "([A-Z][A-Z0-9\\s\\-]+?)\\[([^\\]]+)\\]\\s*([^(\\n]+?)\\s*\\(([^\\n]+)",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * Parse un champ traitement complet et retourne la liste des médicaments trouvés.
     *
     * @param rawTraitement le champ texte brut depuis la BDD Access
     * @return liste de ParsedTraitement (vide si null ou vide)
     */
    public static List<ParsedTraitement> parse(String rawTraitement) {
        List<ParsedTraitement> result = new ArrayList<>();

        if (rawTraitement == null || rawTraitement.isBlank()) {
            return result;
        }

        // Découper en blocs par médicament
        // Chaque bloc commence par un nom en majuscules suivi de [dosage]
        String[] lines = rawTraitement.split("\\r?\\n");

        StringBuilder currentBlock = new StringBuilder();

        for (String line : lines) {
            String trimmed = line.trim();

            // Nouvelle ligne de médicament : commence par lettres majuscules puis [
            boolean isNewMed = trimmed.matches("^[A-Z][A-Z0-9\\s\\-]*\\[.*");

            if (isNewMed && currentBlock.length() > 0) {
                // Traiter le bloc précédent
                ParsedTraitement t = parseBlock(currentBlock.toString());
                if (t != null) result.add(t);
                currentBlock = new StringBuilder();
            }

            if (!trimmed.isEmpty()) {
                currentBlock.append(trimmed).append("\n");
            }
        }

        // Dernier bloc
        if (currentBlock.length() > 0) {
            ParsedTraitement t = parseBlock(currentBlock.toString());
            if (t != null) result.add(t);
        }

        return result;
    }

    private static ParsedTraitement parseBlock(String block) {
        Matcher m = MEDICAMENT_PATTERN.matcher(block);
        if (!m.find()) return null;

        ParsedTraitement t = new ParsedTraitement();
        t.nomCommerciale = m.group(1).trim().toUpperCase();
        t.dosage         = m.group(2).trim();
        t.forme          = cleanForme(m.group(3).trim());
        t.duree          = m.group(4).trim();

        // La posologie est sur les lignes suivantes après la première ligne
        String afterFirstLine = block.substring(m.end()).trim();
        if (!afterFirstLine.isEmpty()) {
            // Prendre uniquement la première ligne non vide comme posologie
            String[] posLines = afterFirstLine.split("\\r?\\n");
            for (String pl : posLines) {
                String pl2 = pl.trim();
                if (!pl2.isEmpty()) {
                    t.posologie = pl2;
                    break;
                }
            }
        }

        return t;
    }

    /**
     * Nettoie la forme pharmaceutique (supprime les points et espaces superflus)
     */
    private static String cleanForme(String forme) {
        if (forme == null) return null;
        // Garder: gel, cp, susp.bvle, gttes auric, sacht, gél, cp.lp, supp, srp, lyoc, etc.
        return forme.replaceAll("\\s+", " ").trim();
    }
}