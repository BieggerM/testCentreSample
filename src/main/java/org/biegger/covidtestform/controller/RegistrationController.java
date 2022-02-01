package org.biegger.covidtestform.controller;

import javassist.NotFoundException;
import org.biegger.covidtestform.Data.Registration;
import org.biegger.covidtestform.service.RegistrationService.RegistrationService;
import org.biegger.covidtestform.service.PdfService.PdfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RegistrationController {

    private RegistrationService registrationService;
    private PdfService pdfService;
    final static Logger log = LoggerFactory.getLogger(RegistrationController.class);

    @Autowired
    public RegistrationController(RegistrationService registrationService, PdfService pdfService) {
        this.registrationService = registrationService;
        this.pdfService = pdfService;
    }

    @RequestMapping(value = "registerUser")
    public String registerUser(@ModelAttribute("registrationDto") Registration registration){
        try{
            registrationService.saveRegistration(registration);
            log.debug("Registration of user " + registration.getFirstName() + " successfull");
        } catch (NotFoundException e) {
            return "error-500";
        }
        return "success";
    }

    @GetMapping(value = "/admin/delete/{registrationId}")
    public String deleteEntry(@PathVariable(value = "registrationId") String registrationId, Model model){
        registrationService.deleteRegistrationById(Integer.parseInt(registrationId));
        model.addAttribute("registrations", registrationService.getAllRegistrations());
        return "administration";
    }

    @RequestMapping(value = "/admin/positive/{registrationId}")
    public String testPositive(@PathVariable(value = "registrationId") String registrationId, Model model){
        try {
            registrationService.generateAndSendEmail(registrationId, "Positiv");
        } catch (Exception e){
            e.printStackTrace();
            model.addAttribute("registrations", registrationService.getAllRegistrations());
            return "administration";
        }
        model.addAttribute("registrations", registrationService.getAllRegistrations());
        return "administration";
    }

    @RequestMapping(value = "/admin/negative/{registrationId}")
    public String testNegative(@PathVariable(value = "registrationId") String registrationId, Model model){
        try {
            registrationService.generateAndSendEmail(registrationId, "Negativ");
        } catch (Exception e){
            e.printStackTrace();
            model.addAttribute("registrations", registrationService.getAllRegistrations());
            return "administration";
        }
        model.addAttribute("registrations", registrationService.getAllRegistrations());
        return "administration";
    }

    @RequestMapping(value = "/admin/print/neg/{registrationId}")
    public ResponseEntity<Resource> printNegative(@PathVariable(value = "registrationId") String registrationId, Model model){
        ByteArrayResource resource = null;
        Registration registration = null;
        try {
            model.addAttribute("registrations", registrationService.getAllRegistrations());
            registration = registrationService.getRegistrationById(registrationId);
            resource = pdfService.printPdf(registration, "Negativ");
        }catch (Exception e){
            e.printStackTrace();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", registration.getSurName()));
        headers.add("Cache-Control", "no-cache, no-store, must revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .headers(headers)
                .body(resource);
    }

    @RequestMapping(value = "/admin/print/pos/{registrationId}")
    public ResponseEntity<Resource> printPositive(@PathVariable(value = "registrationId") String registrationId, Model model){
        ByteArrayResource resource = null;
        Registration registration = null;
        try {
            model.addAttribute("registrations", registrationService.getAllRegistrations());
            registration = registrationService.getRegistrationById(registrationId);
            resource = pdfService.printPdf(registration, "Positiv");
        }catch (Exception e){
            e.printStackTrace();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", registration.getSurName()));
        headers.add("Cache-Control", "no-cache, no-store, must revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .headers(headers)
                .body(resource);
    }
}
