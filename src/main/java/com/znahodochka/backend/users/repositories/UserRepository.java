package com.znahodochka.backend.users.repositories;

import com.znahodochka.backend.users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //Optional<User> findByUsername(String username);

    Optional<User>  findByLogin(String login);

    boolean existsByLogin(String username);

    boolean existsByEmail(String email);

    Optional<User> findById(Long principal);

  //  void save(User follower);

    @Query(value = """
    SELECT * FROM users.users u
            WHERE LOWER(u.login) LIKE LOWER(:query) || '%'
            OR LOWER(u.login) LIKE '%' || LOWER(:query) || '%'
            ORDER BY 
                    CASE
                        WHEN LOWER(u.login) = LOWER(:query) THEN 0
                        WHEN LOWER(u.login) LIKE (:query) || '%' THEN 1
                        ELSE 2
                    END,
                        u.login
            LIMIT 20
                        
            """, nativeQuery = true)
    List<User> findByLoginLike(@Param("query") String login);


}
