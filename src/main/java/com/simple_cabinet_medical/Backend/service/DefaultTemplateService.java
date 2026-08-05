package com.simple_cabinet_medical.Backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simple_cabinet_medical.Backend.model.Client;
import com.simple_cabinet_medical.Backend.utils.Storage.AppProperties;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DefaultTemplateService {

    private final AppProperties appProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DefaultTemplateService(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    // 1. TEMPLATE DE BASE (CORPS + FOOTER AMÉLIORÉ)
    private static final String BASE_DOCUMENT_TEMPLATE = """
            {{HEADER_CONTENT}}
            
            <div style="text-align: center; margin: 10px 0 20px 0;">
                <h1 style="text-decoration: underline; margin: 0; color: #000;">{{TITRE_DOCUMENT}}</h2>
            </div>
            
            <table style="width: 100%; border-collapse: collapse; margin-bottom: 30px; margin-left: 5px; margin-right: 5px;">
                <tr>
                    <td style="width: 70%; padding: 4px 0; color:#000"><strong>Nom :</strong> {{PATIENT_NOM}}</td>
                    <td style="width: 30%; padding: 4px 0; color:#000"><strong>Date :</strong> {{DATE_CONSULTATION}}</td>
                </tr>
                <tr>
                    <td style="width: 70%; padding: 4px 0; color:#000"><strong>Prénom :</strong> {{PATIENT_PRENOM}}</td>
                    <td style="width: 30%; padding: 4px 0; color:#000"><strong>Âge :</strong> {{PATIENT_AGE}}</td>
                </tr>
                <tr>
                    <td style="width: 70%; padding: 4px 0; color:#000"><strong>Adresse :</strong> {{PATIENT_ADRESSE}}</td>
                    <td style="width: 30%; padding: 4px 0; color:#000"><strong>Sexe :</strong> {{PATIENT_SEXE}}</td>
                </tr>
            </table>
            
            <div style="margin: 0 5px;">
                {{CONTENU_DOCUMENT}}
            </div>
            
            <div style="position: fixed; bottom: 0; left: 0; width: 100%;height:60px ;background-color: #fff;">
                        <hr style="border: none; border-top: 1px solid #000; margin: 4px 10px;">
                        <table style="width: 100%; border-collapse: collapse; font-size: 12px;">
                            <tr>
                                <td style="width: 60%; padding: 5px 10px; color: #000;">
                                    Document généré par
                                    <strong>SCM – Simple Cabinet Médical</strong>
                                    Solution de gestion médicale professionnelle
                                </td>
                                <td style="width: 40%; padding: 5px 10px; text-align: right;color:#000000;">
                                    Imprimé le : {{CURRENT_DATE}}
                                </td>
                            </tr>
                        </table>
            </div>
            """;

    public String generateTemplateForClient(Client client) {
        String headerHtml = generateDefaultHeaderHtml(client);
        return BASE_DOCUMENT_TEMPLATE.replace("{{HEADER_CONTENT}}", headerHtml);
//                .replace("{{CURRENT_DATE}}", getCurrentDate());
    }

    /**
     * Régénère le template à partir d'une configuration JSON de header
     */
    public String regenerateTemplateFromJson(String headerConfigJson) {
        try {
            String headerHtml = convertJsonToHtml(headerConfigJson);
            return BASE_DOCUMENT_TEMPLATE.replace("{{HEADER_CONTENT}}", headerHtml);
//                    .replace("{{CURRENT_DATE}}", getCurrentDate());
        } catch (Exception e) {
            e.printStackTrace();
            return BASE_DOCUMENT_TEMPLATE.replace("{{HEADER_CONTENT}}", "<!-- Erreur de génération du header -->");
//                    .replace("{{CURRENT_DATE}}", getCurrentDate());
        }
    }

    public String regenerateTemplateFromHtml(String headerHtml) {
        return BASE_DOCUMENT_TEMPLATE.replace("{{HEADER_CONTENT}}", headerHtml);
    }


    private String convertJsonToHtml(String jsonConfig) throws Exception {
        Map<String, Object> config = objectMapper.readValue(jsonConfig, Map.class);

        StringBuilder html = new StringBuilder();

        // Récupérer les configurations
        Map<String, Object> logoConfig = (Map<String, Object>) config.get("logoConfig");
        Map<String, Object> textBlockConfig = (Map<String, Object>) config.get("textBlockConfig");
        List<Map<String, Object>> elements = (List<Map<String, Object>>) config.get("elements");

        // Déterminer la hauteur du header (par défaut 100px)
        int headerHeight = config.containsKey("headerHeight") ?
                (Integer) config.get("headerHeight") : 120;

        // --- HEADER CONTAINER ---
        html.append("<div class=\"print-header\" style=\"position: relative; width: auto; height: ")
                .append(headerHeight).append("px; overflow: hidden; margin: 0 5px; padding: 0;\">");

        // --- LOGO (Droite) ---
        if (logoConfig != null) {
            int logoWidth = logoConfig.containsKey("width") ? (Integer) logoConfig.get("width") : 90;
            int logoPosX = logoConfig.containsKey("posX") ?
                    ((Number) logoConfig.get("posX")).intValue() : 0;
            int logoPosY = logoConfig.containsKey("posY") ?
                    ((Number) logoConfig.get("posY")).intValue() : 10;
            String logoUrl = (String) logoConfig.get("url");

            html.append("<div style=\"position: absolute; top: ").append(logoPosY)
                    .append("px; right: ").append(logoPosX == 0 ? "0" : (550 - logoPosX - logoWidth))
                    .append("px; z-index: 20;\">");
            html.append("<img src=\"").append(logoUrl)
                    .append("\" style=\"width: ").append(logoWidth)
                    .append("px; height: auto; display: block;\">");
            html.append("</div>");
        }

        // --- BLOC TEXTE (Gauche) ---
        if (textBlockConfig != null && elements != null) {
            int textPosX = textBlockConfig.containsKey("posX") ?
                    ((Number) textBlockConfig.get("posX")).intValue() : 5;
            int textPosY = textBlockConfig.containsKey("posY") ?
                    ((Number) textBlockConfig.get("posY")).intValue() : 10;
            int textWidth = textBlockConfig.containsKey("width") ?
                    (Integer) textBlockConfig.get("width") : 350;

            html.append("<div style=\"position: absolute; top: ").append(textPosY)
                    .append("px; left: ").append(textPosX)
                    .append("px; width: ").append(textWidth)
                    .append("px; z-index: 10; display: flex; flex-direction: column; line-height: 1.25;\">");

            // Ajouter chaque élément visible
            for (Map<String, Object> element : elements) {
                if ((Boolean) element.get("visible")) {
                    String value = (String) element.get("value");
                    Map<String, Object> style = (Map<String, Object>) element.get("style");

                    int fontSize = (Integer) style.get("fontSize");
                    String color = (String) style.get("color");
                    String fontWeight = (String) style.get("fontWeight");
                    int offsetY = (Integer) style.get("offsetY");

                    html.append(createHtmlLine(value, fontSize, color, fontWeight, offsetY));
                }
            }

            html.append("</div>"); // Fin TextBlock
        }

        html.append("</div>"); // Fin Header Container

        return html.toString();
    }

    public String generateDefaultHeaderJson(Client client,String urlLogo) {
        try {
            Map<String, Object> config = new HashMap<>();

            // A. Logo (Droite)
            Map<String, Object> logo = new HashMap<>();
            logo.put("url", urlLogo);
            logo.put("width", 100);
            logo.put("posX", 430);
            logo.put("posY", 30);
            config.put("logoConfig", logo);

            // B. Bloc Texte (Gauche) - Ajusté pour être aligné à gauche
            Map<String, Object> textBlock = new HashMap<>();
            textBlock.put("posX", 5);
            textBlock.put("posY", 10);
            textBlock.put("width", 380);
            config.put("textBlockConfig", textBlock);

            // C. Éléments
            List<Map<String, Object>> elements = new ArrayList<>();

            elements.add(createElement("titre", "Nom Clinique / Médecin",
                    getValueOrEmpty(client.getNomClient()), 22, true, "#619ACF", "bold", 0));

            elements.add(createElement("specialite", "Spécialités",
                    "Consultation, " + getValueOrEmpty(client.getSpecialite()), 12,
                    true, "#000000", "normal", 4));

            elements.add(createElement("adresse", "Adresse",
                    getValueOrEmpty(client.getAdresse()), 12, true, "#000000", "normal", 2));

            String contactInfo = getValueOrEmpty(client.getTelephone());
            if (client.getEmail() != null && !client.getEmail().isEmpty()) {
                contactInfo += " / " + client.getEmail();
            }
            elements.add(createElement("contact", "Contact",
                    contactInfo, 12, true, "#000000", "normal", 2));

            config.put("elements", elements);
            config.put("headerHeight", 120);

            return objectMapper.writeValueAsString(config);
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }

    private String generateDefaultHeaderHtml(Client client) {
        String logoUrl = getLogoUrl(client);
        String nomClient = getValueOrEmpty(client.getNomClient());
        String specialite = "Consultation, " + getValueOrEmpty(client.getSpecialite());
        String adresse = getValueOrEmpty(client.getAdresse());
        String contact = getValueOrEmpty(client.getTelephone());
        if (client.getEmail() != null && !client.getEmail().isEmpty()) {
            contact += " / " + client.getEmail();
        }

        StringBuilder html = new StringBuilder();

        html.append("<div class=\"print-header\" style=\"position: relative; width: auto; height: 100px; overflow: hidden; margin: 0 5px; padding: 0;\">");

        html.append("<div style=\"position: absolute; top: 10px; right: 0px; z-index: 20;\">");
        html.append("<img src=\"").append(logoUrl).append("\" style=\"width: 90px; height: auto; display: block;\">");
        html.append("</div>");

        html.append("<div style=\"position: absolute; top: 10px; left: 5px; width: 350px; z-index: 10; display: flex; flex-direction: column; line-height: 1.25;\">");
        html.append(createHtmlLine(nomClient, 23, "#619ACF", "bold", 0));
        html.append(createHtmlLine(specialite, 12, "#000000", "normal", 3));
        html.append(createHtmlLine(adresse, 12, "#000000", "normal", 1));
        html.append(createHtmlLine(contact, 12, "#000000", "normal", 1));
        html.append("</div>");
        html.append("</div>");

        return html.toString();
    }

    private Map<String, Object> createElement(String id, String label, String value,
                                              int fontSize, boolean visible, String color, String weight, int offsetY) {
        Map<String, Object> el = new HashMap<>();
        el.put("id", id);
        el.put("label", label);
        el.put("value", value);
        el.put("visible", visible);

        Map<String, Object> style = new HashMap<>();
        style.put("fontSize", fontSize);
        style.put("color", color);
        style.put("fontWeight", weight);
        style.put("fontFamily", "Arial, sans-serif");
        style.put("textAlign", "left");
        style.put("offsetX", 0);
        style.put("offsetY", offsetY);
        el.put("style", style);

        return el;
    }

    private String createHtmlLine(String value, int fontSize, String color, String weight, int top) {
        return String.format(
                "<div style=\"position: relative; left: 0px; top: %dpx; font-size: %dpx; color: %s; font-weight: %s; font-family: Arial, sans-serif; text-align: left; margin-bottom: 0px; width: 100%%; white-space: pre-wrap;\">%s</div>",
                top, fontSize, color, weight, value
        );
    }

    private String getValueOrEmpty(String value) {
        return (value != null && !value.trim().isEmpty()) ? value : "";
    }

    private String getLogoUrl(Client client) {
        if (client.getImage() != null && !client.getImage().isBlank()) {
            return appProperties.getBaseUrl() + "/uploads/logos/" + client.getImage();
        }
        return appProperties.getBaseUrl() + "/uploads/logos/default.png";
    }

    private String getCurrentDate() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return now.format(formatter);
    }

    private String getCurrentDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return now.format(formatter);
    }

    public String getDefaultTemplate() {
        return BASE_DOCUMENT_TEMPLATE;
    }
}

