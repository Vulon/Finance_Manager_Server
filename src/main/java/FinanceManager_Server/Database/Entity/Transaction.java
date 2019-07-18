package FinanceManager_Server.Database.Entity;

import FinanceManager_Server.Database.Entity.Database_pk.TransactionPK;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;



@Entity
@Table(name = "transaction")
@IdClass(TransactionPK.class)
public class Transaction implements Serializable, Action {
    private static final long serialVersionUID = -5744874026510540290L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "transaction")
    private Long transaction;

    @Id
    @Column(name = "user")
    private Long user;

    @Column(name = "amount")
    private Double amount;


    @Temporal(TemporalType.DATE)
    @Column(name = "date")
    private Date date;

    @Column(name = "note")
    private String note;

    @OneToOne
    private Category category;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "commit_date")
    private Date commitDate;

    public Transaction(Long user_id, Double amount, Date date, String note, Category category, Date commitDate) {
        this.user = user_id;
        this.amount = amount;
        this.date = date;
        this.note = note;
        this.category = category;
        this.commitDate = commitDate;
    }

    public Transaction(TransactionAction action, Category category){
        this.user = action.getUser();
        this.amount = action.getAmount();
        this.date = action.getDate();
        this.note = action.getNote();
        this.category = category;
        this.commitDate = action.getCommitDate();
    }

    public Transaction() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(transaction, that.transaction) &&
                Objects.equals(user, that.user) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(date, that.date) &&
                Objects.equals(note, that.note) &&
                Objects.equals(category, that.category) &&
                Objects.equals(commitDate, that.commitDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transaction, user, amount, date, note, category, commitDate);
    }

    @Override
    public String getType() {
        return "transaction";
    }

    @Override
    public boolean isCreate() {
        return true;
    }

    @Override
    public void setCreate(boolean create) {

    }

    public Long getTransaction() {
        return transaction;
    }

    public void setTransaction(Long transaction) {
        this.transaction = transaction;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getCommitDate() {
        return commitDate;
    }

    public void setCommitDate(Date commitDate) {
        this.commitDate = commitDate;
    }
}
