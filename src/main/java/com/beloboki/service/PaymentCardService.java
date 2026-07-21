package com.beloboki.service;

import com.beloboki.dao.PaymentCardDAO;
import com.beloboki.dao.UserDAO;
import com.beloboki.model.PaymentCard;
import com.beloboki.model.User;
import com.beloboki.specification.PaymentCardSpecifications;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PaymentCardService {

    private final PaymentCardDAO paymentCardDAO;
    private final UserDAO userDAO;
    private static final Integer PAGE_NUMBER = 0;
    private static final Integer PAGE_SIZE = 10;

    public Page<PaymentCard> retrieveFilterByHolder(String holder) {
        if (holder == null) {
            throw new IllegalArgumentException("Holder must not be null");
        }

        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        return paymentCardDAO.findAll(
                Specification.where(PaymentCardSpecifications.hasHolder(holder)), pageable);
    }

    public PaymentCard save(Long userId, PaymentCard paymentCard) {
        var user = findUserById(userId, paymentCard);

        if (user.getPaymentCards().size() < 5) {
            user.getPaymentCards().add(paymentCard);
        } else {
            throw new IllegalArgumentException(
                    "User with %s has cards limit 5/5".formatted(userId));
        }
        userDAO.saveAndFlush(user);
        return paymentCard;
    }

    public List<PaymentCard> retrieveAllCardsByUserId(Long id) {
        return paymentCardDAO.findAllCardByUserId(id);
    }

    public PaymentCard updateById(Long id, PaymentCard paymentCard) {
        var card = findCardById(id);

        paymentCard.setUser(card.getUser());
        paymentCard.setId(card.getId());
        return paymentCardDAO.save(paymentCard);
    }

    public PaymentCard setStatus(Long id, Boolean status) {
        var card = findCardById(id);

        card.setActive(status);
        card.setId(card.getId());
        return paymentCardDAO.save(card);
    }

    public void deleteById(Long id) {
        paymentCardDAO.deleteById(id);
    }

    private User findUserById(Long userId, PaymentCard paymentCard) {
        var user =
                userDAO.findById(userId)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "Not found user by id = %s".formatted(userId)));
        paymentCard.setUser(user);
        return user;
    }

    private PaymentCard findCardById(Long id) {
        return paymentCardDAO
                .findById(id)
                .orElseThrow(
                        () ->
                                new EntityNotFoundException(
                                        "Not found user by id = %s".formatted(id)));
    }
}
