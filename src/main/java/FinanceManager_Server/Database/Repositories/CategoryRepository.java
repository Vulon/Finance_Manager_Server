package FinanceManager_Server.Database.Repositories;

import FinanceManager_Server.Database.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query
    List<Category> getAllByCommitDateAfter(Date date);

    @Query
    Category getByUserAndCategory(Long user_id, Long category_id);

    @Query(value = "SELECT MAX(c.category) FROM Category c WHERE c.user = :user")
    Long getMaxId(@Param(value = "user") Long user_id);

}
