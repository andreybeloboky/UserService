package com.beloboki.dao;

import com.beloboki.model.PaymentCard;
import com.beloboki.model.User;

import java.util.List;

public interface UserDAO {

    void save(User user);

    User retrieveUserById(Long id);

    void updateUserById(User user, Long id);

    void userStatus(Boolean status);
}
