package ru.bmstu.loyaltyapp.controller;

import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.loyaltyapp.config.ActionType;
import ru.bmstu.loyaltyapp.dto.LogInfoDTO;
import ru.bmstu.loyaltyapp.dto.LoyaltyIntoResponse;
import ru.bmstu.loyaltyapp.kafka.KafkaProducer;
import ru.bmstu.loyaltyapp.service.LoyaltyService;
import ru.bmstu.loyaltyapp.service.TokenService;

import java.util.Date;
import java.util.HashMap;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/loyalty")
public class LoyaltyController {
    private final LoyaltyService loyaltyService;

    @Autowired
    private final TokenService tokenService;

    private final KafkaProducer producer;

    @GetMapping(produces = "application/json")
    public ResponseEntity<?> getDiscountByUsername(@RequestHeader(value = "Authorization", required = false) String bearerToken) {
        log.info("[LOYALTY]: Request to get username's loyalty status was caught.");
        Date startDate = new Date();

        tokenService.validateToken(bearerToken);

        ResponseEntity<?> res = ResponseEntity
                .ok()
                .body(loyaltyService.getDiscountByUsername(bearerToken));

        producer.send(new LogInfoDTO(tokenService.getUsername(bearerToken), startDate, new Date(),
                ActionType.MICROSERVICE_DISCOUNT_CURRENT_USER, null));

        return res;
    }

    @GetMapping(value = "/user", produces = "application/json")
    public ResponseEntity<?> getLoyaltyInfoResponseByUsername(@RequestHeader(value = "Authorization", required = false) String bearerToken) {
        log.info("[LOYALTY]: Request to get username's loyalty info was caught.");
        Date startDate = new Date();

        tokenService.validateToken(bearerToken);

        LoyaltyIntoResponse info = loyaltyService.getLoyaltyInfoResponseByUsername(bearerToken);

        producer.send(new LogInfoDTO(tokenService.getUsername(bearerToken), startDate, new Date(),
                ActionType.MICROSERVICE_LOYALTY_CURRENT_USER, null));

        return ResponseEntity
                .ok()
                .body(info);
    }

    @GetMapping(value = "/update", produces = "application/json")
    public ResponseEntity<Integer> getReservationUpdatedPrice(@PathParam(value = "price") Integer price,
                                                        @PathParam(value = "discount") Integer discount) {
        log.info("[LOYALTY]: Request to get updated price was caught.");
        Date startDate = new Date();

        ResponseEntity<Integer> res = ResponseEntity
                .ok()
                .body(loyaltyService.getReservationUpdatedPrice(price, discount));

        producer.send(new LogInfoDTO(null, startDate, new Date(),
                ActionType.MICROSERVICE_PRICE_WITH_DISCOUNT, new HashMap<String, Integer>() {{
                    put("price", price);
                    put("discount", discount);
        }}));

        return res;
    }

    @PostMapping(produces = "application/json")
    public ResponseEntity<LoyaltyIntoResponse> updateReservationCount(@RequestHeader(value = "Authorization", required = false) String bearerToken) {
        log.info("[LOYALTY]: Request to update reservation count got user was caught.");
        Date startDate = new Date();

        tokenService.validateToken(bearerToken);

        ResponseEntity<LoyaltyIntoResponse> res = ResponseEntity
                .ok()
                .body(loyaltyService.updateReservationCount(bearerToken));

        producer.send(new LogInfoDTO(tokenService.getUsername(bearerToken), startDate, new Date(),
                ActionType.MICROSERVICE_UPDATE_RESERVATION_COUNT, null));

        return res;
    }

    @DeleteMapping(produces = "application/json")
    public ResponseEntity<LoyaltyIntoResponse> cancelReservationCount(@RequestHeader(value = "Authorization", required = false) String bearerToken) {
        log.info("[LOYALTY]: Request to cancel reservation count user was caught.");
        Date startDate = new Date();

        tokenService.validateToken(bearerToken);

        ResponseEntity<LoyaltyIntoResponse> res = ResponseEntity
                .ok()
                .body(loyaltyService.cancelReservationCount(bearerToken));

        producer.send(new LogInfoDTO(tokenService.getUsername(bearerToken), startDate, new Date(),
                ActionType.MICROSERVICE_CANCEL_RESERVATION, null));

        return res;
    }
}
