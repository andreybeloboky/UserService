package com.beloboki.integration;

import com.beloboki.dao.UserDAO;
import com.beloboki.model.User;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.reactive.server.WebTestClient;

public class UserControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired private WebTestClient webTestClient;

    @Autowired private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        userDAO.deleteAll();
    }

    @Test
    void given() {
        userDAO.save(
                User.builder()
                        .name("Name")
                        .surname("Surname")
                        .birthDate(LocalDate.now())
                        .email("siarh.miashkou@mail.ru")
                        .active(false)
                        .build());

        webTestClient
                .get()
                .uri("/users/{id}", 1)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json(
                        """
                        {
                          "active": false,
                          "birthDate": "2026-07-22",
                          "email": "siarh.miashkou@mail.ru",
                          "id": 1,
                          "name": "Name",
                          "paymentCards": [],
                          "surname": "Surname"
                        }
                        """,
                        JsonCompareMode.LENIENT);
    }
}
