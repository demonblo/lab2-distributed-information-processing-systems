package ru.bmstu.gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.gateway.controller.exception.data.RequestDataErrorException;
import ru.bmstu.gateway.dto.*;
import ru.bmstu.gateway.service.GatewayService;

import javax.websocket.server.PathParam;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping({"api/v1", ""})
public class GatewayController {
    @Autowired
    private GatewayService service;


    @GetMapping(value = "/manage/health", produces = "application/json")
    public ResponseEntity<?> isAlive() {
        return ResponseEntity
                .ok()
                .build();
    }

    @GetMapping(value = "/hotels", produces = "application/json")
    public ResponseEntity<?> getHotels(@PathParam(value = "page") Integer page,
                                       @PathParam(value = "size") Integer size) {
        log.info(">>> GATEWAY: Request to get all hotels was caught (params: page={}, size={}).", page, size);

        return service.getHotels(page, size);
    }

    @GetMapping(value = "/me", produces = "application/json")
    public ResponseEntity<?> getUserInfo(@RequestHeader(value = "X-User-Name") String username) {
        log.info(">>> GATEWAY: Request to get all reservations by current username={} was caught.", username);

        return ResponseEntity
                .ok()
                .body(service.getUserInfo(username));
    }

    @GetMapping(value = "/reservations", produces = "application/json")
    public ResponseEntity<?> getReservationsByUsername(@RequestHeader(value = "X-User-Name") String username) {
        log.info(">>> GATEWAY: Request to get all reservations by username={} was caught.", username);

        return ResponseEntity
                .ok()
                .body(service.getReservationsList(username));
    }

    @GetMapping(value = "/reservations/{reservationUid}", produces = "application/json")
    public ResponseEntity<?> getReservationByUsernameReservationUid(@RequestHeader(value = "X-User-Name") String username,
                                                                    @PathVariable(value = "reservationUid") UUID reservationUid) {
        log.info(">>> GATEWAY: Request to get all reservations by username={} and reservationUid={} was caught.", username, reservationUid);

        return ResponseEntity
                .ok()
                .body(service.getReservationByUsernameReservationUid(username, reservationUid));
    }

    @PostMapping(value = "/reservations")
    public ResponseEntity<?> postReservation(@RequestHeader(value = "X-User-Name") String username,
                                                                     @RequestBody CreateReservationRequest request) {
        log.info(">>> Request to create reservation was caught (username={}; data={}).", username, request.toString());

        if (!request.isValid())
            throw new RequestDataErrorException(request.toString());

        return ResponseEntity
                .ok()
                .body(service.postReservation(username, request));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/reservations/{reservationUid}", produces = "application/json")
    public void cancelReservation(@RequestHeader(value = "X-User-Name") String username,
                                  @PathVariable(value = "reservationUid") UUID reservationUid) {
        log.info(">>> GATEWAY: Request to delete reservation was caught (username={}; reservationUid={}).", username, reservationUid);

        service.cancelReservation(username, reservationUid);
    }

    @GetMapping(value = "/loyalty", produces = "application/json")
    public ResponseEntity<?> getLoyaltyInfoResponseByUsername(@RequestHeader(value = "X-User-Name") String username) {
        log.info(">>> GATEWAY: Request to get loyalty info by username={} was caught.", username);

        return ResponseEntity
                .ok()
                .body(service.getLoyaltyInfoResponseByUsername(username));
    }
}
