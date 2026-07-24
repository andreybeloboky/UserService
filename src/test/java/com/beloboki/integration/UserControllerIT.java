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
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.reactive.server.WebTestClient;

public class UserControllerIT extends AbstractIT {

    @Autowired private WebTestClient webTestClient;

    @Autowired private UserDAO userDAO;

    private User user;

    @BeforeEach
    void setUp() {
        userDAO.deleteAll();
        user =
                userDAO.save(
                        User.builder()
                                .name("Name")
                                .surname("Surname")
                                .birthDate(LocalDate.of(2000, Month.JULY, 21))
                                .email("test@gmail.com")
                                .active(false)
                                .build());
    }

    @Test
    void retrieveById_shouldReturnUserResponse() {
        webTestClient
                .get()
                .uri("/users/{id}", user.getId())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json(
                        """
                        {
                          "id": %d,
                          "name": "Name",
                          "surname": "Surname",
                          "email": "test@gmail.com",
                          "active": false,
                          "birthDate": "2000-07-21",
                          "paymentCards": []
                        }
                        """
                                .formatted(user.getId()),
                        JsonCompareMode.LENIENT);
    }

    @Test
    void retrieveAllUsers_shouldReturnPage() {
        webTestClient
                .get()
                .uri(
                        uriBuilder ->
                                uriBuilder
                                        .path("/users")
                                        .queryParam("page", 0)
                                        .queryParam("size", 10)
                                        .build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json(
                        """
                        {
                          "content": [
                            {
                                  "id": %d,
                                  "name": "Name",
                                  "surname": "Surname",
                                  "email": "test@gmail.com",
                                  "active": false,
                                  "birthDate": "2000-07-21",
                                  "paymentCards": []
                            }
                          ]
                        }
                        """
                                .formatted(user.getId()));
    }

    @Test
    void save_shouldCreateUser() {
        UserRequest request = new UserRequest();
        request.setName("Name");
        request.setSurname("Surname");
        request.setBirthDate(LocalDate.of(2000, Month.JULY, 21));
        request.setEmail("second@gmail.com");
        request.setActive(true);

        webTestClient.post().uri("/users").bodyValue(request).exchange().expectStatus().isCreated();

        var users = userDAO.findAll();
        Assertions.assertEquals(2, users.size());
        Assertions.assertEquals("second@gmail.com", users.getLast().getEmail());
        Assertions.assertEquals("test@gmail.com", users.getFirst().getEmail());
    }

    @Test
    void update_shouldModifyUser() {
        UserRequest request = new UserRequest();
        request.setName("New");
        request.setSurname("New");
        request.setBirthDate(LocalDate.of(2000, Month.JULY, 21));
        request.setEmail("new@mail.com");
        request.setActive(false);

        webTestClient
                .put()
                .uri("/users/{id}", user.getId())
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk();

        User updated = userDAO.findById(user.getId()).orElseThrow();
        Assertions.assertEquals("New", updated.getName());
        Assertions.assertEquals("New", updated.getSurname());
        Assertions.assertEquals(LocalDate.of(2000, Month.JULY, 21), updated.getBirthDate());
        Assertions.assertEquals("new@mail.com", updated.getEmail());
        Assertions.assertEquals(false, updated.getActive());
    }

    @Test
    void updateStatus_shouldChangeActiveFlag() {
        webTestClient
                .patch()
                .uri(
                        uriBuilder ->
                                uriBuilder
                                        .path("/users/{id}/status")
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
                .uri("/users/{id}", user.getId())
                .exchange()
                .expectStatus()
                .isNoContent();

        Assertions.assertFalse(userDAO.findById(user.getId()).isPresent());
    }
}
