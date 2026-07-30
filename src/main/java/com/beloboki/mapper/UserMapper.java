package com.beloboki.mapper;

import com.beloboki.dto.PaymentCardResponse;
import com.beloboki.dto.UserRequest;
import com.beloboki.dto.UserResponse;
import com.beloboki.model.User;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User userRequestToUser(UserRequest userRequest);

    @Mapping(target = "paymentCards", expression = "java(mapPaymentCards(user))")
    UserResponse userToUserResponse(User user);

    default List<PaymentCardResponse> mapPaymentCards(User user) {
        if (user.getPaymentCards() == null) {
            return List.of();
        }
        return user.getPaymentCards().stream()
                .map(
                        card ->
                                new PaymentCardResponse(
                                        card.getId(),
                                        card.getNumber(),
                                        card.getHolder(),
                                        card.getExpirationDate(),
                                        card.getActive(),
                                        card.getCreatedAt(),
                                        card.getUpdatedAt()))
                .toList();
    }
}
