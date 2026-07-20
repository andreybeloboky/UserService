package com.beloboki.dao;

import com.beloboki.model.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentCardDAO extends JpaRepository<PaymentCard, Long>, JpaSpecificationExecutor<PaymentCard> {

    @Query(value = "DELETE FROM payment_cards p WHERE p.id = ?", nativeQuery = true)
    Optional<Boolean> deleteByPaymentCardId(@Param("id") Long id);
}
