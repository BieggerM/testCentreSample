package org.biegger.covidtestform.service.FallbackDataService;

import org.biegger.covidtestform.Data.FallbackRegistration;
import org.biegger.covidtestform.Data.Registration;

import java.util.List;

public interface FallbackDataService {

    public List<FallbackRegistration> getAllFallbackRegistrations();

    public void saveRegistrationToFallback(FallbackRegistration transaction);

    public void saveRegistrationToFallback(Registration registration, String result);

    public void deleteAll();
}
