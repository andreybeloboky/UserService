package com.beloboki.initialize;

import com.beloboki.model.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecifications {

    public static Specification<User> hasName(String name) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("name"), name));
    }

    public static Specification<User> hasSurname(String surname) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("surname"), surname));
    }
}
