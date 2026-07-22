package com.beloboki.service;

import com.beloboki.dao.UserDAO;
import com.beloboki.model.User;
import com.beloboki.specification.UserSpecifications;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@CacheConfig(cacheNames = "users")
public class UserService {

    private final UserDAO userDAO;

    @CachePut(key = "#result.id")
    public void save(User user) {
        userDAO.saveAndFlush(user);
    }

    @Cacheable(key = "#id")
    public User retrieveById(Long id) {
        return userDAO.findById(id)
                .orElseThrow(
                        () ->
                                new EntityNotFoundException(
                                        "Not found user by id = %s".formatted(id)));
    }

    @CachePut(key = "#id")
    public void updateById(Long id, User user) {
        user.setId(retrieveById(id).getId());
        userDAO.save(user);
    }

    @CachePut(key = "#id")
    public void updateStatus(Long id, Boolean status) {
        var userById = retrieveById(id);
        userById.setActive(status);
        userDAO.save(userById);
    }

    @CacheEvict(key = "#id")
    public void deleteById(Long id) {
        userDAO.deleteById(id);
    }

    public Page<User> retrieveAllUsers(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return userDAO.findAll(pageable);
    }

    public Page<User> retrieveFilterNameAndSurname(
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
            return userDAO.findAll(pageable);
        }
        return userDAO.findAll(spec, pageable);
    }
}
