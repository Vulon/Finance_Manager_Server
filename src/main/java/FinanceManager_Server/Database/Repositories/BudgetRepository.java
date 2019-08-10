package FinanceManager_Server.Database.Repositories;

import FinanceManager_Server.Database.Entity.Budget;
import FinanceManager_Server.Database.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    @Query
    List<Budget> getAllByUserAndCommitDateAfter(Long user, Date date);

    @Query
    @Modifying
    @Transactional
    void deleteByUserAndBudget(Long user, Long budget);


    @Query
    ArrayList<Budget> getAllByUser(Long user);

    @Query
    Budget findByUserAndBudget(Long user, Long budget);

    @Query(value = "SELECT MAX(b.budget) FROM Budget b WHERE b.user = :user")
    Long getMaxId(@Param(value = "user") Long user_id);
}
