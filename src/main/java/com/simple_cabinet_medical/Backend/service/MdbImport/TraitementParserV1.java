package com.simple_cabinet_medical.Backend.service.MdbImport;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser de traitements pour les fichiers MDB v1.0.
 *
 * Gère deux formats observés dans les données réelles :
 *
 * FORMAT A — tiret en début de ligne (id_cons=1) :
 *   - DOLIPRANE 1GR                   01 BTE / 1cp 3xjr.
 *   - RHINATIOL SIROP A               01 FL / 1 cas 3xjr.
 *   Règle : chaque ligne commençant par "- " est un médicament complet
 *           (nom [dosage] [forme] / posologie sur la même ligne).
 *
 * FORMAT B — alternance nom/posologie sans tiret (id_cons=2) :
 *   amlor 5mg
 *   1 gel./j
 *   lopril 50mg
 *   1 cp/j
 *   Règle : lignes impaires = "nom dosage", lignes paires = posologie.
 *
 * La détection du format est automatique : si le texte contient au moins
 * une ligne commençant par "- " (après trim), on applique FORMAT A,
 * sinon FORMAT B.
 */
public class TraitementParserV1 {

    public static class ParsedTraitement {
        public String nomCommerciale;
        public String dosage;
        public String forme;
        public String posologie;
        public String duree;
    }

    // ── Formes galéniques reconnues (insensible à la casse) ─────────────────
    private static final List<String> FORMES = List.of(
            "gel", "gél", "cp", "cpr", "cps", "comprimé", "comprime",
            "sirop", "susp", "sachet", "amp", "inj", "sol", "pom", "crème",
            "creme", "patch", "spray", "fl", "bte", "flacon", "boite", "boîte",
            "caps", "suppositoire", "suppo"
    );

    // ── Pattern dosage : chiffre(s) + unité ─────────────────────────────────
    private static final Pattern DOSAGE_PATTERN =
            Pattern.compile("\\b(\\d+(?:[.,]\\d+)?\\s*(?:mg|g|ml|ui|mcg|µg|%))\\b",
                    Pattern.CASE_INSENSITIVE);

    // ── Pattern quantité/boîte : "01 BTE", "01 FL" ──────────────────────────
    private static final Pattern QTY_PATTERN =
            Pattern.compile("^\\d+\\s+(?:bte|fl|flacon|boite|boîte|amp)[\\.\\s]?",
                    Pattern.CASE_INSENSITIVE);

    // ════════════════════════════════════════════════════════════════════════
    //  Point d'entrée principal
    // ════════════════════════════════════════════════════════════════════════

