package FinanceManager_Server.Database.Repositories;

import FinanceManager_Server.Database.Entity.Budget;
import FinanceManager_Server.Database.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    @Query
    List<Budget> getAllByCommitDateAfter(Date date);

    @Query
    @Modifying
    @Transactional
    void deleteByUserAndBudget(Long user, Long budget);

    @Query(value = "SELECT b FROM Budget b WHERE b.user = :user AND :category IN (b.categories)") //TODO CHECK THAT
    List<Budget> getAllByUserAndCategory(@Param(value = "user")Long user, @Param(value = "category") Category category);

    @Query
    Budget findByUserAndBudget(Long user, Long budget);

    @Query(value = "SELECT MAX(b.budget) FROM Budget b WHERE b.user = :user")
    Long getMaxId(@Param(value = "user") Long user_id);
}
