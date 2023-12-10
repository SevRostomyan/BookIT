package com.bookit.bookit.service.faktura;

import com.bookit.bookit.entity.bokning.Bokning;
import com.bookit.bookit.entity.faktura.Faktura;
import com.bookit.bookit.entity.kund.Kund;
import com.bookit.bookit.enums.BookingStatus;
import com.bookit.bookit.repository.bokning.BokningRepository;
import com.bookit.bookit.repository.faktura.FakturaRepository;
import com.bookit.bookit.repository.kund.KundRepository;
import com.bookit.bookit.service.notifications.NotificationsService;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import org.antlr.v4.runtime.misc.LogManager;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class FakturaService {
    private static final Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
    private static final Font normalFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);



    private final BokningRepository bokningRepository;
    private final KundRepository kundRepository;
    private final FakturaRepository fakturaRepository;
    private final NotificationsService notificationsService;
    private final Environment env;

// Other necessary autowired repositories...

    public List<Faktura> generateInvoices(Integer kundId) {
        // Fetch customer and completed bookings
        Kund kund = kundRepository.findById(kundId).orElseThrow(() -> new RuntimeException("Kund not found"));
        List<Bokning> completedBookings = bokningRepository.findAllByKundIdAndBookingStatus(kundId, BookingStatus.COMPLETED);

        if (completedBookings.isEmpty()) {
            throw new RuntimeException("No completed bookings available for invoice generation");
        }

        List<Faktura> generatedInvoices = new ArrayList<>();

        for (Bokning booking : completedBookings) {
            String serviceType = booking.getTjänst().getStädningsAlternativ().toString();
            double pricePerService = Double.parseDouble(env.getProperty(serviceType));

            double priceExclVAT = pricePerService;
            double priceInkVAT = priceExclVAT * 1.25; // Including VAT

            Faktura faktura = new Faktura();
            faktura.setBokning(booking);
            faktura.setTjänst(booking.getTjänst());
            faktura.setTotaltBelopp(priceInkVAT);
            faktura.setPriceExclVAT(priceExclVAT);
            faktura.setInvoiceDate(getNearestWorkingDay(LocalDate.now()));


            // Set company and customer details
            faktura.setCompanyName("Städafint AB");
            faktura.setOrganisationalNumber("XXXXXX-XXXX");
            faktura.setCompanyAddress("X Gatan Y, 752 65, Uppsala");

            faktura.setCustomerFirstName(kund.getFirstname());
            faktura.setCustomerLastName(kund.getLastname());
            faktura.setCustomerEmail(kund.getEmail());

            // Update booking status to NOT_PAID
            booking.setBookingStatus(BookingStatus.NOT_PAID);
            bokningRepository.save(booking);

            // Save the invoice to the database if needed
            fakturaRepository.save(faktura);

            generatedInvoices.add(faktura);

            // Prepare and send invoice email
            prepareAndSendInvoiceEmail(faktura);
        }
        return generatedInvoices;
    }

    private LocalDate getNearestWorkingDay(LocalDate date) {
        // Assuming weekends are non-working days. Adjust as per your locale's holidays.
        while (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            date = date.plusDays(1);
        }
        return date;
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
                "Totalbelopp (inkl. moms): " + faktura.getTotaltBelopp() + " kr\n"+

                // Add more details as needed
                "\nTack för att du använder vår tjänst.";
    }


    private File generateInvoicePdf(Faktura faktura) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("invoice.pdf"));
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
            return new File("invoice.pdf");
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Handle exception appropriately
        }
    }


}