    public static List<ParsedTraitement> parse(String raw) {
        if (raw == null || raw.isBlank()) return List.of();

        // Normalisation : supprimer les retours chariot Windows
        String text = raw.replace("\r\n", "\n").replace("\r", "\n");

        // Détection du format
        boolean isFormatA = text.lines()
                .anyMatch(l -> l.stripLeading().startsWith("- "));

        return isFormatA ? parseFormatA(text) : parseFormatB(text);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  FORMAT A : lignes commençant par "- "
    //  Exemple : "- DOLIPRANE 1GR                     01 BTE / 1cp 3xjr."
    // ════════════════════════════════════════════════════════════════════════

    private static List<ParsedTraitement> parseFormatA(String text) {
        List<ParsedTraitement> results = new ArrayList<>();

        for (String rawLine : text.split("\n")) {
            String line = rawLine.stripLeading();
            if (!line.startsWith("- ")) continue;

            // Supprimer le tiret et normaliser les espaces internes
            String content = line.substring(2).trim();
            // Réduire les espaces multiples en un seul (les champs sont souvent alignés)
            content = content.replaceAll("\\s{2,}", " ");

            if (content.isBlank()) continue;

            ParsedTraitement pt = splitFormatAContent(content);
            if (pt != null && isValidName(pt.nomCommerciale)) {
                results.add(pt);
            }
        }
        return results;
    }

    /**
     * Découpe une ligne FORMAT A en ses composants.
     *
     * Exemple : "DOLIPRANE 1GR 01 BTE / 1cp 3xjr."
     *           nom="DOLIPRANE" dosage="1GR" posologie="1cp 3xjr."
     *
     * La posologie se trouve après "/" ou après la quantité/boîte.
     */
    private static ParsedTraitement splitFormatAContent(String content) {
        ParsedTraitement pt = new ParsedTraitement();

        // 1. Extraire la posologie : ce qui suit le "/" s'il existe
        String namePart;
        String posoPart = null;

        int slashIdx = content.indexOf('/');
        if (slashIdx > 0) {
            namePart = content.substring(0, slashIdx).trim();
            posoPart = content.substring(slashIdx + 1).trim();
        } else {
            namePart = content;
        }

        // 2. Dans namePart, supprimer la quantité/boîte ("01 BTE", "01 FL"…)
        namePart = QTY_PATTERN.matcher(namePart).replaceAll("").trim();

        // 3. Extraire le dosage du namePart
        Matcher dosageMatcher = DOSAGE_PATTERN.matcher(namePart);
        String dosage = null;
        if (dosageMatcher.find()) {
            dosage = dosageMatcher.group(1).trim();
            namePart = (namePart.substring(0, dosageMatcher.start())
                    + namePart.substring(dosageMatcher.end())).trim();
        }

        // 4. Extraire la forme galénique du namePart restant
        String forme = extractForme(namePart);
        if (forme != null) {
            namePart = namePart.replaceFirst("(?i)\\b" + Pattern.quote(forme) + "\\b", "").trim();
        }

        // 5. Le reste est le nom commercial
        String nom = normalizeName(namePart);

        if (!isValidName(nom)) return null;

        pt.nomCommerciale = nom;
        pt.dosage         = dosage;
        pt.forme          = forme;
        pt.posologie      = posoPart != null ? posoPart : null;
        pt.duree          = null; // non disponible dans ce format
        return pt;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  FORMAT B : alternance "nom dosage" / "posologie" sans tiret
    //  Exemple :
    //    amlor 5mg        ← ligne médicament
    //    1 gel./j         ← ligne posologie
    //    lopril 50mg
    //    1 cp/j
    // ════════════════════════════════════════════════════════════════════════

    private static List<ParsedTraitement> parseFormatB(String text) {
        List<ParsedTraitement> results = new ArrayList<>();

        // Filtrer les lignes vides
        List<String> lines = text.lines()
                .map(String::trim)
                .filter(l -> !l.isBlank())
                .toList();

        int i = 0;
        while (i < lines.size()) {
            String line = lines.get(i);

            // Classifier la ligne : médicament ou posologie ?
            if (isMedicamentLine(line)) {
                ParsedTraitement pt = parseMedicamentLine(line);

                // La ligne suivante est-elle une posologie ?
                if (i + 1 < lines.size() && isPosologieLine(lines.get(i + 1))) {
                    pt.posologie = lines.get(i + 1);
                    i += 2;
                } else {
                    i++;
                }

                if (isValidName(pt.nomCommerciale)) {
                    results.add(pt);
                }
            } else {
                // Ligne de posologie orpheline ou ligne inconnue, on passe
                i++;
            }
        }
        return results;
    }

    /**
     * Détermine si une ligne décrit un médicament.
     * Heuristique : commence par une lettre et contient au moins 2 caractères.
     * Une ligne de posologie commence typiquement par un chiffre ("1 cp/j", "2 gel/j").
     */
    private static boolean isMedicamentLine(String line) {
        if (line.isBlank()) return false;
        char first = line.charAt(0);
        return Character.isLetter(first);
    }

    /**
     * Détermine si une ligne est une posologie.
     * Ex: "1 cp/j", "2 gel./j", "1 gel./j", "3 fois/j"
     */
    private static boolean isPosologieLine(String line) {
        if (line.isBlank()) return false;
        return Character.isDigit(line.charAt(0));
    }

    /**
     * Parse une ligne FORMAT B de type médicament.
     * Exemple : "amlor 5mg" → nom="AMLOR", dosage="5mg"
     *           "lopril 50mg" → nom="LOPRIL", dosage="50mg"
     */
    private static ParsedTraitement parseMedicamentLine(String line) {
        ParsedTraitement pt = new ParsedTraitement();

        // Extraire le dosage
        Matcher m = DOSAGE_PATTERN.matcher(line);
        String dosage = null;
        if (m.find()) {
            dosage = m.group(1).trim();
            line = (line.substring(0, m.start()) + line.substring(m.end())).trim();
        }

        // Extraire la forme galénique
        String forme = extractForme(line);
        if (forme != null) {
            line = line.replaceFirst("(?i)\\b" + Pattern.quote(forme) + "\\b", "").trim();
        }

        pt.nomCommerciale = normalizeName(line);
        pt.dosage         = dosage;
        pt.forme          = forme;
        pt.posologie      = null; // sera remplie par l'appelant
        pt.duree          = null;
        return pt;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Utilitaires communs
    // ════════════════════════════════════════════════════════════════════════

    /** Cherche une forme galénique dans la chaîne (insensible à la casse). */
    private static String extractForme(String text) {
        for (String forme : FORMES) {
            Pattern p = Pattern.compile("\\b" + Pattern.quote(forme) + "\\b",
                    Pattern.CASE_INSENSITIVE);
            if (p.matcher(text).find()) return forme.toUpperCase();
        }
        return null;
    }

    /** Normalise le nom : trim + majuscules + suppression des caractères parasites. */
    private static String normalizeName(String raw) {
        if (raw == null) return null;
        return raw.trim()
                .replaceAll("[\\./,;:]+$", "")  // ponctuation finale
                .replaceAll("\\s+", " ")         // espaces multiples
                .trim()
                .toUpperCase();
    }

    /** Valide qu'un nom commercial est exploitable. */
    private static boolean isValidName(String nom) {
        return nom != null && nom.length() >= 2 && !nom.isBlank();
    }
}