package com.beloboki.service;

import com.beloboki.dao.PaymentCardDAO;
import com.beloboki.dao.UserDAO;
import com.beloboki.exception.CardLimitException;
import com.beloboki.model.PaymentCard;
import com.beloboki.model.User;
import com.beloboki.specification.PaymentCardSpecifications;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@CacheConfig(cacheNames = "payment_cards")
public class PaymentCardService {

    private final PaymentCardDAO paymentCardDAO;
    private final UserDAO userDAO;
    private static final Integer CARD_LIMIT = 5;

    public void save(Long userId, PaymentCard paymentCard) {
        var user = findUserById(userId, paymentCard);

        if (user.getPaymentCards().size() < CARD_LIMIT) {
            user.getPaymentCards().add(paymentCard);
        } else {
            throw new CardLimitException("User with %s has cards limit 5/5".formatted(userId));
        }
        userDAO.saveAndFlush(user);
    }

    @Cacheable(key = "#id")
    public PaymentCard retrieveById(Long id) {
        return findCardById(id);
    }

    public List<PaymentCard> retrieveAllCardsByUserId(Long id) {
        return paymentCardDAO.findAllCardByUserId(id);
    }

    @Caching(
            evict = {
                @CacheEvict(key = "#id"),
                @CacheEvict(cacheNames = "users", key = "#result.user.id")
            })
    public void updateById(Long id, PaymentCard paymentCard) {
        var card = findCardById(id);

        paymentCard.setUser(card.getUser());
        paymentCard.setId(card.getId());
        paymentCardDAO.saveAndFlush(paymentCard);
    }

    @CacheEvict(key = "#id")
    public void updateStatus(Long id, Boolean status) {
        var card = findCardById(id);

        card.setActive(status);
        card.setId(card.getId());
        paymentCardDAO.saveAndFlush(card);
    }

    @CacheEvict(key = "#id")
    public void deleteById(Long id) {
        if (!paymentCardDAO.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: %s".formatted(id));
        }
        paymentCardDAO.deleteById(id);
    }

    public Page<PaymentCard> retrieveFilterByHolder(String holder, int pageNumber, int pageSize) {
        if (holder == null || holder.isBlank()) {
            throw new IllegalArgumentException("Holder must not be null");
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return paymentCardDAO.findAll(
                Specification.where(PaymentCardSpecifications.hasHolder(holder)), pageable);
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
