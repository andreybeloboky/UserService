package com.beloboki.dao;

import com.beloboki.model.PaymentCard;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentCardDAO
        extends JpaRepository<PaymentCard, Long>, JpaSpecificationExecutor<PaymentCard> {

    @Query("SELECT p FROM PaymentCard p WHERE p.user = :id")
    List<PaymentCard> findAllCardByUserId(@Param("user_id") Long id);
}
