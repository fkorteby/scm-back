package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.Dto.adminDash.*;
import com.simple_cabinet_medical.Backend.repository.ClientRepository;
import com.simple_cabinet_medical.Backend.repository.ConsultationRepository;
import com.simple_cabinet_medical.Backend.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class AdminDashboardService {

    // Le client id=1 est le compte interne / admin : on l'exclut de toutes
    // les statistiques exposées au super-admin pour ne pas fausser les chiffres.
    private static final Long ADMIN_CLIENT_ID = 1L;

    private final ClientRepository clientRepository;
    private final PatientRepository patientRepository;
    private final ConsultationRepository consultationRepository;

    public AdminDashboardService(ClientRepository clientRepository, PatientRepository patientRepository, ConsultationRepository consultationRepository) {
        this.clientRepository = clientRepository;
        this.patientRepository = patientRepository;
        this.consultationRepository = consultationRepository;
    }

    public DoctorKpiDto getDoctorsKpi() {
        // Nombre total de médecins inscrits (hors compte admin)
        long totalDoctors = clientRepository.countByIdClientNot(ADMIN_CLIENT_ID);

        // Nouveaux comptes créés cette semaine (depuis 7 jours)
        LocalDate localDateWeekAgo = LocalDate.now().minusDays(7);
        Date dateWeekAgo = Date.from(localDateWeekAgo.atStartOfDay(ZoneId.systemDefault()).toInstant());
        long newDoctorsThisWeek = clientRepository.countByDateCreationAfterAndIdClientNot(dateWeekAgo, ADMIN_CLIENT_ID);

        // Comptes actifs : ont créé au moins une consultation ces 7 derniers jours
        long activeDoctors = clientRepository.countActiveDoctorsSince(localDateWeekAgo, ADMIN_CLIENT_ID);

        // Comptes inactifs : total - actifs
        long inactiveDoctors = totalDoctors - activeDoctors;
        if (inactiveDoctors < 0) {
            inactiveDoctors = 0; // Sécurité au cas où
        }

        return new DoctorKpiDto(totalDoctors, newDoctorsThisWeek, activeDoctors, inactiveDoctors);
    }

    public PatientKpiDto getPatientsKpi() {
        // 1. Nombre total de dossiers patients (hors patients rattachés au compte admin)
        long totalPatients = patientRepository.countByClient_IdClientNot(ADMIN_CLIENT_ID);

        // 2. Nouveaux dossiers créés aujourd'hui
        LocalDate today = LocalDate.now();
        Date startOfToday = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        long newPatientsToday = patientRepository.countByDateCreationAfterAndClient_IdClientNot(startOfToday, ADMIN_CLIENT_ID);

        // 3. Calcul du taux de croissance (Exemple : Comparaison de ce mois-ci vs le mois dernier)
        LocalDate startOfThisMonth = today.withDayOfMonth(1);
        LocalDate startOfLastMonth = startOfThisMonth.minusMonths(1);

        Date dateThisMonth = Date.from(startOfThisMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date dateLastMonth = Date.from(startOfLastMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());

        long patientsThisMonth = patientRepository.countByDateCreationAfterAndClient_IdClientNot(dateThisMonth, ADMIN_CLIENT_ID);
        long patientsLastMonth = patientRepository.countByDateCreationAfterAndClient_IdClientNot(dateLastMonth, ADMIN_CLIENT_ID) - patientsThisMonth;

        double growthPercentage = 0.0;
        if (patientsLastMonth > 0) {
            growthPercentage = ((double) (patientsThisMonth - patientsLastMonth) / patientsLastMonth) * 100.0;
        } else if (patientsThisMonth > 0) {
            growthPercentage = 100.0; // Si le mois dernier il y avait 0 et ce mois-ci plus
        }

        // Arrondir à 1 chiffre après la virgule (ex: 12.4)
        growthPercentage = Math.round(growthPercentage * 10.0) / 10.0;

        return new PatientKpiDto(
                totalPatients,
                "Dossiers patients",
                growthPercentage,
                newPatientsToday
        );
    }

    public ConsultationCardDto getConsultationCard() {
        // 1. Total global des consultations (hors compte admin)
        long totalConsultations = consultationRepository.countByClient_IdClientNot(ADMIN_CLIENT_ID);

        // 2. Consultations créées aujourd'hui
        LocalDate today = LocalDate.now();
        long consultationsToday = consultationRepository.countByDateConsultationAndClient_IdClientNot(today, ADMIN_CLIENT_ID);

        return new ConsultationCardDto(
                totalConsultations,
                "Consultations totales",
                consultationsToday
        );
    }

    public DoctorEvolutionDto getDoctorEvolution() {
        int currentYear = LocalDate.now().getYear();

        // Récupérer toutes les dates de création (hors compte admin)
        List<Date> allDates = clientRepository.findAllCreationDates(ADMIN_CLIENT_ID);

        // Filtrer l'année en Java et compter par mois (1 à 12)
        Map<Integer, Long> monthlyCounts = new HashMap<>();
        for (Date date : allDates) {
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (localDate.getYear() == currentYear) {
                int month = localDate.getMonthValue();
                monthlyCounts.put(month, monthlyCounts.getOrDefault(month, 0L) + 1);
            }
        }

        // Libellés des mois en français
        String[] monthNames = {"Jan", "Fév", "Mar", "Avr", "Mai", "Juin", "Juil", "Août", "Sep", "Oct", "Nov", "Déc"};

        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();

        for (int i = 1; i <= 12; i++) {
            labels.add(monthNames[i - 1]);
            data.add(monthlyCounts.getOrDefault(i, 0L));
        }

        return new DoctorEvolutionDto(
                "Évolution des inscriptions",
                "Nouveaux médecins inscrits par mois",
                labels,
                data
        );
    }

    public GeoKpiResponseDto getGeoStatistics() {
        List<Object[]> rawData = clientRepository.getGeoStatistics(ADMIN_CLIENT_ID);

        // 1. Calculer le nombre total global de médecins pour faire le pourcentage
        long totalDoctorsAllCities = 0;
        for (Object[] row : rawData) {
            long docCount = ((Number) row[2]).longValue();
            totalDoctorsAllCities += docCount;
        }

        // 2. Construire la liste des DTOs
        List<GeoStatDto> stats = new ArrayList<>();
        for (Object[] row : rawData) {
            String city = (String) row[0];
            String country = (String) row[1];
            long doctorCount = ((Number) row[2]).longValue();

            // Gestion du cas où le cabinet (locals) est vide (SUM peut retourner null)
            long cabinetCount = row[3] != null ? ((Number) row[3]).longValue() : 0L;

            // Calcul du pourcentage
            double percentage = 0.0;
            if (totalDoctorsAllCities > 0) {
                percentage = ((double) doctorCount / totalDoctorsAllCities) * 100.0;
            }
            percentage = Math.round(percentage * 10.0) / 10.0; // Arrondi à 1 chiffre après la virgule

            stats.add(new GeoStatDto(city, country, doctorCount, cabinetCount, percentage));
        }

        return new GeoKpiResponseDto(
                "Répartition géographique",
                "Carte interactive des médecins et cabinets par ville",
                stats
        );
    }
}