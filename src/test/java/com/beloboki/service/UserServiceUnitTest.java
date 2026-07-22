package com.beloboki.service;

import static org.mockito.Mockito.*;

import com.beloboki.dao.UserDAO;
import com.beloboki.model.User;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
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

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {

    @Mock private UserDAO userDAO;

    @InjectMocks private UserService userService;

    @Test
    void givenId_ShouldReturnUser_WhenUserExists() {
        User existingMock = User.builder().id(1L).name("Andrei").build();

        when(userDAO.findById(1L)).thenReturn(Optional.of(existingMock));

        User user = userService.retrieveById(1L);

        Assertions.assertNotNull(user);
        Assertions.assertEquals("Andrei", user.getName());
        verify(userDAO, times(1)).findById(1L);
    }

    @Test
    void givenId_ShouldThrowException_WhenUserDoesNotExist() {
        when(userDAO.findById(99L)).thenReturn(Optional.empty());

        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> {
                    userService.retrieveById(99L);
                });
    }

    @Test
    void givenUser_ShouldSaveUser_WhenDataIsValid() {
        User existingMock = User.builder().id(1L).name("Andrei").build();
        userService.save(existingMock);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userDAO).saveAndFlush(userCaptor.capture());
        User user = userCaptor.getValue(); // maybe it's unnecessary
        Assertions.assertNotNull(user);
        Assertions.assertEquals("Andrei", user.getName());
        verify(userDAO, times(1)).saveAndFlush(userCaptor.capture());
    }

    @Test
    void givenUser_ShouldUpdateUser_WhenUserExists() {
        User existingMock = User.builder().id(1L).name("Andrei").build();

        User updateUser = User.builder().name("Anton").build();

        when(userDAO.findById(1L)).thenReturn(Optional.of(existingMock));

        userService.updateById(1L, updateUser);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userDAO).saveAndFlush(userCaptor.capture());
        User user = userCaptor.getValue();
        Assertions.assertNotNull(user);
        Assertions.assertEquals("Anton", user.getName());
        verify(userDAO, times(1)).saveAndFlush(user);
    }

    @Test
    void givenUser_ShouldThrowException_WhenUserToUpdateNotFound() {
        User existingMock = User.builder().id(1L).name("Andrei").build();

        Assertions.assertThrows(
                EntityNotFoundException.class, () -> userService.updateById(2L, existingMock));
    }

    @Test
    void givenUserAndStatus_ShouldUpdateUserStatus_WhenUserExists() {
        User existingMock = User.builder().id(1L).name("Andrei").active(false).build();

        when(userDAO.findById(1L)).thenReturn(Optional.of(existingMock));

        userService.updateStatus(1L, true);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userDAO).saveAndFlush(userCaptor.capture());
        User user = userCaptor.getValue();
        Assertions.assertNotNull(user);
        Assertions.assertEquals(true, user.getActive());
        verify(userDAO, times(1)).saveAndFlush(user);
    }

    @Test
    void givenUserId_ShouldDeleteUser_WhenUserExists() {
        when(userDAO.existsById(1L)).thenReturn(true);
        userService.deleteById(1L);
        verify(userDAO, times(1)).deleteById(1L);
    }

    @Test
    void givenUserId_ShouldThrowException_WhenUserToDeleteNotFound() {
        when(userDAO.existsById(1L)).thenReturn(false);
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.deleteById(1L));
    }

    @Test
    void givenPageParameters_ShouldReturnUserPage_WhenUsersExist() {
        List<User> userList =
                List.of(
                        User.builder().id(1L).name("Andrei").active(false).build(),
                        User.builder().id(2L).name("Anton").active(false).build());
        Page<User> userPage = new PageImpl<>(userList);

        when(userDAO.findAll(any(Pageable.class))).thenReturn(userPage);

        Page<User> users = userService.retrieveAllUsers(0, 10);
        Assertions.assertNotNull(users);
        Assertions.assertEquals(2, users.getTotalElements());

        List<User> usersList = users.stream().toList();
        Assertions.assertEquals("Andrei", usersList.getFirst().getName());
        Assertions.assertEquals(false, usersList.getFirst().getActive());
        Assertions.assertEquals("Anton", usersList.get(1).getName());
        Assertions.assertEquals(false, usersList.get(1).getActive());
    }

    @Test
    void givenPageParameters_ShouldReturnEmptyPage_WhenNoUsersFound() {
        List<User> userList = List.of();
        Page<User> userPage = new PageImpl<>(userList);

        when(userDAO.findAll(any(Pageable.class))).thenReturn(userPage);

        Page<User> users = userService.retrieveAllUsers(0, 10);
        Assertions.assertNotNull(users);
        Assertions.assertEquals(0, users.getTotalElements());
        verify(userDAO, times(1)).findAll(any(Pageable.class));
    }

    /* @ParameterizedTest
    @CsvSource(value = {"'Name','',0,10",
            "'','Surname',0,10",
            "'','',0,10",
            "'Name', 'Surname', 0,10"})
    void givenFilterParameters_ShouldReturnPageFilterUsers_WhenUserExists(String name, String surname,
                                                                          int page, int size) {

    }

     */

    @Test
    void givenFilterParameters_ShouldReturnPageFilterUsers_WhenUserExists() {
        List<User> userList =
                List.of(
                        User.builder().id(1L).name("Name").surname("Surname").active(false).build(),
                        User.builder()
                                .id(2L)
                                .name("Name")
                                .surname("Surname")
                                .active(false)
                                .build());
        Page<User> userPageCreated = new PageImpl<>(userList);
        when(userDAO.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(userPageCreated);

        Page<User> userPage = userService.retrieveFilterNameAndSurname("Name", "Surname", 0, 10);

        Assertions.assertNotNull(userPage);
        Assertions.assertEquals(userList.size(), userPage.getTotalElements());
        verify(userDAO, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void givenFilterParameters_ShouldReturnList_WhenUserHasOnlyName() {
        List<User> userList = List.of(User.builder().id(1L).name("Name").active(false).build());
        Page<User> userPageCreated = new PageImpl<>(userList);

        when(userDAO.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(userPageCreated);

        Page<User> userPage = userService.retrieveFilterNameAndSurname("Name", "", 0, 10);

        Assertions.assertNotNull(userPage);
        Assertions.assertEquals(userList.size(), userPage.getTotalElements());
        verify(userDAO, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void givenFilterParameters_ShouldReturnList_WhenUserHasOnlySurname() {
        List<User> userList =
                List.of(User.builder().id(1L).surname("Surname").active(false).build());
        Page<User> userPageCreated = new PageImpl<>(userList);

        when(userDAO.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(userPageCreated);

        Page<User> userPage = userService.retrieveFilterNameAndSurname("", "Surname", 0, 10);

        Assertions.assertNotNull(userPage);
        Assertions.assertEquals(userList.size(), userPage.getTotalElements());
        verify(userDAO, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void givenFilterParameters_ShouldReturnEmptyList_WhenUserNotExists() {
        List<User> userList = List.of();
        Page<User> userPageCreated = new PageImpl<>(userList);

        when(userDAO.findAll(any(Pageable.class))).thenReturn(userPageCreated);

        Page<User> userPage = userService.retrieveFilterNameAndSurname("", "", 0, 10);

        Assertions.assertNotNull(userPage);
        Assertions.assertEquals(0, userPage.getTotalElements());
        verify(userDAO, times(1)).findAll(any(Pageable.class));
    }
}
