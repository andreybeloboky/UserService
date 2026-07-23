package com.beloboki.controller;

import com.beloboki.dto.PaymentCardRequest;
import com.beloboki.dto.PaymentCardResponse;
import com.beloboki.model.PaymentCard;
import com.beloboki.service.PaymentCardService;
import jakarta.validation.Valid;

import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping
public class PaymentCardController {

    private PaymentCardService paymentCardService;

    @GetMapping("/payment-cards")
    public ResponseEntity<Page<PaymentCardResponse>> retrieveFilterHolders(
            @RequestParam(required = false) String holder,
            @RequestParam(defaultValue = "0", required = false) Integer pageNumber,
            @RequestParam(defaultValue = "10", required = false) Integer pageSize) {
        Page<PaymentCardResponse> paymentCardsResponse =
                paymentCardService.retrieveFilterByHolder(holder, pageNumber, pageSize);
        return ResponseEntity.ok().body(paymentCardsResponse);
    }

    @GetMapping("/payment-cards/{id}")
    public ResponseEntity<PaymentCardResponse> retrieveById(@PathVariable("id") Long id) {
        var paymentCardResponse = paymentCardService.retrieveById(id);
        return ResponseEntity.ok().body(paymentCardResponse);
    }

    @GetMapping("/users/{userId}/payment-cards")
    public ResponseEntity<List<PaymentCardResponse>> retrieveAllCardsByUserId(
            @PathVariable("userId") Long userId) {
        List<PaymentCardResponse> paymentCardsResponse =
                paymentCardService.retrieveAllCardsByUserId(userId);
        return ResponseEntity.ok().body(paymentCardsResponse);
    }

    @PostMapping("/users/{userId}/payment-cards")
    public ResponseEntity<Void> save(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody PaymentCardRequest paymentCardRequest) {
        paymentCardService.save(userId, paymentCardRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/payment-cards/{id}")
    public ResponseEntity<Void> update(
            @PathVariable("id") Long cardId,
            @Valid @RequestBody PaymentCardRequest paymentCardRequest) {
        PaymentCard paymentCard = paymentCardService.updateById(cardId, paymentCardRequest);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/payment-cards/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable("id") Long cardId, @RequestParam("status") Boolean status) {
        paymentCardService.updateStatus(cardId, status);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/payment-cards/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id) {
        paymentCardService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
