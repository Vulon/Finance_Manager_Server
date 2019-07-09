package FinanceManager_Server.Database;

import FinanceManager_Server.Database.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
