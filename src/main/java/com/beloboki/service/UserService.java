package com.beloboki.service;

import com.beloboki.dao.UserDAO;
import com.beloboki.model.User;
import com.beloboki.specification.UserSpecifications;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserDAO userDAO;

    public User save(User user) {
        return userDAO.saveAndFlush(user);
    }

    public List<User> retrieveAllUsers() {
        return userDAO.findAll();
    }

    public User retrieveById(Long id) {
        return userDAO.findById(id)
                .orElseThrow(
                        () ->
                                new EntityNotFoundException(
                                        "Not found user by id = %s".formatted(id)));
    }

    public Page<User> retrieveFilterByNameAndSurname(
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

    public User updateById(Long id, User user) {
        user.setId(retrieveById(id).getId());
        return userDAO.save(user);
    }

    public User setStatus(Long id, Boolean status) {
        var userById = retrieveById(id);
        userById.setActive(status);
        return userDAO.save(userById);
    }

    public void deleteById(Long id) {
        userDAO.deleteById(id);
    }
}
