package ru.bmstu.gateway.repository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.BodyInserters;
import ru.bmstu.gateway.controller.exception.service.GatewayErrorException;
import ru.bmstu.gateway.controller.exception.service.PaymentServiceNotAvailableException;
import ru.bmstu.gateway.dto.PaymentDTO;
import ru.bmstu.gateway.dto.PaymentInfo;
import ru.bmstu.gateway.dto.ReservationDTO;

import java.util.UUID;

@Slf4j
@Repository
public class PaymentRepository extends BaseRepository {
    @CircuitBreaker(name = "gateway-payment", fallbackMethod = "getPaymentInfoByPaymentUid_fallback")
    public PaymentInfo getPaymentInfoByPaymentUid(UUID paymentUid) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .host(appParams.hostPayment)
                        .path(appParams.pathPayment + "/{paymentUid}")
                        .port(appParams.portPayment)
                        .build(paymentUid))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .onStatus(HttpStatus::isError, error -> {
                    throw new PaymentServiceNotAvailableException(error.statusCode());
                })
                .bodyToMono(PaymentInfo.class)
                .onErrorMap(Throwable.class, error -> {
                    throw new GatewayErrorException(error.getMessage());
                })
                .block();
    }
    private PaymentInfo getPaymentInfoByPaymentUid_fallback(UUID paymentUid, Throwable t) {
        log.error(">>> GATEWAY FALLBACK (getPaymentInfoByPaymentUid) paymentUid={}: {}", paymentUid, t.getMessage());
        throw new PaymentServiceNotAvailableException(HttpStatus.SERVICE_UNAVAILABLE);
    }

    @CircuitBreaker(name = "gateway-payment", fallbackMethod = "postPayment_fallback")
    public PaymentDTO postPayment(Integer price) {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .host(appParams.hostPayment)
                        .path(appParams.pathPayment)
                        .port(appParams.portPayment)
                        .queryParam("price", price)
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .onStatus(HttpStatus::isError, error -> {
                    throw new PaymentServiceNotAvailableException(error.statusCode());
                })
                .bodyToMono(PaymentDTO.class)
                .onErrorMap(Throwable.class, error -> {
                    throw new GatewayErrorException(error.getMessage());
                })
                .block();
    }
    private PaymentDTO postPayment_fallback(Integer price, Throwable t) {
        log.error(">>> GATEWAY FALLBACK (postPayment) price={}: {}", price, t.getMessage());
        throw new PaymentServiceNotAvailableException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @CircuitBreaker(name = "gateway-payment", fallbackMethod = "postReservation_fallback")
    public ReservationDTO postReservation(String username, ReservationDTO reservationDTO) {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .host(appParams.hostHotel)
                        .path(appParams.pathReservation)
                        .port(appParams.portHotel)
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("X-User-Name", username)
                .body(BodyInserters.fromValue(reservationDTO))
                .retrieve()
                .onStatus(HttpStatus::isError, error -> {
                    throw new PaymentServiceNotAvailableException(error.statusCode());
                })
                .bodyToMono(ReservationDTO.class)
                .onErrorMap(Throwable.class, error -> {
                    throw new GatewayErrorException(error.getMessage());
                })
                .block();
    }
    private ReservationDTO postReservation_fallback(String username, ReservationDTO reservationDTO, Throwable t) {
        log.error(">>> GATEWAY FALLBACK (postReservation) username={}: {}", username, t.getMessage());
        throw new PaymentServiceNotAvailableException(HttpStatus.SERVICE_UNAVAILABLE);
    }

    @CircuitBreaker(name = "gateway-payment", fallbackMethod = "cancelPayment_fallback")
    public void cancelPayment(UUID paymentUid) {
        webClient
            .delete()
            .uri(uriBuilder -> uriBuilder
                    .host(appParams.hostPayment)
                    .path(appParams.pathPayment + "/{paymentUid}")
                    .port(appParams.portPayment)
                    .build(paymentUid))
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .retrieve()
            .onStatus(HttpStatus::isError, error -> {
                throw new PaymentServiceNotAvailableException(error.statusCode());
            })
            .bodyToMono(Void.class)
            .onErrorMap(Throwable.class, error -> {
                throw new GatewayErrorException(error.getMessage());
            })
            .block();
    }
    private void cancelPayment_fallback(UUID paymentUid, Throwable t) {
        log.error(">>> GATEWAY FALLBACK (cancelPayment) paymentUid={}: {}", paymentUid, t.getMessage());
        throw new PaymentServiceNotAvailableException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
