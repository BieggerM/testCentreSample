package org.biegger.covidtestform.Data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationRepo extends CrudRepository<Registration, Integer> {


}
