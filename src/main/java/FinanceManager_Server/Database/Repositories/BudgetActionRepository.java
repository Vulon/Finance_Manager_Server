package FinanceManager_Server.Database.Repositories;


import FinanceManager_Server.Database.Entity.BudgetAction;
import FinanceManager_Server.Database.Entity.Database_pk.BudgetPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

public interface BudgetActionRepository extends JpaRepository<BudgetAction, BudgetPK> {

    @Query
    public List<BudgetAction> getAllByUserAndCommitDateAfter(Long user, Date date);


    @Query
    int countByUserAndBudget(Long user_id, Long budget_id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM BudgetAction ba WHERE ba.user = :user " +
            "AND ba.commitDate <= (SELECT MIN (b.commitDate) FROM BudgetAction b)")
    void deleteOldest(@Param(value = "user")Long user_id);
}
