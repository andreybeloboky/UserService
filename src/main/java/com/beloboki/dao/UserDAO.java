package com.beloboki.dao;

import com.beloboki.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDAO extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findUserById(@Param("id") Long id);

    Optional<User> findUserByName(String name);

    @Query(value = "DELETE FROM users u WHERE u.id = ?", nativeQuery = true)
    Optional<Boolean> deleteByUserId(@Param("id") Long id);
}
