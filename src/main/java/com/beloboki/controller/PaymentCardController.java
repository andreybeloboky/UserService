package com.beloboki.controller;

import com.beloboki.dto.PaymentCardRequest;
import com.beloboki.dto.PaymentCardResponse;
import com.beloboki.mapper.PaymentCardMapper;
import com.beloboki.model.PaymentCard;
import com.beloboki.service.PaymentCardService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping
public class PaymentCardController {

    private PaymentCardService paymentCardService;
    private PaymentCardMapper paymentCardMapper;

    @GetMapping("/payment-cards")
    public ResponseEntity<Page<PaymentCardResponse>> retrieveFilterHolders(
            @RequestParam(required = false) String holder,
            @RequestParam(defaultValue = "0", required = false) Integer pageNumber,
            @RequestParam(defaultValue = "10", required = false) Integer pageSize) {
        Page<PaymentCard> paymentCards =
                paymentCardService.retrieveFilterByHolder(holder, pageNumber, pageSize);
        Page<PaymentCardResponse> cardResponses =
                paymentCards.map(paymentCardMapper::cardToCardResponse);
        return ResponseEntity.status(HttpServletResponse.SC_OK).body(cardResponses);
    }

    @GetMapping("/users/{userId}/payment-cards")
    public ResponseEntity<List<PaymentCardResponse>> retrieveAllCardsByUserId(
            @PathVariable("userId") Long userId) {
        List<PaymentCard> paymentCards = paymentCardService.retrieveAllCardsByUserId(userId);
        List<PaymentCardResponse> paymentCardResponses = new ArrayList<>();
        for (PaymentCard paymentCard : paymentCards) {
            PaymentCardResponse paymentCardResponse =
                    paymentCardMapper.cardToCardResponse(paymentCard);
            paymentCardResponses.add(paymentCardResponse);
        }
        return ResponseEntity.status(HttpServletResponse.SC_OK).body(paymentCardResponses);
    }

    @PostMapping("/users/{userId}/payment-cards")
    public ResponseEntity<PaymentCardResponse> save(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody PaymentCardRequest paymentCardRequest) {
        PaymentCard paymentCard =
                paymentCardMapper.paymentCardRequestToPaymentCard(paymentCardRequest);
        PaymentCard savePaymentCard = paymentCardService.save(userId, paymentCard);
        return ResponseEntity.status(HttpServletResponse.SC_CREATED)
                .body(paymentCardMapper.cardToCardResponse(savePaymentCard));
    }

    @PutMapping("/payment-cards/{id}")
    public ResponseEntity<PaymentCardResponse> update(
            @PathVariable("id") Long cardId,
            @Valid @RequestBody PaymentCardRequest paymentCardRequest) {
        PaymentCard paymentCard =
                paymentCardMapper.paymentCardRequestToPaymentCard(paymentCardRequest);
        PaymentCard updateCard = paymentCardService.updateById(cardId, paymentCard);
        return ResponseEntity.status(HttpServletResponse.SC_OK)
                .body(paymentCardMapper.cardToCardResponse(updateCard));
    }

    @PatchMapping("/payment-cards/{id}/status")
    public ResponseEntity<PaymentCardResponse> updateStatus(
            @PathVariable("id") Long cardId, @RequestParam("status") Boolean status) {
        PaymentCard paymentCard = paymentCardService.setStatus(cardId, status);
        return ResponseEntity.status(HttpServletResponse.SC_OK)
                .body(paymentCardMapper.cardToCardResponse(paymentCard));
    }

    @DeleteMapping("/payment-cards/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id) {
        paymentCardService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
