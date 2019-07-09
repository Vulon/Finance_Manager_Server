package FinanceManager_Server.Database.Entity;


import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "user_transaction",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "transaction_id")}
    )
    private Set<Transaction> transactions;

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "user_category",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "category_id")}
    )
    private Set<Category> categories;

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "user_budget",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "budget_id")}
    )
    private Set<Budget> budgets;


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


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", lang='" + lang + '\'' +
                ", verified=" + verified +
                ", transactions=" + transactions +
                ", categories=" + categories +
                ", budgets=" + budgets +
                '}';
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

    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = new HashSet<>(transactions);
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = new HashSet<>(categories);
    }

    public Set<Budget> getBudgets() {
        return budgets;
    }

    public void setBudgets(Set<Budget> budgets) {
        this.budgets = new HashSet<>(budgets);
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }
}
