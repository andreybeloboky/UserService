package com.beloboki.service;

import static org.mockito.Mockito.*;

import com.beloboki.config.CurrentUser;
import com.beloboki.dao.UserDAO;
import com.beloboki.dto.UserRequest;
import com.beloboki.dto.UserResponse;
import com.beloboki.mapper.UserMapper;
import com.beloboki.model.User;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
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
public class UserServiceUnitTest {

    @Mock private UserDAO userDAO;

    @Mock private UserMapper userMapper;

    @InjectMocks private UserService userService;

    private UserRequest userRequest;

    private UserResponse userResponse;

    private CurrentUser currentUser;

    @BeforeEach
    void setUp() {
        currentUser = new CurrentUser(1L, "Name", "USER");
        userRequest =
                new UserRequest(
                        "Name",
                        "Surname",
                        LocalDate.of(2000, Month.JULY, 21),
                        "test@gmail.com",
                        true);

        userResponse =
                new UserResponse(
                        1L,
                        "Name",
                        "Surname",
                        LocalDate.of(2000, Month.APRIL, 2),
                        "test@gmail.com",
                        false,
                        LocalDateTime.of(2026, Month.JULY, 22, 18, 43, 33),
                        LocalDateTime.of(2026, Month.JULY, 22, 18, 43, 33),
                        new ArrayList<>());
    }

    @Test
    void givenId_ShouldReturnUser_WhenUserExists() {
        User existingMock = User.builder().id(1L).name("Name").build();

        when(userDAO.findById(1L)).thenReturn(Optional.of(existingMock));
        when(userMapper.userToUserResponse(existingMock)).thenReturn(userResponse);

        UserResponse user = userService.retrieveById(1L, currentUser);

        Assertions.assertNotNull(user);
        Assertions.assertEquals("Name", user.name());
        verify(userDAO, times(1)).findById(1L);
    }

    @Test
    void givenId_ShouldThrowException_WhenUserDoesNotExist() {
        Assertions.assertThrows(
                AuthorizationDeniedException.class,
                () -> userService.retrieveById(99L, currentUser));
    }

    @Test
    void givenUser_ShouldSaveUser_WhenDataIsValid() {
        User userTest = User.builder().name("Andrei").build();
        when(userMapper.userRequestToUser(any(UserRequest.class))).thenReturn(userTest);

        userService.save(userRequest);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userDAO).saveAndFlush(userCaptor.capture());
        User user = userCaptor.getValue();
        Assertions.assertNotNull(user);
        Assertions.assertEquals("Andrei", user.getName());
        verify(userDAO, times(1)).saveAndFlush(user);
    }

    @Test
    void givenUser_ShouldUpdateUser_WhenUserExists() {
        User existingMock = User.builder().id(1L).name("Andrei").build();

        User updateUser = User.builder().name("Anton").build();

        when(userMapper.userRequestToUser(any(UserRequest.class))).thenReturn(updateUser);

        when(userDAO.findById(1L)).thenReturn(Optional.of(existingMock));

        userService.updateById(1L, userRequest, currentUser);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userDAO).saveAndFlush(userCaptor.capture());
        User user = userCaptor.getValue();
        Assertions.assertNotNull(user);
        Assertions.assertEquals("Anton", user.getName());
        verify(userDAO, times(1)).saveAndFlush(user);
    }

    @Test
    void givenUser_ShouldThrowException_WhenUserToUpdateNotFound() {
        Assertions.assertThrows(
                AuthorizationDeniedException.class,
                () -> userService.updateById(2L, userRequest, currentUser));
    }

    @Test
    void givenUserAndStatus_ShouldUpdateUserStatus_WhenUserExists() {
        User existingMock = User.builder().id(1L).name("Name").active(false).build();

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
        User user = User.builder().id(1L).name("Name").active(false).build();

        List<User> userList = List.of(user);
        Page<User> userPage = new PageImpl<>(userList);

        when(userDAO.findAll(any(Pageable.class))).thenReturn(userPage);
        when(userMapper.userToUserResponse(user)).thenReturn(userResponse);

        Page<UserResponse> users = userService.retrieveAllUsers(0, 10);
        Assertions.assertNotNull(users);
        Assertions.assertEquals(1, users.getTotalElements());

        List<UserResponse> usersList = users.stream().toList();
        Assertions.assertEquals("Name", usersList.getFirst().name());
        Assertions.assertEquals(false, usersList.getFirst().active());
        verify(userDAO, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void givenPageParameters_ShouldReturnEmptyPage_WhenNoUsersFound() {
        List<User> userList = List.of();
        Page<User> userPage = new PageImpl<>(userList);

        when(userDAO.findAll(any(Pageable.class))).thenReturn(userPage);

        Page<UserResponse> users = userService.retrieveAllUsers(0, 10);
        Assertions.assertNotNull(users);
        Assertions.assertEquals(0, users.getTotalElements());
        verify(userDAO, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @SuppressWarnings("unchecked")
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

        Page<UserResponse> userPage =
                userService.retrieveFilterNameAndSurname("Name", "Surname", 0, 10);

        Assertions.assertNotNull(userPage);
        Assertions.assertEquals(userList.size(), userPage.getTotalElements());
        verify(userDAO, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenFilterParameters_ShouldReturnList_WhenUserHasOnlyName() {
        List<User> userList = List.of(User.builder().id(1L).name("Name").active(false).build());
        Page<User> userPageCreated = new PageImpl<>(userList);

        when(userDAO.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(userPageCreated);

        Page<UserResponse> userPage = userService.retrieveFilterNameAndSurname("Name", "", 0, 10);

        Assertions.assertNotNull(userPage);
        Assertions.assertEquals(userList.size(), userPage.getTotalElements());
        verify(userDAO, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenFilterParameters_ShouldReturnList_WhenUserHasOnlySurname() {
        List<User> userList =
                List.of(User.builder().id(1L).surname("Surname").active(false).build());
        Page<User> userPageCreated = new PageImpl<>(userList);

        when(userDAO.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(userPageCreated);

        Page<UserResponse> userPage =
                userService.retrieveFilterNameAndSurname("", "Surname", 0, 10);

        Assertions.assertNotNull(userPage);
        Assertions.assertEquals(userList.size(), userPage.getTotalElements());
        verify(userDAO, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void givenFilterParameters_ShouldReturnException_WhenUserNotExists() {
        Assertions.assertThrows(
                NullPointerException.class,
                () -> userService.retrieveFilterNameAndSurname("", "", 0, 10));
    }

    @Test
    void givenUserId_ShouldThrowException_WhenUserNotFound() {
        when(userDAO.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(
                EntityNotFoundException.class, () -> userService.retrieveById(1L, currentUser));
    }
}
