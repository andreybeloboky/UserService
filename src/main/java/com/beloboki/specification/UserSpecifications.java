package com.beloboki.specification;

import com.beloboki.model.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecifications {

    public static Specification<User> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                name == null || name.trim().isEmpty()
                        ? null
                        : criteriaBuilder.equal(root.get("name"), name);
    }

    public static Specification<User> hasSurname(String surname) {
        return (root, query, criteriaBuilder) ->
                (surname == null || surname.trim().isEmpty())
                        ? null
                        : criteriaBuilder.equal(root.get("surname"), surname);
    }

    private UserSpecifications() {}
}
