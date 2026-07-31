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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

@WithMockUser(
        username = "user",
        authorities = {"ADMIN"})
public class PaymentCardControllerIT extends AbstractIT {

    @Autowired private WebTestClient webTestClient;

    @Autowired private PaymentCardDAO paymentCardDAO;

    @Autowired private UserDAO userDAO;

    private User user;

    private PaymentCard paymentCard;

    private static final String USER_NAME = "Name";
    private static final String USER_SURNAME = "Surname";
    private static final String USER_EMAIL = "test@gmail.com";
    private static final LocalDate USER_BIRTH_DATE = LocalDate.of(2000, Month.JULY, 21);
    private static final String PAYMENT_CARD_HOLDER = "Name";
    private static final LocalDateTime EXPIRATION_DATE =
            LocalDateTime.of(2030, Month.JULY, 21, 0, 0);
    private static final String CARD_NUMBER = "1234567330123456";

    @BeforeEach
    void setUp() {
        userDAO.deleteAll();
        paymentCardDAO.deleteAll();
        user =
                userDAO.save(
                        User.builder()
                                .name(USER_NAME)
                                .surname(USER_SURNAME)
                                .birthDate(USER_BIRTH_DATE)
                                .email(USER_EMAIL)
                                .active(false)
                                .build());
        paymentCard =
                paymentCardDAO.save(
                        PaymentCard.builder()
                                .holder(PAYMENT_CARD_HOLDER)
                                .expirationDate(EXPIRATION_DATE)
                                .active(false)
                                .number(CARD_NUMBER)
                                .user(user)
                                .build());
    }

    @Test
    void retrieveById_shouldReturnCardResponse() {
        webTestClient
                .get()
                .uri("/api/payment-cards/{id}", paymentCard.getId())
                .exchange()
                .expectBody()
                .jsonPath("$.id")
                .isEqualTo(paymentCard.getId())
                .jsonPath("$.holder")
                .isEqualTo(PAYMENT_CARD_HOLDER)
                .jsonPath("$.number")
                .isEqualTo(CARD_NUMBER)
                .jsonPath("$.expirationDate")
                .isEqualTo(EXPIRATION_DATE)
                .jsonPath("$.active")
                .isEqualTo(false);
    }

    @Test
    void retrieveFilterHolders_shouldReturnPageOfCards() {
        webTestClient
                .get()
                .uri(
                        uriBuilder ->
                                uriBuilder
                                        .path("/api/payment-cards")
                                        .queryParam("holder", PAYMENT_CARD_HOLDER)
                                        .queryParam("pageNumber", 0)
                                        .queryParam("pageSize", 10)
                                        .build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.content[0].id")
                .isEqualTo(paymentCard.getId())
                .jsonPath("$.content[0].holder")
                .isEqualTo(PAYMENT_CARD_HOLDER)
                .jsonPath("$.content[0].expirationDate")
                .isEqualTo(EXPIRATION_DATE)
                .jsonPath("$.content[0].active")
                .isEqualTo(false);
    }

    @Test
    void retrieveAllCardsByUserId_shouldReturnCardsList() {
        webTestClient
                .get()
                .uri("/api/users/{userId}/payment-cards", user.getId())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.length()")
                .isEqualTo(1)
                .jsonPath("$.[0].id")
                .isEqualTo(paymentCard.getId())
                .jsonPath("$.[0].holder")
                .isEqualTo(PAYMENT_CARD_HOLDER)
                .jsonPath("$.[0].number")
                .isEqualTo(CARD_NUMBER);
    }

    @Test
    void save_shouldCreatePaymentCard() {
        PaymentCardRequest request =
                new PaymentCardRequest(PAYMENT_CARD_HOLDER, CARD_NUMBER, EXPIRATION_DATE, true);

        webTestClient
                .post()
                .uri("/api/users/{userId}/payment-cards", user.getId())
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated();

        var paymentCard = paymentCardDAO.findAll();
        Assertions.assertEquals(PAYMENT_CARD_HOLDER, paymentCard.getFirst().getHolder());
    }

    @Test
    void update_shouldModifyPaymentCard() {
        PaymentCardRequest request =
                new PaymentCardRequest(PAYMENT_CARD_HOLDER, CARD_NUMBER, EXPIRATION_DATE, true);

        webTestClient
                .put()
                .uri("/api/payment-cards/{id}", paymentCard.getId())
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk();

        var paymentCard = paymentCardDAO.findAll();
        Assertions.assertEquals(PAYMENT_CARD_HOLDER, paymentCard.getFirst().getHolder());
    }

    @Test
    void updateStatus_shouldChangeCardActiveStatus() {
        webTestClient
                .patch()
                .uri(
                        uriBuilder ->
                                uriBuilder
                                        .path("/api/payment-cards/{id}/status")
                                        .queryParam("status", true)
                                        .build(paymentCard.getId()))
                .exchange()
                .expectStatus()
                .isOk();

        var paymentCard = paymentCardDAO.findAll();
        Assertions.assertEquals(true, paymentCard.getFirst().getActive());
    }

    @Test
    void deleteById_shouldRemoveCardFromDatabase() {
        webTestClient
                .delete()
                .uri("/api/payment-cards/{id}", paymentCard.getId())
                .exchange()
                .expectStatus()
                .isNoContent();

        Assertions.assertFalse(paymentCardDAO.findById(paymentCard.getId()).isPresent());
    }
}
