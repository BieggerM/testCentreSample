package org.biegger.covidtestform.service.RegistrationService;

import javassist.NotFoundException;
import org.biegger.covidtestform.Data.Registration;
import org.biegger.covidtestform.Data.RegistrationRepo;
import org.biegger.covidtestform.service.EmailService.EmailService;
import org.biegger.covidtestform.service.FallbackDataService.FallbackDataService;
import org.biegger.covidtestform.service.PdfService.PdfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    private RegistrationRepo repo;
    private PdfService pdfService;
    private EmailService emailService;
    private FallbackDataService fallbackDataService;
    private static Logger log = LoggerFactory.getLogger(RegistrationServiceImpl.class);

    @Autowired
    public RegistrationServiceImpl(RegistrationRepo repo, PdfService pdfService, EmailService emailService, FallbackDataService fallbackDataService) {
        this.repo = repo;
        this.pdfService = pdfService;
        this.emailService = emailService;
        this.fallbackDataService = fallbackDataService;
    }

    @Override
    public void saveRegistration(Registration registration) throws NotFoundException {
        if(StringUtils.isEmpty(registration.getFirstName())){
            throw new NotFoundException("Dto is empty");
        }
        repo.save(registration);
    }

    @Override
    public List<Registration> getAllRegistrations() {
        Iterable<Registration> registrations = repo.findAll();
        List<Registration> result = StreamSupport.stream(registrations.spliterator(), false)
                        .collect(Collectors.toList());
        return sortRegistrationsById(result);
    }

    @Override
    public List<Registration> sortRegistrationsById(List<Registration> result) {
        Comparator<Registration> comparator = new Comparator<Registration>() {
            @Override
            public int compare(Registration left, Registration right) {
                return left.getId() - right.getId(); // use your logic
            }
        };
        Collections.sort(result, comparator);
        return result;
    }

    @Override
    public boolean deleteRegistrationById(Integer id) {
        if(repo.existsById(id)) {
            repo.deleteById(id);
        }
        return true;
    }

    @Override
    public Registration getRegistrationById(String registrationId) throws NotFoundException {
        Optional<Registration> result = null;
        int id = Integer.parseInt(registrationId);
        if(repo.existsById(id)) {
            result = repo.findById(id);
        }
        if(result == null){
            NotFoundException e = new NotFoundException("entity with id " + id + " not found");
            throw e;
        }
        return result.get();
    }

    @Override
    public void generateAndSendEmail(String registrationId, String result) throws NotFoundException {
        Registration currentReg = getRegistrationById(registrationId);
        fallbackDataService.saveRegistrationToFallback(currentReg, result);
        ByteArrayResource byteArrayResource = pdfService.printPdf(currentReg, result);
        log.info("Created PDF for " + currentReg.getSurName());
        emailService.sendEMail(currentReg.geteMail(), byteArrayResource.getByteArray(), result);
        log.info("Email was sent succesfully");
        this.deleteRegistrationById(Integer.parseInt(registrationId));
    }

}
