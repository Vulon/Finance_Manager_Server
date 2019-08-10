package FinanceManager_Server.Database.Entity;

import FinanceManager_Server.Database.Entity.Database_pk.TransactionPK;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;


@Entity
@Table(name = "transaction_action")
@JsonIgnoreProperties
@IdClass(TransactionPK.class)
public class TransactionAction implements Serializable, Action {

    private static final long serialVersionUID = 8921236138362380586L;
    @Column(name = "create")
    private boolean create;

    @Column(name = "commit_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date commitDate;

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

    @Column(name = "category")
    private Long category_id;

    private Long originalId;

    @Column(name = "repeatable")
    private boolean repeatable;

    public static ArrayList<TransactionAction> toTransactionAction(Collection<Transaction> c){
        if (c == null){
            return new ArrayList<>();
        }
        ArrayList<TransactionAction> arrayList = new ArrayList<>(c.size());
        for(Transaction b : c){
            arrayList.add(new TransactionAction(true, b));
        }
        return arrayList;
    }


    public TransactionAction(boolean isCreate, Transaction transaction) {
        this.create = isCreate;
        this.commitDate = transaction.getCommitDate();
        this.transaction = transaction.getTransaction();
        this.user = transaction.getUser();
        this.amount = transaction.getAmount();
        this.date = transaction.getDate();
        this.note = transaction.getNote();
        this.category_id = transaction.getCategory().getCategory();
        this.originalId = transaction.getOriginalId();
        this.repeatable = transaction.isRepeatable();
    }

    public TransactionAction() {
    }

    @Override
    public String toString() {
        return "TransactionAction{" +
                "create=" + create +
                ", commitDate=" + commitDate +
                ", transaction=" + transaction +
                ", user=" + user +
                ", amount=" + amount +
                ", date=" + date +
                ", note='" + note + '\'' +
                ", category_id=" + category_id +
                ", originalId=" + originalId +
                ", repeatable=" + repeatable +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionAction that = (TransactionAction) o;
        return create == that.create &&
                repeatable == that.repeatable &&
                Objects.equals(commitDate, that.commitDate) &&
                Objects.equals(transaction, that.transaction) &&
                Objects.equals(user, that.user) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(date, that.date) &&
                Objects.equals(note, that.note) &&
                Objects.equals(category_id, that.category_id) &&
                Objects.equals(originalId, that.originalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(create, commitDate, transaction, user, amount, date, note, category_id, originalId, repeatable);
    }

    @Override
    public Long getOriginalId() {
        return originalId;
    }
    @Override
    public String getType() {
        return "transaction";
    }

    public void setTransaction(Long transaction) {
        this.transaction = transaction;
        originalId = transaction;
    }

    @Override
    public boolean isCreate() {
        return create;
    }

    @Override
    public void setCreate(boolean create) {
        this.create = create;
    }

    @Override
    public Date getCommitDate() {
        return commitDate;
    }

    @Override
    public void setCommitDate(Date commitDate) {
        this.commitDate = commitDate;
    }

    public Long getTransaction() {
        return transaction;
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

    public Long getCategory_id() {
        return category_id;
    }

    public void setCategory_id(Long category_id) {
        this.category_id = category_id;
    }

    public void setOriginalId(Long originalId) {
        this.originalId = originalId;
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public void setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
    }
}
