package com.beloboki.service;

import static org.mockito.Mockito.*;

import com.beloboki.config.CurrentUser;
import com.beloboki.dao.PaymentCardDAO;
import com.beloboki.dto.PaymentCardRequest;
import com.beloboki.dto.PaymentCardResponse;
import com.beloboki.exception.CardLimitException;
import com.beloboki.mapper.PaymentCardMapper;
import com.beloboki.model.PaymentCard;
import com.beloboki.model.User;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authorization.AuthorizationDeniedException;

@ExtendWith(MockitoExtension.class)
public class PaymentCardServiceUnitTest {

    @Mock private PaymentCardDAO paymentCardDAO;

    @Mock private UserService userService;

    @Mock private PaymentCardMapper paymentCardMapper;

    @InjectMocks private PaymentCardService paymentCardService;

    private PaymentCardRequest paymentCardRequest;

    private PaymentCardResponse paymentCardResponse;

    private CurrentUser currentUser;

    @BeforeEach
    void setUp() {
        currentUser = new CurrentUser(1L, "Name", "USER");

        paymentCardRequest =
                new PaymentCardRequest(
                        "Holder",
                        "123456789123",
                        LocalDateTime.of(2030, Month.JULY, 21, 0, 0),
                        false);

        paymentCardResponse =
                new PaymentCardResponse(
                        1L,
                        "Name",
                        "1234567891234",
                        LocalDateTime.of(2030, Month.MAY, 5, 0, 0),
                        false,
                        LocalDateTime.of(2026, Month.JULY, 22, 18, 43, 33),
                        LocalDateTime.of(2026, Month.JULY, 22, 18, 43, 33));
    }

    @Test
    void givenPaymentCardAndUserId_ShouldSavePaymentCard_WhenUserExists() {
        User userMock =
                User.builder().id(1L).name("Andrei").paymentCards(new ArrayList<>()).build();
        PaymentCard paymentCard =
                PaymentCard.builder().user(userMock).number("12345678222").build();

        when(paymentCardMapper.paymentCardRequestToPaymentCard(any(PaymentCardRequest.class)))
                .thenReturn(paymentCard);
        when(userService.retrieveByUserIdLocking(1L)).thenReturn(userMock);

        paymentCardService.save(userMock.getId(), paymentCardRequest, currentUser);
        ArgumentCaptor<PaymentCard> paymentCardArgumentCaptor =
                ArgumentCaptor.forClass(PaymentCard.class);

        verify(paymentCardDAO).saveAndFlush(paymentCardArgumentCaptor.capture());
        PaymentCard paymentCardCaptured = paymentCardArgumentCaptor.getValue();
        Assertions.assertNotNull(paymentCardCaptured);
        Assertions.assertEquals("12345678222", paymentCardCaptured.getNumber());
        verify(paymentCardDAO, times(1)).saveAndFlush(paymentCard);
    }

