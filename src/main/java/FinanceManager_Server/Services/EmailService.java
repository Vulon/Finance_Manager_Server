package FinanceManager_Server.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailService {
    @Bean
    public JavaMailSender getJavaMailSender(PropertiesDataManager propertiesDataManager){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        //propertiesDataManager.saveData();
        mailSender.setUsername(propertiesDataManager.getEmailAddress());
        mailSender.setPassword(propertiesDataManager.getEmailApiPassword());
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "false");
        return  mailSender;
    }

    @Autowired JavaMailSender mailSender;

    public void sendMessage(String address, String subject, String text){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(address);
        mailMessage.setSubject(subject);
        mailMessage.setText(text);
        mailSender.send(mailMessage);
    }

    public String sendVerification(String email, String access_token){
        sendMessage(email, "Registration in Finance Manager App", "Your email was registered in Finance Manager App\n" +
                "If it was not you, please, do not follow this link, your data will be deleted from server in 30 mins" +
                "\nTo verify your email please follow that link: " + PropertiesDataManager.SERVER_ADDRESS +"" +
                "/verify?access_token=" + access_token);
        return PropertiesDataManager.SERVER_ADDRESS + "/verify?access_token=" + access_token;
    }

}
