package com.beloboki.mapper;

import com.beloboki.dto.UserRequest;
import com.beloboki.dto.UserResponse;
import com.beloboki.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User userRequestToUser(UserRequest userRequest);

    UserResponse userToUserResponse(User user);
}
