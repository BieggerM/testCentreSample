package org.biegger.covidtestform.serviceTest;
import javassist.NotFoundException;
import org.biegger.covidtestform.Data.Registration;
import org.biegger.covidtestform.Data.RegistrationRepo;
import org.biegger.covidtestform.service.PdfService.PdfService;
import org.biegger.covidtestform.service.RegistrationService.RegistrationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
@AutoConfigureMockMvc
public class GeneralServiceTests {


    @Autowired
    private PdfService pdfService;
    @Autowired
    private RegistrationService registrationService;
    @MockBean
    private RegistrationRepo registrationRepo;

    List<Registration> list = new ArrayList<>();

    public GeneralServiceTests() {
    }

    @Before
    public void setUp() {

        generateMockRegistrations();
        Mockito.when(registrationRepo.findAll())
                .thenReturn(list);

    }

    @Test
    public void emptyRegistrationShouldResultInErrorThrown() {
        assertThrows(NotFoundException.class, () -> registrationService.saveRegistration(new Registration()));
    }

    @Test
    public void shouldSortRegistrationsById(){
        List<Registration> result = registrationService.getAllRegistrations();
        ArrayList<Registration> sorted = new ArrayList<Registration>(result);
        Collections.sort(sorted);
        Assert.assertEquals ("List is not sorted", sorted, result);
    }

    @Test
    public void PdfByteArrayIsBeingReturned(){
        assertTrue(pdfService.printPdf(list.get(0), "positiv") instanceof ByteArrayResource);
    }

    @Test
    public void saveIsSuccessfull() {
        Mockito.when(registrationRepo.save(list.get(0))).thenReturn(list.get(0));
        assertDoesNotThrow(()->registrationService.saveRegistration(list.get(2)));
    }

    private void generateMockRegistrations() {
        Registration one = new Registration();
        one.setFirstName("Alex");
        one.setSurName("Axelkopf");
        one.seteMail("test@test.de");
        one.setBirthDate("12.12.12");
        one.setZipCode(88255);
        one.setPlace("Bernheim");
        one.setHouseNumber(65);
        one.setStreet("Gehstraße");
        one.setId(2);

        Registration two = new Registration();
        two.setFirstName("Hannes");
        two.setId(3);

        Registration three = new Registration();
        three.setFirstName("Kim");
        three.setId(4);

        Registration four = new Registration();
        four.setFirstName("Sergej");
        four.setId(1);

        list.add(one);
        list.add(two);
        list.add(three);
        list.add(four);
    }

}
