package com.beloboki.service;

import com.beloboki.dao.UserDAO;
import com.beloboki.initialize.UserSpecifications;
import com.beloboki.model.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserDAO userDAO;
    private static final Integer PAGE_NUMBER = 0;
    private static final Integer PAGE_SIZE = 10;

    public User save(User user) {
        return userDAO.saveAndFlush(user);
    }

    public User retrieveById(Long id) {
        return userDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found user by id = " + id));
    }

    public Page<User> retrieveFilterByNameAndSurname(String name, String surname) {
        if (name == null || surname == null) {
            throw new IllegalArgumentException("Name filter must not be null");
        }

        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

        return userDAO.findAll(
                Specification.where(UserSpecifications.hasName(name))
                        .and(UserSpecifications.hasSurname(surname)),
                pageable);
    }

    public User updateById(Long id, User user) {
        var userById =
                userDAO.findById(id)
                        .orElseThrow(
                                () -> new EntityNotFoundException("Not found user by id = " + id));

        user.setId(userById.getId());
        return userDAO.save(user);
    }

    public User setStatus(Long id, Boolean status) {
        var userById =
                userDAO.findById(id)
                        .orElseThrow(
                                () -> new EntityNotFoundException("Not found user by id = " + id));

        userById.setActive(status);
        return userDAO.save(userById);
    }

    public void deleteById(Long id) {
        userDAO.deleteById(id);
    }
}
