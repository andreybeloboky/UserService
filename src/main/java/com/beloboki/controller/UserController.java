package com.beloboki.controller;

import com.beloboki.dto.UserRequest;
import com.beloboki.dto.UserResponse;
import com.beloboki.mapper.UserMapper;
import com.beloboki.model.User;
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
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    public ResponseEntity<Page<UserResponse>> retrieveFilterNameAndSurnameUsers(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "surname", required = false) String surname,
            @RequestParam(value = "page") Integer pageNumber,
            @RequestParam(value = "size") Integer pageSize) {
        if ((name != null && !name.isBlank()) || (surname != null && !surname.isBlank())) {
            Page<User> users =
                    userService.retrieveFilterNameAndSurname(name, surname, pageNumber, pageSize);
            Page<UserResponse> userResponses = users.map(userMapper::userToUserResponse);
            return ResponseEntity.ok().body(userResponses);
        } else {
            Page<User> users = userService.retrieveAllUsers(pageNumber, pageSize);
            Page<UserResponse> userResponses = users.map(userMapper::userToUserResponse);
            return ResponseEntity.ok().body(userResponses);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> retrieveById(@PathVariable("id") Long userId) {
        User user = userService.retrieveById(userId);
        return ResponseEntity.ok().body(userMapper.userToUserResponse(user));
    }

    @PostMapping
    public ResponseEntity<UserResponse> save(@Valid @RequestBody UserRequest userRequest) {
        User user = userMapper.userRequestToUser(userRequest);
        User saveUser = userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userMapper.userToUserResponse(saveUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(
            @PathVariable("id") Long id, @Valid @RequestBody UserRequest userRequest) {
        User user = userMapper.userRequestToUser(userRequest);
        User updatedUser = userService.updateById(id, user);
        return ResponseEntity.ok().body(userMapper.userToUserResponse(updatedUser));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponse> updateStatus(
            @PathVariable("id") Long userId, @RequestParam("status") Boolean status) {
        User user = userService.updateStatus(userId, status);
        return ResponseEntity.ok().body(userMapper.userToUserResponse(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long userId) {
        userService.deleteById(userId);
        return ResponseEntity.noContent().build();
    }
}
