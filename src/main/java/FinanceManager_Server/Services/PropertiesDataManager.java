package FinanceManager_Server.Services;

import FinanceManager_Server.Application;
import io.jsonwebtoken.impl.TextCodec;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class PropertiesDataManager implements Serializable {

    private static final long serialVersionUID = 8571137094210487341L;

    private Environment env;

    public static String SERVER_ADDRESS = "http://localhost:8081";

    public PropertiesDataManager(Environment env) {
        this.env = env;
    }




    public byte[] getEncodedSecretKey(){
        return TextCodec.BASE64.decode(getSecretKey());
    }

    public String getEmailAddress(){
        return env.getProperty("email_host");
    }

    public   String getEmailApiPassword(){
        return env.getProperty("google_api_key");
    }
    public  String getSecretKey(){
        return env.getProperty("secret_key");
    }


}
