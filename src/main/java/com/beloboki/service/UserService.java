package com.beloboki.service;

import com.beloboki.dao.UserDAO;
import com.beloboki.initialize.UserSpecifications;
import com.beloboki.model.PaymentCard;
import com.beloboki.model.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserDAO userDAO;

    @Autowired
    public UserService(UserDAO dao) {
        this.userDAO = dao;
    }

    public User saveUser(User user) {
        return userDAO.saveAndFlush(user);
    }

    public User retrieveUserById(Long id) {
        return userDAO.findUserById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found user by id = " + id));
    }

    public Page<User> retrieveUsersFilterByNameAndSurname(String name, String surname) {
        Pageable pageable = PageRequest.of(0, 10,
                Sort.by("name").ascending().and(Sort.by("surname").ascending()));

        return userDAO.findAll(Specification.where(UserSpecifications.hasName(name))
                .and(UserSpecifications.hasSurname(surname)), pageable);
    }

    public List<PaymentCard> retrieveAllCardByUserId(Long id) {
        User user = userDAO.findUserById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found user by id = " + id));
        return user.getPaymentCards();
    }

    @Transactional
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

    @Transactional
    public Boolean deleteUserById(Long id) {
        return userDAO.deleteByUserId(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found user by id = " + id));
    }
}
