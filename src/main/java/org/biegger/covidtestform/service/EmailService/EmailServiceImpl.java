package org.biegger.covidtestform.service.EmailService;

import org.apache.pdfbox.io.IOUtils;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.email.EmailPopulatingBuilder;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.*;



@Service
public class EmailServiceImpl implements EmailService {

    private final static Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final String eMailTestzentrum = "schnelltest.donautal@gmail.com";

    @Value("${spring.mail.username}")
    private String from;
    @Value("${spring.mail.password}")
    private String password;
    @Value("${spring.mail.host}")
    private String host;
    @Value("${spring.mail.port}")
    private Integer port;

    private Mailer mailer;

    @Autowired
    public EmailServiceImpl() {
    }

    private void buildMailer() {
        mailer = MailerBuilder
                .withSMTPServer(host, port, from, password)
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .withSessionTimeout(10 * 1000)
                .withDebugLogging(false)
                .async()
                .buildMailer();
    }

    private Email buildMailToCustomer(String toEmail, byte[] certificatePdf, String subject, String plainText, byte[] infoPdfArray, byte[] dkim) throws AddressException {
        EmailPopulatingBuilder emailBuilder = EmailBuilder.startingBlank()
                .from(new InternetAddress(from))
                .to(new InternetAddress(toEmail))
                .withSubject(subject)
                .withHTMLText(text)
                .withPlainText(plainText)
                .withAttachment("ergebnis.pdf", certificatePdf, "application/pdf")
                .withAttachment("info_positives_ergebnis.pdf", infoPdfArray, "application/pdf")
                .withHeader("X-Priority", 3)
                .signWithDomainKey(dkim, "schnelltest-donautal.com", "foo");
        return emailBuilder.buildEmail();
    }

