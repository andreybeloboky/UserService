package com.beloboki.service;

import com.beloboki.dao.UserDAO;
import com.beloboki.model.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

public class UserService {

    private final UserDAO userDAO;

    @Autowired
    public UserService(UserDAO userDao) {
        this.userDAO = userDao;
    }

    public void saveUser(User user) {
        userDAO.saveAndFlush(user);
    }

    public User retrieveUserById(Long id) {
        return userDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found user by id = " + id));
    }

    public Page<User> retrieveUsersFilterByNameAndSurname(String name, String surname) {
        Pageable pageable = PageRequest.of(0, 10,
                Sort.by("name").ascending().and(Sort.by("surname").ascending()));

        /* Specification<User> spec = Specification
                .where

         */
        return userDAO.findAll(pageable);
    }

    public User updateUserById(Long id, User user) {
        var userById = userDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found user by id = " + id));

        user.setId(userById.getId());
        user.setPaymentCards(userById.getPaymentCards());

        return userDAO.save(user);
    }

    public void setStatus(Long id) {

    }
}
