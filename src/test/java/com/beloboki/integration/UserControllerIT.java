package com.beloboki.integration;

import com.beloboki.dao.UserDAO;
import com.beloboki.dto.UserRequest;
import com.beloboki.model.User;
import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

public class UserControllerIT extends AbstractIT {

    @Autowired private WebTestClient webTestClient;

    @Autowired private UserDAO userDAO;

    private User user;

    private static final String NAME = "Name";
    private static final String SURNAME = "Surname";
    private static final String EMAIL = "test@gmail.com";
    private static final LocalDate BIRTH_DATE = LocalDate.of(2000, Month.JULY, 21);
    private static final String SECOND_EMAIL = "second@gmail.com";
    private static final String UPGRADE_EMAIL = "new@gmail.com";

    @BeforeEach
    void setUp() {
        userDAO.deleteAll();
        user =
                userDAO.save(
                        User.builder()
                                .name(NAME)
                                .surname(SURNAME)
                                .birthDate(BIRTH_DATE)
                                .email(EMAIL)
                                .active(false)
                                .build());
    }

    @Test
    void retrieveById_shouldReturnUserResponse() {
        webTestClient
                .get()
                .uri("/api/users/{id}", user.getId())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(user.getId())
                .jsonPath("$.name").isEqualTo(NAME)
                .jsonPath("$.surname").isEqualTo(SURNAME)
                .jsonPath("$.email").isEqualTo(EMAIL)
                .jsonPath("$.active").isEqualTo(false)
                .jsonPath("$.birthDate").isEqualTo(BIRTH_DATE)
                .jsonPath("$.paymentCards").isArray()
                .jsonPath("$.paymentCards.length()").isEqualTo(0);
    }

    @Test
    void retrieveAllUsers_shouldReturnPage() {
        webTestClient
                .get()
                .uri(
                        uriBuilder ->
                                uriBuilder
                                        .path("/api/users")
                                        .queryParam("page", 0)
                                        .queryParam("size", 10)
                                        .build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.content.length()").isEqualTo(1)
                .jsonPath("$.content[0].id").isEqualTo(user.getId())
                .jsonPath("$.content[0].name").isEqualTo(NAME)
                .jsonPath("$.content[0].surname").isEqualTo(SURNAME)
                .jsonPath("$.content[0].email").isEqualTo(EMAIL)
                .jsonPath("$.content[0].active").isEqualTo(false)
                .jsonPath("$.content[0].birthDate").isEqualTo(BIRTH_DATE)
                .jsonPath("$.content[0].paymentCards").isArray()
                .jsonPath("$.content[0].paymentCards.length()").isEqualTo(0);
    }

    @Test
    void save_shouldCreateUser() {
        UserRequest request =
                new UserRequest(NAME, SURNAME,
                        LocalDate.of(2000, Month.JULY, 21),
                        SECOND_EMAIL,
                        true);

        webTestClient
                .post()
                .uri("/api/users")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated();

        var users = userDAO.findAll();
        Assertions.assertEquals(2, users.size());
        Assertions.assertEquals(SECOND_EMAIL, users.getLast().getEmail());
        Assertions.assertEquals(EMAIL, users.getFirst().getEmail());
    }

    @Test
    void update_shouldModifyUser() {
        UserRequest request =
                new UserRequest(
                        NAME, SURNAME, BIRTH_DATE, UPGRADE_EMAIL, true);

        webTestClient
                .put()
                .uri("/api/users/{id}", user.getId())
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk();

        User updated = userDAO.findById(user.getId()).orElseThrow();
        Assertions.assertEquals(NAME, updated.getName());
        Assertions.assertEquals(SURNAME, updated.getSurname());
        Assertions.assertEquals(BIRTH_DATE, updated.getBirthDate());
        Assertions.assertEquals(UPGRADE_EMAIL, updated.getEmail());
        Assertions.assertEquals(true, updated.getActive());
    }

    @Test
    void updateStatus_shouldChangeActiveFlag() {
        webTestClient
                .patch()
                .uri(uriBuilder -> uriBuilder.path("/api/users/{id}/status")
                        .queryParam("status", true)
                        .build(user.getId()))
                .exchange()
                .expectStatus()
                .isOk();

        User updated = userDAO.findById(user.getId()).orElseThrow();
        Assertions.assertTrue(updated.getActive());
    }

    @Test
    void deleteById_shouldRemoveUser() {
        webTestClient
                .delete()
                .uri("/api/users/{id}", user.getId())
                .exchange()
                .expectStatus()
                .isNoContent();

        Assertions.assertFalse(userDAO.findById(user.getId()).isPresent());
    }
}
