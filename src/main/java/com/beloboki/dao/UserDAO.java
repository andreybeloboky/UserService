package com.beloboki.dao;

import com.beloboki.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.Optional;

@Repository
public interface UserDAO extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findUserById(@Param("id") Long id);

    Optional<User> findUserByName(String name);

    @Query(value = "SELECT * FROM users u WHERE u.surname = ?", nativeQuery = true)
    Optional<User> findUserBySurname(@Param("surname") String surname);
}
