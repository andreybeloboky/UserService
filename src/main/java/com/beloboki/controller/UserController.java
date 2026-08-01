package com.beloboki.controller;

import com.beloboki.config.CurrentUser;
import com.beloboki.dto.UserRequest;
import com.beloboki.dto.UserResponse;
import com.beloboki.model.Role;
import com.beloboki.service.UserService;
import jakarta.validation.Valid;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserResponse>> retrieveFilterNameAndSurnameUsers(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "surname", required = false) String surname,
            @RequestParam(value = "page") Integer pageNumber,
            @RequestParam(value = "size") Integer pageSize) {
        if ((name != null && !name.isBlank()) || (surname != null && !surname.isBlank())) {
            return ResponseEntity.ok()
                    .body(
                            userService.retrieveFilterNameAndSurname(
                                    name, surname, pageNumber, pageSize));
        } else {
            return ResponseEntity.ok().body(userService.retrieveAllUsers(pageNumber, pageSize));
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> retrieveById(
            @AuthenticationPrincipal CurrentUser currentUser, @PathVariable("id") Long userId) {
        validate(currentUser.userId(), userId, currentUser.role());
        return ResponseEntity.ok().body(userService.retrieveById(userId));
    }

    @PostMapping
    public ResponseEntity<UserResponse> save(@Valid @RequestBody UserRequest userRequest) {
        UserResponse response = userService.save(userRequest);
        log.info("New user was successfully saved to the database");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable("id") Long userId,
            @Valid @RequestBody UserRequest userRequest) {
        validate(currentUser.userId(), userId, currentUser.role());
        userService.updateById(userId, userRequest);
        log.info("User with ID {} was successfully updated", userId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable("id") Long userId,
            @RequestParam("status") Boolean status) {
        validate(currentUser.userId(), userId, currentUser.role());
        userService.updateStatus(userId, status);
        log.info("Status for user ID {} was successfully changed", userId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @AuthenticationPrincipal CurrentUser currentUser, @PathVariable("id") Long userId) {
        validate(currentUser.userId(), userId, currentUser.role());
        userService.deleteById(userId);
        log.info("User with ID {} was successfully deleted", userId);
        return ResponseEntity.noContent().build();
    }

    private void validate(Long currentUser, Long userId, String role) {
        if (!Objects.equals(currentUser, userId)
                || (Objects.equals(role, String.valueOf(Role.ADMIN)))) {
            throw new AuthorizationDeniedException("Access denied");
        }
    }
}
