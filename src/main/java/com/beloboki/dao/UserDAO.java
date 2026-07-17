package com.beloboki.dao;

import com.beloboki.model.PaymentCard;
import com.beloboki.model.User;

import java.util.List;

public interface UserDAO {

    void createUser(User user);

    void createCard(PaymentCard card);

    User retrieveUserById(Long id);

    List<PaymentCard> retrieveAllCardsByUserId(Long userId);

    void updateUserById(User user, Long id);

    void userStatus(Boolean status);

    void cardStatus(Boolean status);

}
