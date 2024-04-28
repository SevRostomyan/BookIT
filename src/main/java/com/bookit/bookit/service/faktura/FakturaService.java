package com.bookit.bookit.service.faktura;

import com.bookit.bookit.dto.InvoiceGenerationResult;
import com.bookit.bookit.dto.InvoiceResponsDTO;
import com.bookit.bookit.entity.bokning.Bokning;
import com.bookit.bookit.entity.faktura.Faktura;
import com.bookit.bookit.entity.kund.Kund;
import com.bookit.bookit.enums.BookingStatus;
import com.bookit.bookit.enums.StädningsAlternativ;
import com.bookit.bookit.repository.bokning.BokningRepository;
import com.bookit.bookit.repository.faktura.FakturaRepository;
import com.bookit.bookit.repository.kund.KundRepository;
import com.bookit.bookit.service.notifications.NotificationsService;
import com.bookit.bookit.utils.CleaningServiceUtils;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import org.apache.log4j.Logger;

import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@EnableJpaRepositories("com.bookit.bookit.repository.faktura")
public class FakturaService {
    private static final Logger logger = Logger.getLogger(FakturaService.class);

    private final BokningRepository bokningRepository;
    private final KundRepository kundRepository;
    private final FakturaRepository fakturaRepository;
    private final NotificationsService notificationsService;
    private final Environment env;

    private static final Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
    private static final Font normalFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);

