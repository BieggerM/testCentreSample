package org.biegger.covidtestform.service.RegistrationService;

import javassist.NotFoundException;
import org.biegger.covidtestform.Data.Registration;

import java.util.List;

public interface RegistrationService {

    public void saveRegistration(Registration registrationDto) throws NotFoundException;

    public List<Registration> getAllRegistrations();

    public boolean deleteRegistrationById(Integer id);

    public Registration getRegistrationById(String registrationId) throws NotFoundException;

    public void generateAndSendEmail(String registrationId, String result) throws NotFoundException;

    public List<Registration> sortRegistrationsById(List<Registration> result);

}
