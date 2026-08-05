package com.beloboki.service;

import com.beloboki.config.CurrentUser;
import com.beloboki.dao.UserDAO;
import com.beloboki.dto.UserRequest;
import com.beloboki.dto.UserResponse;
import com.beloboki.mapper.UserMapper;
import com.beloboki.model.Role;
import com.beloboki.model.User;
import com.beloboki.specification.UserSpecifications;
import jakarta.persistence.EntityNotFoundException;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@CacheConfig(cacheNames = "users")
public class UserService {

    private final UserDAO userDAO;
    private final UserMapper userMapper;

    public UserResponse save(UserRequest userRequest) {
        User user = userDAO.saveAndFlush(userMapper.userRequestToUser(userRequest));
        return userMapper.userToUserResponse(user);
    }

    @Cacheable(key = "#id")
    @Transactional(readOnly = true)
    public UserResponse retrieveById(Long id, CurrentUser currentUser) {
        validate(currentUser.userId(), id, currentUser.role());
        User user = retrieveByUserId(id);
        return userMapper.userToUserResponse(user);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> retrieveAllUsers(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> users = userDAO.findAll(pageable);
        return users.map(userMapper::userToUserResponse);
    }

    @CacheEvict(key = "#id")
    @Transactional
    public void updateById(Long id, UserRequest userRequest, CurrentUser currentUser) {
        validate(currentUser.userId(), id, currentUser.role());
        User user = userMapper.userRequestToUser(userRequest);
        User userFromDB = retrieveByUserId(id);
        user.setId(userFromDB.getId());
        user.setPaymentCards(userFromDB.getPaymentCards());
        userDAO.saveAndFlush(user);
    }

    @CacheEvict(key = "#id")
    @Transactional
    public void updateStatus(Long id, Boolean status) {
        User userById = retrieveByUserId(id);
        userById.setActive(status);
        userDAO.saveAndFlush(userById);
    }

    @CacheEvict(key = "#id")
    @Transactional
    public void deleteById(Long id) {
        if (!userDAO.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: %s".formatted(id));
        }
        userDAO.deleteById(id);
    }

    public Page<UserResponse> retrieveFilterNameAndSurname(
            String name, String surname, int pageNumber, int pageSize) {
        Specification<User> spec =
                Specification.where(UserSpecifications.hasName(name))
                        .or(UserSpecifications.hasSurname(surname));

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> users = userDAO.findAll(spec, pageable);

        return users.map(userMapper::userToUserResponse);
    }

    public User retrieveByUserIdLocking(Long id) {
        return userDAO.findByUserId(id)
                .orElseThrow(
                        () ->
                                new EntityNotFoundException(
                                        "Not found user by id = %s".formatted(id)));
    }

    public UserResponse retrieveByEmail(CurrentUser currentUser, String email) {
        User userMail =
                userDAO.findByEmail(email)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "Not found user by id = %s".formatted(email)));

        validate(currentUser.userId(), userMail.getId(), currentUser.role());
        return userMapper.userToUserResponse(userMail);
    }

    private void validate(Long currentUser, Long userId, String role) {
        if (!Objects.equals(currentUser, userId)
                && (!Objects.equals(role, String.valueOf(Role.ADMIN)))) {
            throw new AuthorizationDeniedException("Access denied");
        }
    }

    private User retrieveByUserId(Long id) {
        return userDAO.findById(id)
                .orElseThrow(
                        () ->
                                new EntityNotFoundException(
                                        "Not found user by id = %s".formatted(id)));
    }
}
