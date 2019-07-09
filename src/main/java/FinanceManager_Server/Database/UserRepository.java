package FinanceManager_Server.Database;

import FinanceManager_Server.Database.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {



    @Query
    User findByEmail(String email);

    @Query
    User getById(Long id);

    @Modifying(flushAutomatically = true)
    @Transactional
    @Query("UPDATE User u SET u.access_token = :access, u.refresh_token = :refresh WHERE u.id = :id")
    void updateTokens(@Param("id")Long user_id, @Param("access") String access_token, @Param("refresh") String refresh_token);

    @Modifying(flushAutomatically = true)
    @Transactional
    @Query("UPDATE User u SET u.verified = :verified WHERE u.id = :id")
    void setUserVerifiedField(@Param("id") Long user_id, @Param("verified") Boolean verifiedField);

}
