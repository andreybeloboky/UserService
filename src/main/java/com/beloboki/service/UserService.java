package com.beloboki.service;

import com.beloboki.dao.UserDAO;
import com.beloboki.model.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class UserService {

    private final UserDAO userDAO;

    @Autowired
    public UserService(UserDAO dao) {
        this.userDAO = dao;
    }

    public void saveUser(User user) {
        userDAO.saveAndFlush(user);
    }

    public User retrieveUserById(Long id) {
        return userDAO.findUserById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found user by id = " + id));
    }

    public Page<User> retrieveUsersFilterByNameAndSurname(String name, String surname) {
        Pageable pageable =
                PageRequest.of(
                        0, 10, Sort.by("name").ascending().and(Sort.by("surname").ascending()));

        /* Specification<User> spec = Specification
               .where

        */
        return userDAO.findAll(pageable);
    }

    public User updateUserById(Long id, User user) {
        var userById =
                userDAO.findUserById(id)
                        .orElseThrow(
                                () -> new EntityNotFoundException("Not found user by id = " + id));

        user.setId(userById.getId());

        return userDAO.save(user);
    }

    public User setStatusUser(Long id) {
        var userById =
                userDAO.findUserById(id)
                        .orElseThrow(
                                () -> new EntityNotFoundException("Not found user by id = " + id));

        Boolean meaning = userById.getActive();
        userById.setActive(!meaning);

        return userDAO.save(userById);
    }
}
