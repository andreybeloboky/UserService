package com.beloboki.service;

import com.beloboki.dao.UserDAO;
import com.beloboki.dto.UserRequest;
import com.beloboki.dto.UserResponse;
import com.beloboki.mapper.UserMapper;
import com.beloboki.model.User;
import com.beloboki.specification.UserSpecifications;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@CacheConfig(cacheNames = "users")
public class UserService {

    private final UserDAO userDAO;
    private final UserMapper userMapper;

    public void save(UserRequest userRequest) {
        userDAO.saveAndFlush(userMapper.userRequestToUser(userRequest));
    }

    @Cacheable(key = "#id")
    @Transactional(readOnly = true)
    public UserResponse retrieveById(Long id) {
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
    public void updateById(Long id, UserRequest userRequest) {
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
        Specification<User> spec = null;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        if (name != null && !name.isBlank()) {
            spec = Specification.where(UserSpecifications.hasName(name));
        }

        if (surname != null && !surname.isBlank()) {
            if (spec == null) {
                spec = Specification.where(UserSpecifications.hasSurname(surname));
            } else {
                spec = spec.or(UserSpecifications.hasSurname(surname));
            }
        }

        if (spec == null) {
            Page<User> users = userDAO.findAll(pageable);
            return users.map(userMapper::userToUserResponse);
        }
        Page<User> users = userDAO.findAll(spec, pageable);
        return users.map(userMapper::userToUserResponse);
    }

    public User retrieveByUserIdLocking(Long id) {
        return userDAO.findByUserId(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found user by id = %s".formatted(id)));
    }

    private User retrieveByUserId(Long id) {
        return userDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found user by id = %s".formatted(id)));
    }
}
