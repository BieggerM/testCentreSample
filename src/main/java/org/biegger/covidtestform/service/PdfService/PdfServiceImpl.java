package org.biegger.covidtestform.service.PdfService;

import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.biegger.covidtestform.Data.Registration;
import org.biegger.covidtestform.service.RegistrationService.RegistrationServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
public class PdfServiceImpl implements PdfService {

    private final String OLD_FORMAT = "yyyy-MM-dd";
    private final String NEW_FORMAT = "dd.MM.yyyy";
    final static Logger log = LoggerFactory.getLogger(RegistrationServiceImpl.class);

    @Override
    public ByteArrayResource printPdf(Registration registration, String result) {
        String name = registration.getFirstName() + " " + registration.getSurName();
        String address = registration.getStreet() + " " + registration.getHouseNumber() + ", " + registration.getZipCode() + " " + registration.getPlace();
        String birth = registration.getBirthDate();
        /* For safe handling of closing data streams*/
        InputStream resource = null;
        PDDocument pDDocument = null;
        ByteArrayOutputStream byteArrayOutputStream = null;

        try {
            String newDateString = convertToDateViaInstant(java.time.LocalDate.now(ZoneId.of("Europe/Berlin")).toString());
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
            String time = LocalTime.now(ZoneId.of("Europe/Berlin")).format(dtf);
            ClassLoader classLoader = getClass().getClassLoader();
            resource = classLoader.getResourceAsStream("template1.pdf");
            pDDocument = PDDocument.load(resource);
            generatePdfFile(result, name, birth, address, pDDocument, newDateString, time);
            byteArrayOutputStream = new ByteArrayOutputStream();
            pDDocument.save(byteArrayOutputStream);

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } finally {
            try {
                pDDocument.close();
                resource.close();
                byteArrayOutputStream.close();
            } catch (IOException | NullPointerException e ) {
                IOUtils.closeQuietly(pDDocument);
                IOUtils.closeQuietly(resource);
                IOUtils.closeQuietly(byteArrayOutputStream);
            }
        }
        return new ByteArrayResource(byteArrayOutputStream.toByteArray());
    }

    private void generatePdfFile(String result, String name, String birth, String address, PDDocument pDDocument, String newDateString, String time) throws IOException {
        PDAcroForm pDAcroForm = pDDocument.getDocumentCatalog().getAcroForm();
        PDField field = pDAcroForm.getField("Vorname, Nachname");
        field.setValue(name);
        field = pDAcroForm.getField("Geburtstdatum");
        field.setValue(birth);
        field = pDAcroForm.getField("Anschrift");
        field.setValue(address);
        field = pDAcroForm.getField("Testergebnis");
        field.setValue(result);
        field = pDAcroForm.getField("Datum");
        field.setValue(newDateString);
        field = pDAcroForm.getField("Uhrzeit");
        field.setValue(time);
        field = pDAcroForm.getField("Aktuelles Datum");
        field.setValue(newDateString);
        pDAcroForm.flatten();
    }

    private String convertToDateViaInstant(String oldDateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
        if(oldDateStringNotYetOK(oldDateString, sdf)) {
            Date d = sdf.parse(oldDateString);
            sdf.applyPattern(NEW_FORMAT);
            return sdf.format(d);
        }
        return oldDateString;
    }

    private boolean oldDateStringNotYetOK(String oldDateString, SimpleDateFormat sdf) {
        sdf.setLenient(false);
        try{
            sdf.parse(oldDateString);
        } catch (ParseException e) {
           return false;
        }
        return true;
    }
}
