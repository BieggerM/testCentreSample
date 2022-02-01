package org.biegger.covidtestform.service.EmailService;

public interface EmailService {

    public void sendEMail(String toEmail, byte[] outputStream, String result);
}
