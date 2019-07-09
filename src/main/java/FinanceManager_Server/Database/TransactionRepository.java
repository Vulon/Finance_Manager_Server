package FinanceManager_Server.Database;

import FinanceManager_Server.Database.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

}
