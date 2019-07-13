package FinanceManager_Server.Database.Repositories;

import FinanceManager_Server.Database.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query
    public List<Category> getAllByCommitDateAfter(Date date);
}
