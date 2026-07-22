package com.beloboki.service;

import com.beloboki.dao.PaymentCardDAO;
import com.beloboki.dao.UserDAO;
import com.beloboki.exception.CardLimitException;
import com.beloboki.model.PaymentCard;
import com.beloboki.model.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentCardServiceUnitTest {

    @Mock
    private PaymentCardDAO paymentCardDAO;

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private PaymentCardService paymentCardService;

    @Test
    void givenPaymentCardAndUserId_ShouldSavePaymentCard_WhenUserExists() {
        User userMock = User.builder().id(1L).name("Andrei").paymentCards(new ArrayList<>()).build();
        PaymentCard paymentCard = PaymentCard.builder().user(userMock).number("12345678222").build();

        when(userDAO.findById(1L)).thenReturn(Optional.of(userMock));

        when(userDAO.saveAndFlush(any(User.class))).thenReturn(userMock);

        paymentCardService.save(userMock.getId(), paymentCard);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userDAO).saveAndFlush(userCaptor.capture());
        User user = userCaptor.getValue(); // maybe it's unnecessary
        Assertions.assertNotNull(user.getPaymentCards());
        Assertions.assertEquals("12345678222", user.getPaymentCards().getFirst().getNumber());
        verify(userDAO, times(1)).saveAndFlush(user);
    }

    @Test
    void givenUserId_ShouldThrownException_WhenUserNotExist() {
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> paymentCardService.save(1L, PaymentCard.builder().number("111111111111").build()));
    }

    @Test
    void givenPaymentCardAndUserId_ShouldThrownExceptionPaymentCard_WhenUserHasLimit() {
        User userMock = User.builder().id(1L).name("Andrei").paymentCards(List.of(
                PaymentCard.builder().number("111111111111").build(),
                PaymentCard.builder().number("222222222222").build(),
                PaymentCard.builder().number("333333333333").build(),
                PaymentCard.builder().number("444444444444").build(),
                PaymentCard.builder().number("555555555555").build())).build();
        PaymentCard paymentCardLimit = PaymentCard.builder().user(userMock).number("666666666666").build();

        when(userDAO.findById(1L)).thenReturn(Optional.of(userMock));

        Assertions.assertThrows(CardLimitException.class, () -> paymentCardService.save(userMock.getId(), paymentCardLimit));
    }

    @Test
    void givenId_ShouldReturnCard_WhenCardExists() {
        when(paymentCardDAO.findById(1L)).thenReturn(Optional.of(PaymentCard.builder().id(1L).number("555555555555").build()));

        PaymentCard paymentCard = paymentCardService.retrieveById(1L);

        Assertions.assertNotNull(paymentCard);
        Assertions.assertEquals("555555555555", paymentCard.getNumber());
        verify(paymentCardDAO, times(1)).findById(1L);
    }

    @Test
    void givenCardId_ShouldThrownException_WhenCardNotExist() {
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> paymentCardService.retrieveById(1L));
        verify(paymentCardDAO, times(1)).findById(1L);
    }

    @Test
    void givenUserId_ShouldBackAllCards_WhenCardsExist() {
        List<PaymentCard> paymentCards = List.of(
                PaymentCard.builder().number("111111111111").build(),
                PaymentCard.builder().number("222222222222").build());

        when(paymentCardDAO.findAllCardByUserId(1L)).thenReturn(paymentCards);

        List<PaymentCard> retrieveAllCardsByUserId = paymentCardService.retrieveAllCardsByUserId(1L);

        Assertions.assertEquals(2, retrieveAllCardsByUserId.size());
        Assertions.assertEquals("111111111111", retrieveAllCardsByUserId.getFirst().getNumber());
        Assertions.assertEquals("222222222222", retrieveAllCardsByUserId.getLast().getNumber());
        verify(paymentCardDAO, times(1)).findAllCardByUserId(1L);
    }

    @Test
    void givenCard_ShouldUpdateCard_WhenCardExists() {
        when(paymentCardDAO.findById(1L))
                .thenReturn(Optional.of(PaymentCard.builder().number("111111111111").build()));

        paymentCardService.updateById(1L, PaymentCard.builder().number("222222222222").build());

        ArgumentCaptor<PaymentCard> userCaptor = ArgumentCaptor.forClass(PaymentCard.class);
        verify(paymentCardDAO).saveAndFlush(userCaptor.capture());
        PaymentCard paymentCard = userCaptor.getValue();
        Assertions.assertNotNull(paymentCard);
        Assertions.assertEquals("222222222222", paymentCard.getNumber());
        verify(paymentCardDAO, times(1)).saveAndFlush(paymentCard);
    }

    @Test
    void givenCard_ShouldThrowException_WhenCardToUpdateNotFound() {
        Assertions.assertThrows(
                EntityNotFoundException.class, () -> paymentCardService.updateById(2L,
                        PaymentCard.builder().number("111111111111").build()));
    }

    @Test
    void givenCardIdAndNewStatus_ShouldUpdateCardStatus_WhenCardExists() {
        when(paymentCardDAO.findById(1L)).thenReturn(Optional.of(PaymentCard.builder().id(1L).active(true).build()));

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
        Assertions.assertThrows(EntityNotFoundException.class, () -> paymentCardService.deleteById(1L));
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenFilterParameters_ShouldReturnList_WhenCardHasHolder() {
        List<PaymentCard> userList = List.of(PaymentCard.builder().holder("Test").build());
        Page<PaymentCard> userPageCreated = new PageImpl<>(userList);

        when(paymentCardDAO.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(userPageCreated);

        Page<PaymentCard> userPage = paymentCardService.retrieveFilterByHolder("Test", 0, 10);

        Assertions.assertNotNull(userPage);
        Assertions.assertEquals(userList.size(), userPage.getTotalElements());
        verify(paymentCardDAO, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }


    @Test
    void givenFilterParameters_ShouldReturnThrownException_WhenCardHaveNotHolder() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> paymentCardService.retrieveFilterByHolder("", 0, 10));
    }
}
