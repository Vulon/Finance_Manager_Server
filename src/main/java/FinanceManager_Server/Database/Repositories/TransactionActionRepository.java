package FinanceManager_Server.Database.Repositories;


import FinanceManager_Server.Database.Entity.Database_pk.TransactionPK;
import FinanceManager_Server.Database.Entity.TransactionAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface TransactionActionRepository extends JpaRepository<TransactionAction, TransactionPK> {
    @Query
    public List<TransactionAction> getAllByCommitDateAfter(Date date);
}
