package com.znahodochka.backend.users.repositories;

import com.znahodochka.backend.users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //Optional<User> findByUsername(String username);

    Optional<User>  findByLogin(String login);

    boolean existsByLogin(String username);

    boolean existsByEmail(String email);

    Optional<User> findById(Long principal);

  //  void save(User follower);
}
