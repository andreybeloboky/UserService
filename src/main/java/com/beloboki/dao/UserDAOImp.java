package com.beloboki.dao;

import com.beloboki.model.PaymentCard;
import com.beloboki.model.User;

import java.util.List;

public class UserDAOImp implements UserDAO{

    @Override
    public void createUser(User user) {

    }

    @Override
    public void createCard(PaymentCard card) {

    }

    @Override
    public User retrieveUserById(Long id) {
        return null;
    }

    @Override
    public List<PaymentCard> retrieveAllCardsByUserId(Long userId) {
        return List.of();
    }

    @Override
    public void updateUserById(User user, Long id) {

    }

    @Override
    public void userStatus(Boolean status) {

    }

    @Override
    public void cardStatus(Boolean status) {

    }
}