    @Override
    public void sendEMail(String toEmail, byte[] certificatePdf, String result) {
        String subject = "Ihr Testergebnis zum PoC-Antigentest auf SARS-CoV-2 im Testzentrum Donautal";
        String plainText = "Guten Tag, im Anhang finden Sie Ihr Antigen Schnelltestergebnis";
        InputStream dkimInputStream = null;
        InputStream infoPdfStream = null;
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            dkimInputStream = classLoader.getResourceAsStream("dkim.der");
            infoPdfStream =classLoader.getResourceAsStream("info_positives_ergebnis.pdf");
            byte[] infoPdfArray = infoPdfStream.readAllBytes();
            byte[] dkim = dkimInputStream.readAllBytes();
            buildMailer();
            if (StringUtils.equals(result, "Positiv")) {
                sendEmailToTestzentrum(certificatePdf, toEmail);
            }
            Email email = buildMailToCustomer(toEmail, certificatePdf, subject, plainText, infoPdfArray, dkim);
            mailer.sendMail(email);
            log.debug("message to " + toEmail + " was sent");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                dkimInputStream.close();
                infoPdfStream.close();
            } catch (IOException | NullPointerException e) {
                IOUtils.closeQuietly(dkimInputStream);
                IOUtils.closeQuietly(infoPdfStream);
            }
        }
    }


    private void sendEmailToTestzentrum(byte[] array, String toEmail) {
        InputStream dkimInputStream = null;
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            dkimInputStream = classLoader.getResourceAsStream("dkim.der");
            byte[] dkim = dkimInputStream.readAllBytes();
            EmailPopulatingBuilder emailBuilder = EmailBuilder.startingBlank()
                    .from(new InternetAddress(from))
                    .to(new InternetAddress(eMailTestzentrum))
                    .withSubject("Positives Ergebnis bei " + toEmail)
                    .withHTMLText("Positives Ergebnis bei " + toEmail)
                    .withPlainText("Positives Ergebnis bei " + toEmail)
                    .withAttachment("ergebnis.pdf", array, "application/pdf")
                    .withHeader("X-Priority", 5)
                    .signWithDomainKey(dkim, "schnelltest-donautal.com", "foo");

            Email email = emailBuilder.buildEmail();
            mailer.sendMail(email);
            log.info("message to " + "Testzentum" + " was sent");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                dkimInputStream.close();
            } catch (IOException e) {
                IOUtils.closeQuietly(dkimInputStream);
            }
        }
    }

    private final String text = "<html>" +
            "<p style=\"background: white none repeat scroll 0% 0%; vertical-align: baseline; line-height: 1;\"><span style=\"font-size: 14px;\"><span style=\"font-family: Helvetica;\"><span style=\"color: black; border: 1pt none windowtext; padding: 0cm;\">Sehr geehrte Damen und Herren,&nbsp;</span></span></span></p>\n" +
            "<p style=\"margin-bottom: 14pt; background: white none repeat scroll 0% 0%; vertical-align: baseline; line-height: 1;\"><span style=\"font-size: 14px;\"><span style=\"font-family: Helvetica;\"><span style=\"color: black;\">&nbsp;</span></span></span></p>\n" +
            "<p style=\"background: white none repeat scroll 0% 0%; vertical-align: baseline; line-height: 1;\"><span style=\"font-size: 14px;\"><span style=\"font-family: Helvetica;\"><span style=\"color: black; border: 1pt none windowtext; padding: 0cm;\">Sie erhalten im Anhang an diese E-Mail Ihr Testergebnis zum PoC-Antigentest auf SARS-CoV-2.</span><span style=\"color: black;\">&nbsp;</span></span></span></p>\n" +
            "<p style=\"background: white none repeat scroll 0% 0%; vertical-align: baseline; line-height: 1;\"><span style=\"font-size: 14px;\"><span style=\"font-family: Helvetica;\"><br></span></span></p>\n" +
            "<p style=\"background: white none repeat scroll 0% 0%; vertical-align: baseline; line-height: 1;\"><span style=\"font-size: 14px;\"><span style=\"font-family: Helvetica;\"><strong><span style=\"color: black; border: 1pt none windowtext; padding: 0cm;\">Bitte beachten !!!</span></strong></span></span></p>\n" +
            "<p style=\"background: white none repeat scroll 0% 0%; vertical-align: baseline; line-height: 1;\"><span style=\"font-size: 14px;\"><span style=\"font-family: Helvetica;\"><strong><span style=\"color: red; border: 1pt none windowtext; padding: 0cm;\">Falls Ihr Testergebnis positiv ist,</span></strong><strong><span style=\"color: red;\">&nbsp;<span style=\"border:none windowtext 1.0pt;padding:0cm;\">beachten Sie bitte das im Anhang eingef&uuml;gte Merkblatt.</span></span></strong></span></span></p>\n" +
            "<p style=\"background: white none repeat scroll 0% 0%; vertical-align: baseline; line-height: 1;\"><span style=\"font-size: 14px;\"><span style=\"font-family: Helvetica;\"><strong><span style=\"color: red; border: 1pt none windowtext; padding: 0cm;\">Dort wird Ihnen aufgelistet, wie Sie vorgehen m&uuml;ssen.&nbsp;</span></strong></span></span></p>\n" +
            "<p style=\"background: white none repeat scroll 0% 0%; vertical-align: baseline; line-height: 1;\"><span style=\"font-size: 14px;\"><span style=\"font-family: Helvetica;\"><span style=\"color: black; border: 1pt none windowtext; padding: 0cm;\">&nbsp;</span></span></span></p>\n" +
            "<p style=\"background: white none repeat scroll 0% 0%; vertical-align: baseline; line-height: 1;\"><span style=\"font-size: 14px;\"><span style=\"font-family: Helvetica;\"><span style=\"color: black; border: 1pt none windowtext; padding: 0cm;\">Ihr Corona-Schnelltest wurde von folgendem Testzentrum durchgef&uuml;hrt:</span></span></span></p>\n" +
            "<p style=\"margin-bottom: 14pt; background: white none repeat scroll 0% 0%; vertical-align: baseline; line-height: 1;\"><span style=\"font-size: 14px;\"><span style=\"font-family: Helvetica;\"><span style=\"color: black;\">&nbsp;</span></span></span></p>\n" +
            "<p style=\"background: white none repeat scroll 0% 0%; vertical-align: baseline; line-height: 1;\"><span style=\"font-size: 14px;\"><span style=\"font-family: Helvetica;\"><strong><span style=\"color: black; border: 1pt none windowtext; padding: 0cm;\">Schnelltestzentrum Donautal</span></strong></span></span></p>\n" +
            "<p style=\"background: white none repeat scroll 0% 0%; vertical-align: baseline; line-height: 1;\"><span style=\"font-size: 14px;\"><span style=\"font-family: Helvetica;\"><span style=\"color: black; border: 1pt none windowtext; padding: 0cm;\">Boschstra&szlig;e 40</span></span></span></p>\n" +
            "<p style=\"background: white none repeat scroll 0% 0%; vertical-align: baseline; line-height: 1;\"><span style=\"font-size: 14px;\"><span style=\"font-family: Helvetica;\"><span style=\"color: black; border: 1pt none windowtext; padding: 0cm;\">88045 Ulm&nbsp;</span></span></span></p>\n" +
            "<p style=\"background: white none repeat scroll 0% 0%; vertical-align: baseline; line-height: 1;\"><span style=\"font-size: 14px;\"><span style=\"font-family: Helvetica;\"><span style=\"color: black; border: 1pt none windowtext; padding: 0cm;\">+49 176 71230495</span></span></span></p>\n" +
            "<p style=\"margin-bottom: 14pt; background: white none repeat scroll 0% 0%; vertical-align: baseline; line-height: 1;\"><span style=\"font-size: 14px;\"><span style=\"font-family: Helvetica;\"><span style=\"color: black;\">&nbsp;</span></span></span></p>\n" +
            "<p style=\"background: white none repeat scroll 0% 0%; vertical-align: baseline; line-height: 1;\"><span style=\"font-size: 14px;\"><span style=\"font-family: Helvetica;\"><span style=\"color: black; border: 1pt none windowtext; padding: 0cm;\">F&uuml;r R&uuml;ckfragen stehen wir gerne zur Verf&uuml;gung. Bleiben Sie gesund !&nbsp;</span></span></span></p>\n" +
            "<p style=\"margin-bottom: 14pt; background: white none repeat scroll 0% 0%; vertical-align: baseline; line-height: 1;\"><span style=\"font-size: 14px;\"><span style=\"font-family: Helvetica;\"><span style=\"color: black;\">&nbsp;</span></span></span></p>\n" +
            "<p style=\"background: white none repeat scroll 0% 0%; vertical-align: baseline; line-height: 1;\"><span style=\"font-size: 14px;\"><span style=\"font-family: Helvetica;\"><span style=\"color: black; border: 1pt none windowtext; padding: 0cm;\">Herzliche Gr&uuml;&szlig;e</span><span style=\"color: black;\">&nbsp;</span></span></span></p>\n" +
            "<p style=\"background: white none repeat scroll 0% 0%; vertical-align: baseline; line-height: 1;\"><span style=\"font-size: 14px;\"><span style=\"font-family: Helvetica;\"><span style=\"color: black; border: 1pt none windowtext; padding: 0cm;\">Schnelltestzentrum Donautal&nbsp;</span></span></span></p>" + "</html>";
}