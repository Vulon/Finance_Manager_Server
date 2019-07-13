package FinanceManager_Server.Database.Repositories;

import FinanceManager_Server.Database.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query
    public List<Transaction> getAllByCommitDateAfter(Date date);
}
