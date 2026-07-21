package com.beloboki.mapper;

import com.beloboki.dto.PaymentCardRequest;
import com.beloboki.dto.PaymentCardResponse;
import com.beloboki.model.PaymentCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentCardMapper {

    PaymentCardResponse cardToCardResponse(PaymentCard card);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "id", ignore = true)
    PaymentCard paymentCardRequestToPaymentCard(PaymentCardRequest paymentCardRequest);
}
