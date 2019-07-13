package FinanceManager_Server.Database.Repositories;

import FinanceManager_Server.Database.Entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    @Query
    public List<Budget> getAllByCommitDateAfter(Date date);
}
