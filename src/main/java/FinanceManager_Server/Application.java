package FinanceManager_Server;


import FinanceManager_Server.Services.EmailService;
import FinanceManager_Server.Services.PropertiesDataManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {
    static ConfigurableApplicationContext springContext;

    public static void main(String[] args) {
        springContext = SpringApplication.run(Application.class, args);
        EmailService emailService = springContext.getBean(EmailService.class);
        PropertiesDataManager propertiesDataManager = springContext.getBean(PropertiesDataManager.class);

    }
}
