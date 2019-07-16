package FinanceManager_Server.Database.Repositories;


import FinanceManager_Server.Database.Entity.Database_pk.TransactionPK;
import FinanceManager_Server.Database.Entity.TransactionAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

public interface TransactionActionRepository extends JpaRepository<TransactionAction, TransactionPK> {
    @Query
    List<TransactionAction> getAllByCommitDateAfter(Date date);

    @Query
    int countByUserAndTransaction(Long user_id, Long transaction_id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM TransactionAction tr WHERE tr.user = :user AND" +
            " tr.commitDate <= (SELECT MIN (t.commitDate) FROM TransactionAction t)")
    void deleteOldest(@Param(value = "user")Long user_id);
}
