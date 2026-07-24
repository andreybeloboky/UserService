package com.beloboki.controller;

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
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping
@Slf4j
public class PaymentCardController {

    private PaymentCardService paymentCardService;

    @GetMapping("/api/payment-cards")
    public ResponseEntity<Page<PaymentCardResponse>> retrieveFilterHolders(
            @RequestParam(required = false) String holder,
            @RequestParam(defaultValue = "0", required = false) Integer pageNumber,
            @RequestParam(defaultValue = "10", required = false) Integer pageSize) {
        log.info(
                "Received a request to get payment cards. Filter -> Holder: '{}', Page: {}, Size:"
                        + " {}",
                holder,
                pageNumber,
                pageSize);
        return ResponseEntity.ok()
                .body(paymentCardService.retrieveFilterByHolder(holder, pageNumber, pageSize));
    }

    @GetMapping("/api/payment-cards/{id}")
    public ResponseEntity<PaymentCardResponse> retrieveById(@PathVariable("id") Long id) {
        log.info("Looking for payment card with ID: {}", id);
        return ResponseEntity.ok().body(paymentCardService.retrieveById(id));
    }

    @GetMapping("/api/users/{userId}/payment-cards")
    public ResponseEntity<List<PaymentCardResponse>> retrieveAllCardsByUserId(
            @PathVariable("userId") Long userId) {
        log.info("Retrieve all payment cards for User ID: {}", userId);
        return ResponseEntity.ok().body(paymentCardService.retrieveAllCardsByUserId(userId));
    }

    @PostMapping("/api/users/{userId}/payment-cards")
    public ResponseEntity<Void> save(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody PaymentCardRequest paymentCardRequest) {
        log.info(
                "Creating a new payment card for User ID: {} (Holder: '{}')",
                userId,
                paymentCardRequest.getHolder());
        paymentCardService.save(userId, paymentCardRequest);
        log.info("New payment card was successfully created for User ID: {}", userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/api/payment-cards/{id}")
    public ResponseEntity<Void> update(
            @PathVariable("id") Long cardId,
            @Valid @RequestBody PaymentCardRequest paymentCardRequest) {
        log.info(
                "Updating payment card with ID: {} (New holder: '{}')",
                cardId,
                paymentCardRequest.getHolder());
        paymentCardService.updateById(cardId, paymentCardRequest);
        log.info("Payment card with ID {} was successfully updated", cardId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/api/payment-cards/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable("id") Long cardId, @RequestParam("status") Boolean status) {
        log.info("Changing status for payment card ID {} to: {}", cardId, status);
        paymentCardService.updateStatus(cardId, status);
        log.info("Status for payment card ID {} was successfully changed", cardId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/payment-cards/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id) {
        log.info("Received a request to delete payment card with ID: {}", id);
        paymentCardService.deleteById(id);
        log.info("Payment card with ID {} was successfully deleted", id);
        return ResponseEntity.noContent().build();
    }
}
