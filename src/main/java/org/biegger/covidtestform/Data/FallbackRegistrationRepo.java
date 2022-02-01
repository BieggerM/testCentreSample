package org.biegger.covidtestform.Data;

import org.springframework.data.repository.CrudRepository;

public interface FallbackRegistrationRepo extends CrudRepository<FallbackRegistration, Integer> {
}
