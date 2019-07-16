package FinanceManager_Server.Database.Repositories;

import FinanceManager_Server.Database.Entity.CategoryAction;
import FinanceManager_Server.Database.Entity.Database_pk.CategoryPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

public interface CategoryActionRepository extends JpaRepository<CategoryAction, CategoryPK> {
    @Query
    public List<CategoryAction> getAllByCommitDateAfter(Date date);

    @Query
    int countByUserAndCategory(Long user, Long category);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM CategoryAction ca WHERE ca.user = :user AND " +
            "ca.commitDate <= (SELECT MIN (c.commitDate) FROM CategoryAction c)")
    void deleteOldest(@Param(value = "user")Long user_id);
}
