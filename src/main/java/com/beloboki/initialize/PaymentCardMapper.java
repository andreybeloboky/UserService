package com.beloboki.initialize;

import com.beloboki.dto.PaymentCardRequest;
import com.beloboki.dto.PaymentCardResponse;
import com.beloboki.model.PaymentCard;
import org.mapstruct.Mapper;

@Mapper
public interface PaymentCardMapper {

    PaymentCardResponse cardToCardResponse(PaymentCard card);

    PaymentCard paymentCardRequestToPaymentCard(PaymentCardRequest paymentCardRequest);
}
