package FinanceManager_Server.Services;

import FinanceManager_Server.Application;
import io.jsonwebtoken.impl.TextCodec;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class PropertiesDataManager implements Serializable {

    private static final long serialVersionUID = 8571137094210487341L;
    private SecretData secretData = null;
    public static String SERVER_ADDRESS = "http://localhost:8081";

    public PropertiesDataManager() {
    }

    public void loadData(){
        //saveData();
        InputStream inputStream = null;
        try{
            ClassLoader classLoader = Application.class.getClassLoader();
            File file = new File(classLoader.getResource("data.properties").getFile());
            inputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            secretData = (SecretData)objectInputStream.readObject();
            inputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public byte[] getEncodedSecretKey(){
        return TextCodec.BASE64.decode(getSecretKey());
    }

    public String getEmailAddress(){
        if (secretData == null){
            loadData();
        }
        return secretData.getHostAddress();
    }

    public   String getEmailApiPassword(){
        if (secretData == null){
            loadData();
        }
        return secretData.getHostApiPassword();
    }
    public  String getSecretKey(){
        if (secretData == null){
            loadData();
        }
        return  secretData.getSecret_key();
    }

    private   class SecretData implements Serializable {
        private static final long serialVersionUID = -3895578810390383253L;
        String hostAddress;
        String hostApiPassword;
        String secret_key;


        public SecretData() {
        }

        public SecretData(String hostAddress, String hostApiPassword, String secret_key) {
            this.hostAddress = hostAddress;
            this.hostApiPassword = hostApiPassword;
            this.secret_key = secret_key;
        }

        public String getHostAddress() {
            return hostAddress;
        }

        public String getHostApiPassword() {
            return hostApiPassword;
        }

        public String getSecret_key() {
            return secret_key;
        }
    }
}
