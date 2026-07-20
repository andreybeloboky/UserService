package com.beloboki.service;

import com.beloboki.dao.PaymentCardDAO;
import com.beloboki.dao.UserDAO;
import com.beloboki.model.PaymentCard;
import com.beloboki.model.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PaymentCardService {

    private final PaymentCardDAO paymentCardDAO;
    private final UserDAO userDAO;

    public void save(PaymentCard paymentCard, Long id) {
        User user = userDAO.findById(id)
                        .orElseThrow(
                                () -> new EntityNotFoundException("Not found user by id = " + id));

        if (user.getPaymentCards().size() < 5) {
            user.getPaymentCards().add(paymentCard);
        } else {
            throw new IllegalArgumentException("User with " + id + " has cards limit 5/5");
        }
        userDAO.saveAndFlush(user);
    }

    public List<PaymentCard> retrieveAllCardsByUserId(Long id) {
        return paymentCardDAO.findAllCardByUserId(id);
    }

    public PaymentCard retrieveById(Long id) {
        return paymentCardDAO
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found user by id = " + id));
    }

    @Transactional
    public PaymentCard updateById(Long id, PaymentCard paymentCard) {
        var cardById =
                paymentCardDAO
                        .findById(id)
                        .orElseThrow(
                                () -> new EntityNotFoundException("Not found user by id = " + id));

        paymentCard.setId(cardById.getId());
        return paymentCardDAO.save(paymentCard);
    }

    public PaymentCard setStatus(Long id, Boolean status) {
        var cardById =
                paymentCardDAO
                        .findById(id)
                        .orElseThrow(
                                () -> new EntityNotFoundException("Not found user by id = " + id));

        cardById.setActive(status);
        return paymentCardDAO.save(cardById);
    }

    public void deleteById(Long id) {
        paymentCardDAO.deleteById(id);
    }
}
