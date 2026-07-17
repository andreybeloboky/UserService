package com.beloboki.dao;

import com.beloboki.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDAO extends JpaRepository<User, Long> {

    Page<User> findByNameAndSurname(String name, String surname, Pageable pageable);
}
