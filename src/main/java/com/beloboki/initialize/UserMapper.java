package com.beloboki.initialize;

import com.beloboki.dto.UserRequest;
import com.beloboki.dto.UserResponse;
import com.beloboki.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = PaymentCardMapper.class)
public interface UserMapper {

    User userRequestToUser(UserRequest userRequest);

    @Mapping(target = "paymentCards", source = "paymentCards")
    UserResponse userToUserResponse(User user);
}
