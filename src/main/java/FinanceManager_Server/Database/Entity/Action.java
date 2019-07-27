package FinanceManager_Server.Database.Entity;

import java.util.Date;

public interface Action extends Comparable{

    @Override
    default int compareTo(Object o) {
        Action other = (Action)o;
        return Long.compare(this.getCommitDate().getTime(), other.getCommitDate().getTime());
    }

    Date getCommitDate();

    void setCommitDate(Date commitDate);

    String getType();

    boolean isCreate();

    void setCreate(boolean create);

    Long getOriginalId();
}
