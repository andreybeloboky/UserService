package com.beloboki.service;

import com.beloboki.config.CurrentUser;
import com.beloboki.dao.PaymentCardDAO;
import com.beloboki.dto.PaymentCardRequest;
import com.beloboki.dto.PaymentCardResponse;
import com.beloboki.exception.CardLimitException;
import com.beloboki.mapper.PaymentCardMapper;
import com.beloboki.model.PaymentCard;
import com.beloboki.model.Role;
import com.beloboki.model.User;
import com.beloboki.specification.PaymentCardSpecifications;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@CacheConfig(cacheNames = "payment_cards")
public class PaymentCardService {

    private final PaymentCardDAO paymentCardDAO;
    private final UserService userService;
    private final PaymentCardMapper paymentCardMapper;
    private static final Integer CARD_LIMIT = 5;

    @CacheEvict(cacheNames = "users", key = "#userId")
    @Transactional
    public void save(Long userId, PaymentCardRequest paymentCardRequest) {
        PaymentCard paymentCard =
                paymentCardMapper.paymentCardRequestToPaymentCard(paymentCardRequest);
        User user = userService.retrieveByUserIdLocking(userId);
        if (user.getPaymentCards().size() < CARD_LIMIT) {
            paymentCard.setUser(user);
            paymentCardDAO.saveAndFlush(paymentCard);
        } else {
            throw new CardLimitException("User with %s has cards limit 5/5".formatted(userId));
        }
    }

    @Cacheable(key = "#id")
    @Transactional(readOnly = true)
    public PaymentCardResponse retrieveById(Long id, CurrentUser currentUser) {
        PaymentCard paymentCard = findCardById(id);
        validate(paymentCard.getUser().getId(), currentUser.userId(), currentUser.role());
        return paymentCardMapper.cardToCardResponse(paymentCard);
    }

    @Transactional(readOnly = true)
    public List<PaymentCardResponse> retrieveAllCardsByUserId(Long id) {
        List<PaymentCard> paymentCards = paymentCardDAO.findAllCardByUserId(id);
        return paymentCards.stream().map(paymentCardMapper::cardToCardResponse).toList();
    }

    @Caching(
            evict = {
                @CacheEvict(key = "#id"),
                @CacheEvict(cacheNames = "users", key = "#result.user.id")
            })
    @Transactional
    public PaymentCard updateById(Long id, PaymentCardRequest paymentCardRequest) {
        PaymentCard paymentCard =
                paymentCardMapper.paymentCardRequestToPaymentCard(paymentCardRequest);
        var card = findCardById(id);

        paymentCard.setUser(card.getUser());
        paymentCard.setId(card.getId());
        return paymentCardDAO.saveAndFlush(paymentCard);
    }

    @CacheEvict(key = "#id")
    @Transactional
    public void updateStatus(Long id, Boolean status) {
        var card = findCardById(id);

        card.setActive(status);
        card.setId(card.getId());
        paymentCardDAO.saveAndFlush(card);
    }

    @CacheEvict(key = "#id")
    @Transactional
    public void deleteById(Long id) {
        if (!paymentCardDAO.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: %s".formatted(id));
        }
        paymentCardDAO.deleteById(id);
    }

    public Page<PaymentCardResponse> retrieveFilterByHolder(
            String holder, int pageNumber, int pageSize) {
        if (holder == null || holder.isBlank()) {
            throw new IllegalArgumentException("Holder must not be null");
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<PaymentCard> paymentCards =
                paymentCardDAO.findAll(
                        Specification.where(PaymentCardSpecifications.hasHolder(holder)), pageable);
        return paymentCards.map(paymentCardMapper::cardToCardResponse);
    }

    private PaymentCard findCardById(Long id) {
        return paymentCardDAO
                .findById(id)
                .orElseThrow(
                        () ->
                                new EntityNotFoundException(
                                        "Not found user by id = %s".formatted(id)));
    }

    public void validate(Long currentUser, Long userId, String role) {
        if (!Objects.equals(currentUser, userId)
                && (!Objects.equals(role, String.valueOf(Role.ADMIN)))) {
            throw new AuthorizationDeniedException("Access denied");
        }
    }
}
