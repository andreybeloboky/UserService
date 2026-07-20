package com.beloboki.service;

import com.beloboki.dao.PaymentCardDAO;
import com.beloboki.model.PaymentCard;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentCardService {

    private final PaymentCardDAO paymentCardDAO;

    @Autowired
    public PaymentCardService(PaymentCardDAO paymentCardDAO) {
        this.paymentCardDAO = paymentCardDAO;
    }

    public PaymentCard savePaymentCard(PaymentCard paymentCard) {
        return paymentCardDAO.saveAndFlush(paymentCard);
    }

    public PaymentCard retrievePaymentCardById(Long id) {
        return paymentCardDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found user by id = " + id));
    }

    @Transactional
    public PaymentCard updatePaymentCardById(Long id, PaymentCard paymentCard) {
        var cardById =
                paymentCardDAO.findById(id)
                        .orElseThrow(
                                () -> new EntityNotFoundException("Not found user by id = " + id));

        paymentCard.setId(cardById.getId());
        return paymentCardDAO.save(paymentCard);
    }

    public PaymentCard setStatusPaymentCard(Long id) {
        var cardById =
                paymentCardDAO.findById(id)
                        .orElseThrow(
                                () -> new EntityNotFoundException("Not found user by id = " + id));

        Boolean meaning = cardById.getActive();
        cardById.setActive(!meaning);
        return paymentCardDAO.save(cardById);
    }

    @Transactional
    public Boolean deletePaymentCardById(Long id) {
        return paymentCardDAO.deleteByPaymentCardId(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found user by id = " + id));
    }
}