// Other necessary autowired repositories...


    public InvoiceGenerationResult generateInvoices(Integer kundId) {
        try {
            // Fetch customer and completed bookings
            Kund kund = kundRepository.findById(kundId)
                    .orElseThrow(() -> new RuntimeException("Kund not found"));
            List<Bokning> completedBookings = bokningRepository.findAllByKundIdAndBookingStatus(kundId, BookingStatus.COMPLETED);

            if (completedBookings.isEmpty()) {
                return new InvoiceGenerationResult(false, "No completed bookings available for invoice generation");
            }

            for (Bokning booking : completedBookings) {
                StädningsAlternativ alternativ = booking.getTjänst().getStädningsAlternativ();
                String serviceType = CleaningServiceUtils.getServiceTypeKey(alternativ);
                double pricePerServiceExclVAT = Double.parseDouble(Objects.requireNonNull(env.getProperty(serviceType)));
                double priceInkVAT = pricePerServiceExclVAT * 1.25; // Including VAT

                Faktura faktura = new Faktura();
                faktura.setKund(booking.getKund());
                faktura.setBokning(booking);
                faktura.setTjänst(booking.getTjänst());
                faktura.setTotaltBelopp(priceInkVAT);
                faktura.setPriceExclVAT(pricePerServiceExclVAT);
                faktura.setInvoiceDate(getNearestWorkingDay(LocalDate.now()));
                faktura.setFakturanummer(getNextInvoiceNumber());

                // Calculate due date
                LocalDate förfallodatum = getNearestWorkingDay(LocalDate.now()).plusDays(30); // 30 days payment term
                faktura.setFörfallodatum(förfallodatum.toString());

                // Set company and customer details
                faktura.setCompanyName("Städafint AB");
                faktura.setOrganisationalNumber("XXXXXX-XXXX");
                faktura.setCompanyAddress("X Gatan Y, 752 65, Uppsala");
                faktura.setCustomerFirstName(kund.getFirstname());
                faktura.setCustomerLastName(kund.getLastname());
                faktura.setCustomerEmail(kund.getEmail());

                try {
                    File invoicePdfFile = generateInvoicePdf(faktura);
                    if (invoicePdfFile != null) {
                        faktura.setInvoiceFilePath(invoicePdfFile.getAbsolutePath());
                    } else {
                        logger.error("PDF generation failed for booking ID: " + booking.getId());
                    }
                } catch (Exception e) {
                    logger.error("An error occurred during PDF generation for booking ID: " + booking.getId(), e);
                }

                fakturaRepository.save(faktura);
                prepareAndSendInvoiceEmail(faktura);
                booking.setBookingStatus(BookingStatus.NOT_PAID);
                bokningRepository.save(booking);
            }
            return new InvoiceGenerationResult(true, "Invoices generated and sent successfully.");
        } catch (Exception e) {
            return new InvoiceGenerationResult(false, "An unexpected error occurred: " + e.getMessage());
        }
    }

    //Hjälpmetoder till generateInvoice metoden:
    private LocalDate getNearestWorkingDay(LocalDate date) {
        // Assuming weekends are non-working days. Adjust as per your locale's holidays.
        while (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            date = date.plusDays(1);
        }
        return date;
    }

    public synchronized String getNextInvoiceNumber() {
        // Fetch the last fakturanummer and increment it
        Faktura lastFaktura = fakturaRepository.findTopByOrderByFakturanummerDesc();
        long nextNumber = 1; // Start from 1 if no invoices are present
        if (lastFaktura != null) {
            String lastNumber = lastFaktura.getFakturanummer();
            nextNumber = Long.parseLong(lastNumber) + 1;
        }
        return String.format("%06d", nextNumber); // Formats the number with leading zeros
    }


    //Method to Prepare and Send Invoice Email
    private void prepareAndSendInvoiceEmail(Faktura faktura) {
        String email = faktura.getCustomerEmail();
        String subject = "Din faktura från StädaFint AB";
        String body = createInvoiceEmailBody(faktura);

        // Generate PDF (discussed later) and attach to email
        File invoicePdf = generateInvoicePdf(faktura);

        // Send the email with PDF attachment
        notificationsService.sendEmailWithAttachment(email, subject, body, invoicePdf, faktura);
    }

    private String createInvoiceEmailBody(Faktura faktura) {
        // Example implementation
        return "Kära " + faktura.getCustomerFirstName() + ",\n\n" +
                "Här är din faktura för den städtjänst som tillhandahållits.\n" +
                "Fakturanummer: " + faktura.getId() + "\n" +
                "Totalbelopp (exkl. moms): " + faktura.getPriceExclVAT() + " kr\n" +
                "Totalbelopp (inkl. moms): " + faktura.getTotaltBelopp() + " kr\n" +
                "Förfallodatum: " + faktura.getFörfallodatum() + ".\n" +


                // Add more details as needed
                "\nTack för att du använder vår tjänst.";
    }

    private File generateInvoicePdf(Faktura faktura) {
        String directoryPath = "C:/Users/Vahe/Desktop/Java-Development - Ec utbildning/Kurs - Mjukvaruprojekt och webutveckling/BookItIInvoices";
        String fileName = "Invoice_" + faktura.getId() + ".pdf";
        String filePath = directoryPath + "/" + fileName;

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Invoice header
            Paragraph header = new Paragraph("Fakturadetaljer", headerFont);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);
            document.add(new Paragraph("---------------------------------------------------------", normalFont));

            // Company details
            document.add(new Paragraph("Företagsnamn: " + faktura.getCompanyName(), normalFont));
            document.add(new Paragraph("Organisationsnummer: " + faktura.getOrganisationalNumber(), normalFont));
            document.add(new Paragraph("Företagsadress: " + faktura.getCompanyAddress(), normalFont));
            document.add(new Paragraph("---------------------------------------------------------", normalFont));

            // Customer details
            document.add(new Paragraph("Kundens Namn: " + faktura.getCustomerFirstName() + " " + faktura.getCustomerLastName(), normalFont));
            document.add(new Paragraph("Kundens E-post: " + faktura.getCustomerEmail(), normalFont));
            document.add(new Paragraph("---------------------------------------------------------", normalFont));

            // Invoice specifics
            document.add(new Paragraph("Fakturanummer: " + faktura.getFakturanummer(), normalFont));
            document.add(new Paragraph("Fakturadatum: " + faktura.getInvoiceDate().toString(), normalFont));
            document.add(new Paragraph("Förfallodatum: " + faktura.getFörfallodatum(), normalFont));
            document.add(new Paragraph("---------------------------------------------------------", normalFont));

            // Booking and service details
            document.add(new Paragraph("Tjänsttyp: " + faktura.getTjänst().getStädningsAlternativ().toString(), normalFont));
            document.add(new Paragraph("Boknings-ID: " + faktura.getBokning().getId(), normalFont));
            document.add(new Paragraph("---------------------------------------------------------", normalFont));

            // Pricing
            document.add(new Paragraph("Pris (exkl. moms): " + faktura.getPriceExclVAT() + " SEK", normalFont));
            document.add(new Paragraph("Totalbelopp (inkl. moms): " + faktura.getTotaltBelopp() + " SEK", normalFont));
            document.add(new Paragraph("---------------------------------------------------------", normalFont));

            document.close();
            return new File(filePath);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Handle exception appropriately
        }
    }


    //Metod för att hämta de genererade fakturaobjekten till frontenden i form av en tabell. Tabellen ska innehålla även sökväg till
    // PDF filen som man kan ladda ner via downloadInvoice endpointen i FakturaController. Kan användas av både Admin och kund:

    public List<InvoiceResponsDTO> getInvoicesForCustomer(Integer kundId) {
        List<Faktura> invoices = fakturaRepository.findAllByKundId(kundId);
        List<InvoiceResponsDTO> invoiceDTOs = new ArrayList<>();

        for (Faktura invoice : invoices) {
            InvoiceResponsDTO dto = new InvoiceResponsDTO();
            dto.setId(invoice.getId());
            dto.setTotaltBelopp(invoice.getTotaltBelopp());
            dto.setInvoiceDate(invoice.getInvoiceDate());
            dto.setFörfallodatum(invoice.getFörfallodatum());

            if (invoice.getTjänst() != null) {
                dto.setTjänstTyp(invoice.getTjänst().getStädningsAlternativ().toString());
            }

            invoiceDTOs.add(dto);
        }

        return invoiceDTOs;
    }



}
