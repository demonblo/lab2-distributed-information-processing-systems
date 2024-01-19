package ru.bmstu.reservationapp.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.reservationapp.config.ActionType;
import ru.bmstu.reservationapp.dto.LogInfoDTO;
import ru.bmstu.reservationapp.dto.ReservationDTO;
import ru.bmstu.reservationapp.kafka.KafkaProducer;
import ru.bmstu.reservationapp.service.ReservationService;
import ru.bmstu.reservationapp.service.TokenService;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/reservations")
public class ReservationController {
    @Autowired
    private final TokenService tokenService;

    private final ReservationService reservationService;
    private final KafkaProducer producer;

    @GetMapping(produces = "application/json")
    public ResponseEntity<?> getReservationsByUsername(@RequestHeader(value = "Authorization", required = false) String bearerToken) {
        log.info("[RESERVATION]: Request to get all reservations by username={} was caught.", tokenService.getUsername(bearerToken));
        Date startDate = new Date();

        tokenService.validateToken(bearerToken);

        ResponseEntity<?> res = ResponseEntity
                .status(HttpStatus.OK)
                .body(reservationService.getReservationsByUsername(bearerToken));

        producer.send(new LogInfoDTO(tokenService.getUsername(bearerToken), startDate, new Date(),
                ActionType.MICROSERVICE_RESERVATION_BY_USERNAME, null));

        return res;
    }

    @GetMapping(value = "/{reservationUid}", produces = "application/json")
    public ResponseEntity<?> getReservationsByUsernameReservationUid(@RequestHeader(value = "Authorization", required = false) String bearerToken,
                                                                     @PathVariable(value = "reservationUid") UUID reservationUid) {
        log.info("[RESERVATION]: Request to get all reservations by username and reservationUid={} was caught.", reservationUid);
        Date startDate = new Date();

        tokenService.validateToken(bearerToken);

        ResponseEntity<?> res = ResponseEntity
                .ok()
                .body(reservationService.getReservationsByUsernameReservationUid(bearerToken, reservationUid));;

        producer.send(new LogInfoDTO(tokenService.getUsername(bearerToken), startDate, new Date(),
                ActionType.MICROSERVICE_RESERVATION_BY_USERNAME_RESERVATIONUID, new HashMap<String, UUID>() {{
                    put("reservationUid", reservationUid);
        }}));

        return res;
    }

    @PostMapping(produces = "application/json")
    public ResponseEntity<?> postReservation(@RequestHeader(value = "Authorization", required = false) String bearerToken,
                                             @RequestBody ReservationDTO reservationDTO) {
        log.info("[RESERVATION]: Request to post a new reservation({}) for user was caught.", reservationDTO);
        Date startDate = new Date();

        tokenService.validateToken(bearerToken);

        ResponseEntity<?> res = ResponseEntity
                .ok()
                .body(reservationService.postReservation(bearerToken, reservationDTO));

        producer.send(new LogInfoDTO(tokenService.getUsername(bearerToken), startDate, new Date(),
                ActionType.MICROSERVICE_CREATE_RESERVATION, reservationDTO));

        return res;
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/{reservationUid}")
    public void revokeReservation(@RequestHeader(value = "Authorization", required = false) String bearerToken,
                                  @PathVariable(value = "reservationUid") UUID reservationUid) {
        log.info("[RESERVATION]: Request to revoke reservation was caught (reservationUid={}).", reservationUid);
        Date startDate = new Date();

        tokenService.validateToken(bearerToken);

        reservationService.revokeReservation(bearerToken, reservationUid);

        producer.send(new LogInfoDTO(tokenService.getUsername(bearerToken), startDate, new Date(),
                ActionType.MICROSERVICE_REVOKE_RESERVATION, new HashMap<String, UUID>() {{
                    put("reservationUid", reservationUid);
        }}));
    }
}
