package FinanceManager_Server.Database.Repositories;

import FinanceManager_Server.Database.Entity.Category;
import FinanceManager_Server.Database.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query
    List<Transaction> getAllByCommitDateAfter(Date date);

    @Modifying
    @Transactional
    void deleteByUserAndTransaction(Long user, Long transaction);

    @Query
    List<Transaction> getAllByCategory(Category category);

    @Query(value = "SELECT MAX(t.transaction) FROM Transaction t WHERE t.user = :user_id")
    Long getMaxId(@Param(value = "user_id") Long user);

    @Query
    Transaction getByUserAndTransaction(Long user, Long transaction);
}
