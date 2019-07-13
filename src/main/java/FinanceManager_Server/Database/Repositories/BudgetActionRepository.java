package FinanceManager_Server.Database.Repositories;


import FinanceManager_Server.Database.Entity.BudgetAction;
import FinanceManager_Server.Database.Entity.Database_pk.BudgetPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface BudgetActionRepository extends JpaRepository<BudgetAction, BudgetPK> {

    @Query
    public List<BudgetAction> getAllByCommitDateAfter(Date date);
}
