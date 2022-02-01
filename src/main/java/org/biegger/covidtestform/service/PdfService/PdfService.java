package org.biegger.covidtestform.service.PdfService;

import org.biegger.covidtestform.Data.Registration;
import org.springframework.core.io.ByteArrayResource;

public interface PdfService {

    ByteArrayResource printPdf(Registration currentReg, String result);
}
