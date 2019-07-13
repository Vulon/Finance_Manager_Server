package FinanceManager_Server.Database.Repositories;

import FinanceManager_Server.Database.Entity.CategoryAction;
import FinanceManager_Server.Database.Entity.Database_pk.CategoryPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface CategoryActionRepository extends JpaRepository<CategoryAction, CategoryPK> {
    @Query
    public List<CategoryAction> getAllByCommitDateAfter(Date date);
}
