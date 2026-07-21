package com.beloboki.controller;

import com.beloboki.dto.UserRequest;
import com.beloboki.dto.UserResponse;
import com.beloboki.mapper.UserMapper;
import com.beloboki.model.User;
import com.beloboki.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private UserMapper userMapper;

    @GetMapping("/filter")
    public ResponseEntity<Page<UserResponse>> retrieveFilterByNameAndSurname(
            @RequestParam("name") String name, @RequestParam("surname") String surname,
            @RequestParam("page") int pageNumber, @RequestParam("size") int pageSize) {
        Page<User> users = userService.retrieveFilterByNameAndSurname(name, surname, pageNumber, pageSize);
        Page<UserResponse> userResponses = users.map(userMapper::userToUserResponse);
        return ResponseEntity.status(HttpServletResponse.SC_OK).body(userResponses);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> retrieveAllUsers() {
        List<User> users = userService.retrieveAllUsers();
        List<UserResponse> userResponses =
                users.stream().map(userMapper::userToUserResponse).toList();
        return ResponseEntity.status(HttpServletResponse.SC_OK).body(userResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> retrieveById(@PathVariable("id") Long userId) {
        User user = userService.retrieveById(userId);
        return ResponseEntity.status(HttpServletResponse.SC_OK)
                .body(userMapper.userToUserResponse(user));
    }

    @PostMapping
    public ResponseEntity<UserResponse> save(@Valid @RequestBody UserRequest userRequest) {
        User user = userMapper.userRequestToUser(userRequest);
        User saveUser = userService.save(user);
        return ResponseEntity.status(HttpServletResponse.SC_CREATED)
                .body(userMapper.userToUserResponse(saveUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(
            @PathVariable("id") Long id, @Valid @RequestBody UserRequest userRequest) {
        User user = userMapper.userRequestToUser(userRequest);
        User updatedUser = userService.updateById(id, user);
        return ResponseEntity.status(HttpServletResponse.SC_OK)
                .body(userMapper.userToUserResponse(updatedUser));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<UserResponse> updateStatus(
            @PathVariable("id") Long userId, @PathVariable("status") Boolean status) {
        User user = userService.setStatus(userId, status);
        return ResponseEntity.status(HttpServletResponse.SC_OK)
                .body(userMapper.userToUserResponse(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable("id") Long userId) {
        userService.deleteById(userId);
        return ResponseEntity.ok().build();
    }
}
