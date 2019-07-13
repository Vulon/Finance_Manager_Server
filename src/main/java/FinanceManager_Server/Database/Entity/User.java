package FinanceManager_Server.Database.Entity;


import FinanceManager_Server.TransportableDataObjects.TokenData;

import javax.persistence.*;

@Entity
@Table(name = "user")
public class User {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "email", length = 30)
    private String email;

    @Column(name = "password", length = 30)
    private String password;

    @Column(name = "lang", length = 10)
    private String lang;

    @Column(name = "verified")
    private boolean verified;

    @Column(name = "access_token")
    private String access_token;

    @Column(name = "refresh_token")
    private String refresh_token;


    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public void setTokenData(TokenData tokenData){
        access_token = tokenData.getAccess_token();
        refresh_token = tokenData.getRefresh_token();
    }


    public User(String email, String password, String lang, Boolean verified, String access_token, String refresh_token) {
        this.email = email;
        this.password = password;
        this.lang = lang;
        this.verified = verified;
        this.access_token = access_token;
        this.refresh_token = refresh_token;
    }

    public User() {
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }


    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }
}
