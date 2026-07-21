package com.beloboki;

import com.beloboki.dao.PaymentCardDAO;
import com.beloboki.dao.UserDAO;
import com.beloboki.model.PaymentCard;
import com.beloboki.model.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.*;

@DataJpaTest
class userDAOTest {

    @Autowired private UserDAO userDAO;
    @Autowired private PaymentCardDAO paymentCardDAO;

    @Test
    void givenValidUserWithCard_whenSave_thenIdsGenerated() {
        var user = new User();
        user.setName("Name");
        user.setSurname("Surname");
        user.setEmail("example@gmail.com");
        user.setActive(true);
        user.setBirthDate(LocalDate.now());
        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setUser(user);
        paymentCard.setActive(true);
        paymentCard.setHolder("Name Surname");
        paymentCard.setNumber("123456789123");
        paymentCard.setExpirationDate(LocalDateTime.now());
        user.setPaymentCards(List.of(paymentCard));

        var userSave = userDAO.saveAndFlush(user);

        Assertions.assertNotNull(userSave.getId());
        Assertions.assertNotNull(userSave.getPaymentCards().getFirst().getId());
    }

    @Test
    void givenSavedUser_whenUpdateNameAndCardNumber_thenChangesPersisted() {
        var user = new User();
        user.setName("Name");
        user.setSurname("Surname");
        user.setEmail("example@gmail.com");
        user.setActive(true);
        user.setBirthDate(LocalDate.now());
        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setUser(user);
        paymentCard.setActive(true);
        paymentCard.setHolder("Name Surname");
        paymentCard.setNumber("123456789124");
        paymentCard.setExpirationDate(LocalDateTime.now());
        user.setPaymentCards(List.of(paymentCard));

        var userSave = userDAO.saveAndFlush(user);

        userSave.setName("NameTwo");
        userSave.getPaymentCards().getFirst().setNumber("123456789123");

        var userUpdate = userDAO.saveAndFlush(userSave);

        Assertions.assertEquals("NameTwo", userUpdate.getName());
        Assertions.assertEquals(
                "123456789123", userUpdate.getPaymentCards().getFirst().getNumber());
    }

    @Test
    void givenUserInDb_whenFindByExample_thenUserFound() {
        var user = new User();
        user.setName("Name");
        user.setSurname("Surname");
        user.setEmail("example@gmail.com");
        user.setActive(true);
        user.setBirthDate(LocalDate.now());

        userDAO.save(user);

        var exampleUser = new User();
        exampleUser.setName("Name");
        exampleUser.setSurname("Surname");

        Pageable pageable = PageRequest.of(0, 5);
        Page<User> all = userDAO.findAll(Example.of(exampleUser), pageable);

        Assertions.assertEquals(1, all.getTotalElements());
    }

    @Test
    void givenUserInDb_whenFindByNameAndSurname_thenUserFound() {
        var user = new User();
        user.setName("Name");
        user.setSurname("Surname");
        user.setEmail("example@gmail.com");
        user.setActive(true);
        user.setBirthDate(LocalDate.now());

        userDAO.save(user);
        Pageable pageable =
                PageRequest.of(
                        0, 5, Sort.by("name").ascending().and(Sort.by("surname").ascending()));

        Page<User> all = userDAO.findAll(pageable);
        Assertions.assertEquals(1, all.getTotalElements());
    }

    @Test
    void givenUserInDb_whenFindAllCardsById_thenCardsFound() {
        var user = new User();
        user.setName("Name");
        user.setSurname("Surname");
        user.setEmail("example@gmail.com");
        user.setActive(true);
        user.setBirthDate(LocalDate.now());

        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setUser(user);
        paymentCard.setActive(true);
        paymentCard.setHolder("Name Surname");
        paymentCard.setNumber("123456789123");
        paymentCard.setExpirationDate(LocalDateTime.now());

        PaymentCard paymentCardTwo = new PaymentCard();
        paymentCardTwo.setUser(user);
        paymentCardTwo.setActive(true);
        paymentCardTwo.setHolder("Name Surname");
        paymentCardTwo.setNumber("123456789125");
        paymentCardTwo.setExpirationDate(LocalDateTime.now());
        user.setPaymentCards(List.of(paymentCard, paymentCardTwo));
        userDAO.saveAndFlush(user);

        var userTwo = new User();
        userTwo.setName("Name");
        userTwo.setSurname("Surname");
        userTwo.setEmail("example1@gmail.com");
        userTwo.setActive(true);
        userTwo.setBirthDate(LocalDate.now());

        PaymentCard paymentCardThree = new PaymentCard();
        paymentCardThree.setUser(userTwo);
        paymentCardThree.setActive(true);
        paymentCardThree.setHolder("Name Surname");
        paymentCardThree.setNumber("123456789123");
        paymentCardThree.setExpirationDate(LocalDateTime.now());
        userTwo.setPaymentCards(List.of(paymentCardThree));
        userDAO.saveAndFlush(userTwo);

        List<PaymentCard> paymentCards = paymentCardDAO.findAllCardByUserId(user.getId());

        Assertions.assertEquals(2, paymentCards.size());
    }
}
