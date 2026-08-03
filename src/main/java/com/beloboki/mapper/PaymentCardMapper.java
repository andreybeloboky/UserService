package com.beloboki.mapper;

import com.beloboki.dto.PaymentCardRequest;
import com.beloboki.dto.PaymentCardResponse;
import com.beloboki.model.PaymentCard;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentCardMapper {

    PaymentCardResponse cardToCardResponse(PaymentCard card);

    PaymentCard paymentCardRequestToPaymentCard(PaymentCardRequest paymentCardRequest);
}
