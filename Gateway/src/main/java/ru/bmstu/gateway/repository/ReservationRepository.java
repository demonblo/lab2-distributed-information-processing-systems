package ru.bmstu.gateway.repository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import ru.bmstu.gateway.controller.exception.service.GatewayErrorException;
import ru.bmstu.gateway.controller.exception.service.ReservationServiceNotAvailableException;
import ru.bmstu.gateway.dto.CreateReservationRequest;
import ru.bmstu.gateway.dto.ReservationDTO;

import java.util.UUID;

@Slf4j
@Repository
public class ReservationRepository extends BaseRepository{
    @CircuitBreaker(name = "gateway-reservation", fallbackMethod = "getReservationsArrByUsername_fallback")
    public ReservationDTO[] getReservationsArrByUsername(String username) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .host(appParams.hostHotel)
                        .path(appParams.pathReservation)
                        .port(appParams.portHotel)
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("X-User-Name", username)
                .retrieve()
                .onStatus(HttpStatus::isError, error -> {
                    throw new ReservationServiceNotAvailableException(error.statusCode());
                })
                .bodyToMono(ReservationDTO[].class)
                .onErrorMap(Throwable.class, error -> {
                    throw new GatewayErrorException(error.getMessage());
                })
                .block();
    }
    private ReservationDTO[] getReservationsArrByUsername_fallback(String username, Throwable t) {
        log.error(">>> GATEWAY FALLBACK (getReservationsArrByUsername) username={}: {}", username, t.getMessage());
        throw new ReservationServiceNotAvailableException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @CircuitBreaker(name = "gateway-reservation", fallbackMethod = "getReservationByUsernameReservationUid_fallback")
    public ReservationDTO getReservationByUsernameReservationUid(String username, UUID reservationUid) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .host(appParams.hostHotel)
                        .path(appParams.pathReservation + "/{reservationUid}")
                        .port(appParams.portHotel)
                        .build(reservationUid))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("X-User-Name", username)
                .retrieve()
                .onStatus(HttpStatus::isError, error -> {
                    throw new ReservationServiceNotAvailableException(error.statusCode());
                })
                .bodyToMono(ReservationDTO.class)
                .onErrorMap(Throwable.class, error -> {
                    throw new GatewayErrorException(error.getMessage());
                })
                .block();
    }
    private ReservationDTO getReservationByUsernameReservationUid_fallback(String username, UUID reservationUid, Throwable t) {
        log.error(">>> GATEWAY FALLBACK (getReservationByUsernameReservationUid) username={}, reservationUid={}: {}",
                username, reservationUid, t.getMessage());
        throw new ReservationServiceNotAvailableException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @CircuitBreaker(name = "gateway-reservation", fallbackMethod = "getReservationFullPrice_fallback")
    public Integer getReservationFullPrice(String username, CreateReservationRequest request) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .host(appParams.hostHotel)
                        .path(appParams.pathHotel + "/{hotelUid}/price")
                        .port(appParams.portHotel)
                        .queryParam("startDate", request.getStartDate())
                        .queryParam("endDate", request.getEndDate())
                        .build(request.getHotelUid()))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("X-User-Name", username)
                .retrieve()
                .onStatus(HttpStatus::isError, error -> {
                    throw new ReservationServiceNotAvailableException(error.statusCode());
                })
                .bodyToMono(Integer.class)
                .onErrorMap(error -> {
                    throw new GatewayErrorException(error.getMessage());
                })
                .block();
    }
    private Integer getReservationFullPrice_fallback(String username, CreateReservationRequest request, Throwable t) {
        log.error(">>> GATEWAY FALLBACK (getReservationFullPrice) username={}: {}", username, t.getMessage());
        throw new ReservationServiceNotAvailableException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @CircuitBreaker(name = "gateway-reservation", fallbackMethod = "cancelReservation_fallback")
    public void cancelReservation(String username, UUID reservationUid) {
        webClient
            .delete()
            .uri(uriBuilder -> uriBuilder
                    .host(appParams.hostHotel)
                    .path(appParams.pathReservation + "/{reservationUid}")
                    .port(appParams.portHotel)
                    .build(reservationUid))
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header("X-User-Name", username)
            .retrieve()
            .onStatus(HttpStatus::isError, error -> {
                throw new ReservationServiceNotAvailableException(error.statusCode());
            })
            .bodyToMono(Void.class)
            .onErrorMap(Throwable.class, error -> {
                throw new GatewayErrorException(error.getMessage());
            })
            .block();
    }
    private void cancelReservation_fallback(String username, UUID reservationUid, Throwable t) {
        log.error(">>> GATEWAY FALLBACK (cancelReservation) username={}, reservationUid={}: {}", username, reservationUid, t.getMessage());
        throw new ReservationServiceNotAvailableException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
