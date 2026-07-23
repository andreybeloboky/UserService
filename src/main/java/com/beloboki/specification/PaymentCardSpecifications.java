package com.beloboki.specification;

import com.beloboki.model.PaymentCard;
import org.springframework.data.jpa.domain.Specification;

public class PaymentCardSpecifications {

    public static Specification<PaymentCard> hasHolder(String holder) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("holder"), holder));
    }

    private PaymentCardSpecifications() {}
}
