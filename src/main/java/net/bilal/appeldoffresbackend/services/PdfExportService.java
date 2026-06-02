package net.bilal.appeldoffresbackend.services;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.RequiredArgsConstructor;

import net.bilal.appeldoffresbackend.entities.AppelDoffres;
import net.bilal.appeldoffresbackend.repositories.AppelDoffresRepository;

import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class PdfExportService {

    private final AppelDoffresRepository appelDoffresRepository;

    public ByteArrayInputStream exportAOPdf(Long id) {

        try {

            AppelDoffres ao =
                    appelDoffresRepository
                            .findById(id)
                            .orElseThrow();

            Document document =
                    new Document();

            ByteArrayOutputStream out =
                    new ByteArrayOutputStream();

            PdfWriter.getInstance(
                    document,
                    out
            );

            document.open();

            document.add(
                    new Paragraph(
                            "FICHE APPEL D'OFFRES"
                    )
            );

            document.add(
                    new Paragraph(" ")
            );

            document.add(
                    new Paragraph(
                            "Reference : "
                                    + ao.getReference()
                    )
            );

            document.add(
                    new Paragraph(
                            "Objet : "
                                    + ao.getObjet()
                    )
            );

            document.add(
                    new Paragraph(
                            "Montant estime : "
                                    + ao.getMontantEstime()
                    )
            );

            document.add(
                    new Paragraph(
                            "Statut : "
                                    + ao.getStatut()
                    )
            );

            if (ao.getClient() != null) {

                document.add(
                        new Paragraph(
                                "Client : "
                                        + ao.getClient()
                                        .getRaisonSociale()
                        )
                );
            }

            document.close();

            return new ByteArrayInputStream(
                    out.toByteArray()
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Erreur PDF",
                    e
            );
        }
    }
}