package org.gardella.security.service;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

public class EmailService {

    protected final Log logger = LogFactory.getLog(getClass());
    
    private JavaMailSenderImpl mailSender;

    private static final String DEFAULT_SUPPORT_ADDRESS = "support@gardella.com";
    

    public void sendOutboundHTMLEmail(String email, String subject, String body){
        MimeMessage message = this.mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            // SimpleMailMessage msg = new SimpleMailMessage();
            helper.setFrom(DEFAULT_SUPPORT_ADDRESS);
            helper.setSubject(subject);
            helper.setTo(email);
            helper.setText( body, true );
            this.mailSender.send(message);
        }catch(MessagingException e){
            logger.error( e );
        }
        catch(MailException ex) {
            logger.error( ex );
        }
    }
    
    ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////
    
    public void setMailSender(JavaMailSenderImpl mailSender) {
        this.mailSender = mailSender;
        //this.mailSender.setPort(587);
        //this.mailSender.setUsername("ben@hitplay.com");
        //this.mailSender.setPassword("bgAsdf123");
        
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        
        this.mailSender.setJavaMailProperties(props);
    }
 
}
