package net.bilal.appeldoffresbackend.web;

import lombok.RequiredArgsConstructor;
import net.bilal.appeldoffresbackend.entities.AppelDoffres;
import net.bilal.appeldoffresbackend.repositories.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import net.bilal.appeldoffresbackend.dtos.DashboardStatsDTO;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin("*")
public class DashboardController {

    private final ClientRepository clientRepository;
    private final AppelDoffresRepository appelDoffresRepository;
    private final ConsultationRepository consultationRepository;
    private final MarcheRepository marcheRepository;
    private final CommandeRepository commandeRepository;
    private final PaiementRepository paiementRepository;

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public DashboardStatsDTO getDashboardStats() {

        long totalClients = clientRepository.count();
        long totalAppelsOffres = appelDoffresRepository.count();
        long totalConsultations = consultationRepository.count();
        long totalMarches = marcheRepository.count();
        long totalCommandes = commandeRepository.count();
        long totalPaiements = paiementRepository.count();

        double chiffreAffaireTotal =
                paiementRepository.getTotalChiffreAffaire();

        long totalAO = appelDoffresRepository.count();

        long aoAdjuges =
                appelDoffresRepository.countByStatut("ADJUGE");

        double tauxReussite = 0;

        if (totalAO > 0) {
            tauxReussite =
                    ((double) aoAdjuges / totalAO) * 100;
        }

        tauxReussite =
                Math.round(tauxReussite * 100.0) / 100.0;

        long aoEnCours =
                appelDoffresRepository.countByStatut("EN_COURS");

        long aoAnnules =
                appelDoffresRepository.countByStatut("ANNULE");

        double montantTotalAO =
                appelDoffresRepository.findAll()
                        .stream()
                        .filter(ao -> ao.getMontantEstime() != null)
                        .mapToDouble(AppelDoffres::getMontantEstime)
                        .sum();

        String topClient =
                appelDoffresRepository.findAll()
                        .stream()
                        .filter(ao -> ao.getClient() != null)
                        .collect(
                                java.util.stream.Collectors.groupingBy(
                                        ao -> ao.getClient().getRaisonSociale(),
                                        java.util.stream.Collectors.counting()
                                )
                        )
                        .entrySet()
                        .stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse("Aucun client");

        return new DashboardStatsDTO(
                totalClients,
                totalAppelsOffres,
                totalConsultations,
                totalMarches,
                totalCommandes,
                totalPaiements,
                chiffreAffaireTotal,
                aoAdjuges,
                tauxReussite,
                aoEnCours,
                aoAnnules,
                montantTotalAO,
                topClient
        );
    }

    @GetMapping("/alertes/appels-offres")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public List<AppelDoffres> getAOUrgents() {

        return appelDoffresRepository
                .getAppelsOffresUrgents(LocalDate.now().plusDays(7));
    }
}
