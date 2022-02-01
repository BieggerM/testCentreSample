package org.biegger.covidtestform.service.FallbackDataService;

import org.biegger.covidtestform.Data.FallbackRegistration;
import org.biegger.covidtestform.Data.FallbackRegistrationRepo;
import org.biegger.covidtestform.Data.Registration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class FallbackDataServiceImpl implements FallbackDataService {

    private FallbackRegistrationRepo repo;

    @Autowired
    public FallbackDataServiceImpl(FallbackRegistrationRepo repo) {
        this.repo = repo;
    }

    @Override
    public List<FallbackRegistration> getAllFallbackRegistrations(){
        Iterable<FallbackRegistration> registrations = repo.findAll();
        List<FallbackRegistration> result = StreamSupport.stream(registrations.spliterator(), false)
                .collect(Collectors.toList());
        return result;
    }

    @Override
    public void saveRegistrationToFallback(FallbackRegistration transaction) {
        repo.save(transaction);
    }

    @Override
    public void saveRegistrationToFallback(Registration registration, String result) {
        FallbackRegistration transaction = new FallbackRegistration();
        transaction.seteMail(registration.geteMail());
        String birth = registration.getBirthDate();
        transaction.setBirthDate(birth);
        transaction.setFirstName(registration.getFirstName());
        transaction.setSurName(registration.getSurName());
        transaction.setStreet(registration.getStreet());
        transaction.setHouseNumber(registration.getHouseNumber());
        transaction.setZipCode(registration.getZipCode());
        transaction.setPlace(registration.getPlace());
        transaction.setSaveTime(LocalDateTime.now(ZoneId.of("Europe/Berlin")).toString());
        transaction.setResult(result);
        transaction.setPhone(registration.getPhone());
        saveRegistrationToFallback(transaction);
    }
    @Override
    public void deleteAll(){
        repo.deleteAll();
    }
}
