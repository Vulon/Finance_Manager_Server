package FinanceManager_Server.Database;

import FinanceManager_Server.Database.Entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

}
