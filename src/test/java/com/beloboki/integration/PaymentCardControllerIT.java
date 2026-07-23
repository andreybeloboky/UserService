package com.beloboki.integration;

import com.beloboki.dao.PaymentCardDAO;
import com.beloboki.dao.UserDAO;
import com.beloboki.dto.PaymentCardRequest;
import com.beloboki.model.PaymentCard;
import com.beloboki.model.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.reactive.server.WebTestClient;

public class PaymentCardControllerIT extends AbstractIT {

    @Autowired private WebTestClient webTestClient;

    @Autowired private PaymentCardDAO paymentCardDAO;

    @Autowired private UserDAO userDAO;
    private User user;
    private PaymentCard paymentCard;

    @BeforeEach
    void setUp() {
        userDAO.deleteAll();
        paymentCardDAO.deleteAll();
        user =
                userDAO.save(
                        User.builder()
                                .name("Name")
                                .surname("Surname")
                                .birthDate(LocalDate.of(2000, Month.JULY, 21))
                                .email("test@gmail.com")
                                .active(false)
                                .build());
        paymentCard =
                paymentCardDAO.save(
                        PaymentCard.builder()
                                .holder("Name")
                                .expirationDate(LocalDateTime.of(2030, Month.JULY, 21, 0, 0))
                                .active(false)
                                .number("1234567330123456")
                                .user(user)
                                .build());
    }

    @Test
    void retrieveById_shouldReturnUserResponse() {
        webTestClient
                .get()
                .uri("/payment-cards/{id}", paymentCard.getId())
                .exchange()
                .expectBody()
                .json(
                        """
                        {
                          "id": %d,
                          "holder": "Name",
                          "number": "1234567330123456",
                          "expirationDate": "2030-07-21T00:00:00",
                          "active": false
                        }
                        """
                                .formatted(paymentCard.getId()),
                        JsonCompareMode.LENIENT);
    }

    @Test
    void retrieveFilterHolders_shouldReturnPageOfCards() {
        webTestClient
                .get()
                .uri(
                        uriBuilder ->
                                uriBuilder
                                        .path("/payment-cards")
                                        .queryParam("holder", "Name")
                                        .queryParam("pageNumber", 0)
                                        .queryParam("pageSize", 10)
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
                          "holder": "Name",
                          "number": "1234567330123456",
                          "expirationDate": "2030-07-21T00:00:00",
                          "active": false
                            }
                          ]
                        }
                        """
                                .formatted(paymentCard.getId()),
                        JsonCompareMode.LENIENT);
    }

    @Test
    void retrieveAllCardsByUserId_shouldReturnCardsList() {
        webTestClient
                .get()
                .uri("/users/{userId}/payment-cards", user.getId())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json(
                        """
                        [
                          {
                            "id": %d,
                          "holder": "Name",
                          "number": "1234567330123456",
                          "expirationDate": "2030-07-21T00:00:00",
                          "active": false
                          }
                        ]
                        """
                                .formatted(paymentCard.getId()),
                        JsonCompareMode.LENIENT);
    }

    @Test
    void save_shouldCreatePaymentCard() {
        PaymentCardRequest request = new PaymentCardRequest();
        request.setHolder("New Holder");
        request.setNumber("1234567890123456");
        request.setExpirationDate(LocalDateTime.of(2035, Month.DECEMBER, 1, 0, 0));
        request.setActive(true);

        webTestClient
                .post()
                .uri("/users/{userId}/payment-cards", user.getId())
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    void update_shouldModifyPaymentCard() {
        PaymentCardRequest request = new PaymentCardRequest();
        request.setHolder("Updated Holder");
        request.setNumber("1234567890123456");
        request.setExpirationDate(LocalDateTime.of(2030, Month.JULY, 21, 0, 0));
        request.setActive(true);

        webTestClient
                .put()
                .uri("/payment-cards/{id}", paymentCard.getId())
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void updateStatus_shouldChangeCardActiveStatus() {
        webTestClient
                .patch()
                .uri(
                        uriBuilder ->
                                uriBuilder
                                        .path("/payment-cards/{id}/status")
                                        .queryParam("status", false) // Меняем false на true
                                        .build(paymentCard.getId()))
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void deleteById_shouldRemoveCardFromDatabase() {
        webTestClient
                .delete()
                .uri("/payment-cards/{id}", paymentCard.getId())
                .exchange()
                .expectStatus()
                .isNoContent();

        Assertions.assertTrue(paymentCardDAO.findById(paymentCard.getId()).isPresent());
    }
}
