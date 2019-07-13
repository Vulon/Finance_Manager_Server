package FinanceManager_Server.Database.Entity;

import java.util.Date;

public abstract class Action implements Comparable{

    @Override
    public int compareTo(Object o) {
        Action other = (Action)o;
        return Long.compare(this.getCommitDate().getTime(), other.getCommitDate().getTime());
    }

    public abstract Date getCommitDate();

    public abstract void setCommitDate(Date commitDate);

    public abstract String getType();

    public abstract boolean isCreate();

    public abstract void setCreate(boolean create);
}