    @Test
    void givenUserId_ShouldThrownException_WhenUserNotExist() {
        PaymentCard paymentCard = PaymentCard.builder().number("111111111111").build();
        when(paymentCardMapper.paymentCardRequestToPaymentCard(any(PaymentCardRequest.class)))
                .thenReturn(paymentCard);
        when(userService.retrieveByUserIdLocking(1L)).thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> paymentCardService.save(1L, paymentCardRequest, currentUser));
    }

    @Test
    void givenPaymentCardAndUserId_ShouldThrownExceptionPaymentCard_WhenUserHasLimit() {
        User userMock =
                User.builder()
                        .id(1L)
                        .name("Andrei")
                        .paymentCards(
                                List.of(
                                        PaymentCard.builder().number("111111111111").build(),
                                        PaymentCard.builder().number("222222222222").build(),
                                        PaymentCard.builder().number("333333333333").build(),
                                        PaymentCard.builder().number("444444444444").build(),
                                        PaymentCard.builder().number("555555555555").build()))
                        .build();
        PaymentCard paymentCardLimit =
                PaymentCard.builder().user(userMock).number("666666666666").build();

        when(paymentCardMapper.paymentCardRequestToPaymentCard(any(PaymentCardRequest.class)))
                .thenReturn(paymentCardLimit);
        when(userService.retrieveByUserIdLocking(1L)).thenReturn(userMock);

        Assertions.assertThrows(
                CardLimitException.class,
                () -> paymentCardService.save(userMock.getId(), paymentCardRequest, currentUser));
    }

    @Test
    void givenId_ShouldReturnCard_WhenCardExists() {
        User user = new User();
        user.setId(1L);
        PaymentCard mockCard =
                PaymentCard.builder().id(1L).number("1234567891234").user(user).build();

        when(paymentCardDAO.findById(1L)).thenReturn(Optional.of(mockCard));
        when(paymentCardMapper.cardToCardResponse(any(PaymentCard.class)))
                .thenReturn(paymentCardResponse);

        PaymentCardResponse paymentCard = paymentCardService.retrieveById(1L, currentUser);

        Assertions.assertNotNull(paymentCard);
        Assertions.assertEquals("1234567891234", paymentCard.number());
        verify(paymentCardDAO, times(1)).findById(1L);
    }

    @Test
    void givenUserId_ShouldBackAllCards_WhenCardsExist() {
        PaymentCard first = PaymentCard.builder().number("1234567891234").build();
        List<PaymentCard> paymentCards = List.of(first);
        when(paymentCardDAO.findAllCardByUserId(1L)).thenReturn(paymentCards);

        when(paymentCardMapper.cardToCardResponse(first)).thenReturn(paymentCardResponse);

        List<PaymentCardResponse> retrieveAllCardsByUserId =
                paymentCardService.retrieveAllCardsByUserId(1L, currentUser);

        Assertions.assertEquals(1, retrieveAllCardsByUserId.size());
        Assertions.assertEquals("1234567891234", retrieveAllCardsByUserId.getFirst().number());
        verify(paymentCardDAO, times(1)).findAllCardByUserId(1L);
    }

    @Test
    void givenCard_ShouldUpdateCard_WhenCardExists() {
        PaymentCard paymentCard = PaymentCard.builder().number("222222222222").build();

        when(paymentCardMapper.paymentCardRequestToPaymentCard(any(PaymentCardRequest.class)))
                .thenReturn(paymentCard);
        when(paymentCardDAO.findById(1L))
                .thenReturn(Optional.of(PaymentCard.builder().number("111111111111").build()));

        paymentCardService.updateById(1L, paymentCardRequest);

        ArgumentCaptor<PaymentCard> userCaptor = ArgumentCaptor.forClass(PaymentCard.class);
        verify(paymentCardDAO).saveAndFlush(userCaptor.capture());
        PaymentCard paymentCardCapture = userCaptor.getValue();
        Assertions.assertNotNull(paymentCard);
        Assertions.assertEquals("222222222222", paymentCard.getNumber());
        verify(paymentCardDAO, times(1)).saveAndFlush(paymentCardCapture);
    }

    @Test
    void givenCard_ShouldThrowException_WhenCardToUpdateNotFound() {
        PaymentCard paymentCard = PaymentCard.builder().number("111111111111").build();
        when(paymentCardMapper.paymentCardRequestToPaymentCard(any(PaymentCardRequest.class)))
                .thenReturn(paymentCard);
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> paymentCardService.updateById(2L, paymentCardRequest));
    }

    @Test
    void givenCardIdAndNewStatus_ShouldUpdateCardStatus_WhenCardExists() {
        when(paymentCardDAO.findById(1L))
                .thenReturn(Optional.of(PaymentCard.builder().id(1L).active(true).build()));

        paymentCardService.updateStatus(1L, false);

        ArgumentCaptor<PaymentCard> userCaptor = ArgumentCaptor.forClass(PaymentCard.class);
        verify(paymentCardDAO).saveAndFlush(userCaptor.capture());
        PaymentCard paymentCard = userCaptor.getValue();
        Assertions.assertNotNull(paymentCard);
        Assertions.assertEquals(false, paymentCard.getActive());
        verify(paymentCardDAO, times(1)).saveAndFlush(paymentCard);
    }

    @Test
    void givenCardId_ShouldDeleteCard_WhenCardExists() {
        when(paymentCardDAO.existsById(1L)).thenReturn(true);
        paymentCardService.deleteById(1L);
        verify(paymentCardDAO, times(1)).deleteById(1L);
    }

    @Test
    void givenCardId_ShouldThrowException_WhenCardToDeleteNotFound() {
        when(paymentCardDAO.existsById(1L)).thenReturn(false);
        Assertions.assertThrows(
                EntityNotFoundException.class, () -> paymentCardService.deleteById(1L));
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenFilterParameters_ShouldReturnList_WhenCardHasHolder() {
        List<PaymentCard> userList = List.of(PaymentCard.builder().holder("Test").build());
        Page<PaymentCard> userPageCreated = new PageImpl<>(userList);

        when(paymentCardDAO.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(userPageCreated);

        Page<PaymentCardResponse> userPage =
                paymentCardService.retrieveFilterByHolder("Test", 0, 10);

        Assertions.assertNotNull(userPage);
        Assertions.assertEquals(userList.size(), userPage.getTotalElements());
        verify(paymentCardDAO, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void givenFilterParameters_ShouldReturnThrownException_WhenCardHaveNotHolder() {
        Assertions.assertThrows(
                NullPointerException.class,
                () -> paymentCardService.retrieveFilterByHolder("", 0, 10));
    }

    @Test
    void givenWrongIdAndToken_ShouldReturnThrownException_WhenValidateNotMatch() {
        CurrentUser currentMock = new CurrentUser(999L, "TEST", "TEST");

        Assertions.assertThrows(
                AuthorizationDeniedException.class,
                () -> paymentCardService.retrieveById(1L, currentMock));
    }
}
