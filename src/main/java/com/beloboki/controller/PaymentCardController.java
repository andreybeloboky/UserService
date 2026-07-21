package com.beloboki.controller;

import com.beloboki.dto.PaymentCardRequest;
import com.beloboki.dto.PaymentCardResponse;
import com.beloboki.mapper.PaymentCardMapper;
import com.beloboki.model.PaymentCard;
import com.beloboki.service.PaymentCardService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/paymentCard")
public class PaymentCardController {

    private PaymentCardService paymentCardService;
    private PaymentCardMapper paymentCardMapper;

    @GetMapping("/filter")
    public ResponseEntity<Page<PaymentCardResponse>> retrieveFilterByNameAndSurname(
            @RequestParam String holder,
            @RequestParam int pageNumber,
            @RequestParam int pageSize) {
        Page<PaymentCard> paymentCards = paymentCardService.retrieveFilterByHolder(holder, pageNumber, pageSize);
        Page<PaymentCardResponse> cardResponses =
                paymentCards.map(paymentCardMapper::cardToCardResponse);
        return ResponseEntity.status(HttpServletResponse.SC_OK).body(cardResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<PaymentCardResponse>> retrieveAllCardsByUserId(
            @PathVariable("id") Long userId) {
        List<PaymentCard> paymentCards = paymentCardService.retrieveAllCardsByUserId(userId);
        List<PaymentCardResponse> paymentCardResponses = new ArrayList<>();
        for (PaymentCard paymentCard : paymentCards) {
            PaymentCardResponse paymentCardResponse =
                    paymentCardMapper.cardToCardResponse(paymentCard);
            paymentCardResponses.add(paymentCardResponse);
        }
        return ResponseEntity.status(HttpServletResponse.SC_OK).body(paymentCardResponses);
    }

    @PostMapping("/{id}")
    public ResponseEntity<PaymentCardResponse> save(
            @PathVariable("id") Long userId,
            @Valid @RequestBody PaymentCardRequest paymentCardRequest) {
        PaymentCard paymentCard =
                paymentCardMapper.paymentCardRequestToPaymentCard(paymentCardRequest);
        PaymentCard savePaymentCard = paymentCardService.save(userId, paymentCard);
        return ResponseEntity.status(HttpServletResponse.SC_CREATED)
                .body(paymentCardMapper.cardToCardResponse(savePaymentCard));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentCardResponse> update(
            @PathVariable("id") Long cardId,
            @Valid @RequestBody PaymentCardRequest paymentCardRequest) {
        PaymentCard paymentCard =
                paymentCardMapper.paymentCardRequestToPaymentCard(paymentCardRequest);
        PaymentCard updateCard = paymentCardService.updateById(cardId, paymentCard);
        return ResponseEntity.status(HttpServletResponse.SC_OK)
                .body(paymentCardMapper.cardToCardResponse(updateCard));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<PaymentCardResponse> updateStatus(
            @PathVariable("id") Long cardId, @PathVariable("status") Boolean status) {
        PaymentCard paymentCard = paymentCardService.setStatus(cardId, status);
        return ResponseEntity.status(HttpServletResponse.SC_OK)
                .body(paymentCardMapper.cardToCardResponse(paymentCard));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable("id") Long id) {
        paymentCardService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
