package com.beloboki.controller;

import com.beloboki.dto.UserRequest;
import com.beloboki.dto.UserResponse;
import com.beloboki.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserResponse>> retrieveFilterNameAndSurnameUsers(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "surname", required = false) String surname,
            @RequestParam(value = "page") Integer pageNumber,
            @RequestParam(value = "size") Integer pageSize) {
        if ((name != null && !name.isBlank()) || (surname != null && !surname.isBlank())) {
            return ResponseEntity.ok()
                    .body(userService.retrieveFilterNameAndSurname(
                                    name, surname, pageNumber, pageSize));
        } else {
            return ResponseEntity.ok().body(userService.retrieveAllUsers(pageNumber, pageSize));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> retrieveById(@PathVariable("id") Long userId) {
        return ResponseEntity.ok().body(userService.retrieveById(userId));
    }

    @PostMapping
    public ResponseEntity<Void> save(@Valid @RequestBody UserRequest userRequest) {
        userService.save(userRequest);
        log.info("New user was successfully saved to the database");
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
            @PathVariable("id") Long id, @Valid @RequestBody UserRequest userRequest) {
        userService.updateById(id, userRequest);
        log.info("User with ID {} was successfully updated", id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable("id") Long userId, @RequestParam("status") Boolean status) {
        userService.updateStatus(userId, status);
        log.info("Status for user ID {} was successfully changed", userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long userId) {
        userService.deleteById(userId);
        log.info("User with ID {} was successfully deleted", userId);
        return ResponseEntity.noContent().build();
    }
}
