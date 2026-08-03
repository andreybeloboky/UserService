package com.beloboki.controller;

import com.beloboki.config.CurrentUser;
import com.beloboki.dto.PaymentCardRequest;
import com.beloboki.dto.PaymentCardResponse;
import com.beloboki.service.PaymentCardService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping
@Slf4j
public class PaymentCardController {

    private PaymentCardService paymentCardService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/api/payment-cards")
    public ResponseEntity<Page<PaymentCardResponse>> retrieveFilterHolders(
            @RequestParam(required = false) String holder,
            @RequestParam(defaultValue = "0", required = false) Integer pageNumber,
            @RequestParam(defaultValue = "10", required = false) Integer pageSize) {
        return ResponseEntity.ok()
                .body(paymentCardService.retrieveFilterByHolder(holder, pageNumber, pageSize));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/api/payment-cards/{id}")
    public ResponseEntity<PaymentCardResponse> retrieveById(
            @AuthenticationPrincipal CurrentUser currentUser, @PathVariable("id") Long id) {
        return ResponseEntity.ok().body(paymentCardService.retrieveById(id, currentUser));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/api/users/{id}/payment-cards")
    public ResponseEntity<List<PaymentCardResponse>> retrieveAllCardsByUserId(
            @AuthenticationPrincipal CurrentUser currentUser, @PathVariable("id") Long userId) {
        paymentCardService.validate(currentUser.userId(), userId, currentUser.role());
        return ResponseEntity.ok().body(paymentCardService.retrieveAllCardsByUserId(userId));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/api/users/{id}/payment-cards")
    public ResponseEntity<Void> save(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable("id") Long userId,
            @Valid @RequestBody PaymentCardRequest paymentCardRequest) {
        paymentCardService.validate(currentUser.userId(), userId, currentUser.role());
        paymentCardService.save(userId, paymentCardRequest);
        log.info("New payment card was successfully created for User ID: {}", userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/api/payment-cards/{id}")
    public ResponseEntity<Void> update(
            @PathVariable("id") Long cardId,
            @Valid @RequestBody PaymentCardRequest paymentCardRequest) {
        paymentCardService.updateById(cardId, paymentCardRequest);
        log.info("Payment card with ID {} was successfully updated", cardId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/api/payment-cards/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable("id") Long cardId, @RequestParam("status") Boolean status) {
        paymentCardService.updateStatus(cardId, status);
        log.info("Status for payment card ID {} was successfully changed", cardId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/api/payment-cards/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id) {
        paymentCardService.deleteById(id);
        log.info("Payment card with ID {} was successfully deleted", id);
        return ResponseEntity.noContent().build();
    }
}
