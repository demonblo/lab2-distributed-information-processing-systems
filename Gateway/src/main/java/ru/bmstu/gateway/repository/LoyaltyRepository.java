package ru.bmstu.gateway.repository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import ru.bmstu.gateway.controller.exception.service.GatewayErrorException;
import ru.bmstu.gateway.controller.exception.service.LoyaltyServiceNotAvailableException;
import ru.bmstu.gateway.dto.LoyaltyInfoResponse;


@Slf4j
@Repository
public class LoyaltyRepository extends BaseRepository {
    @CircuitBreaker(name = "gateway-loyalty", fallbackMethod = "getLoyaltyInfoResponseByUsername_fallback")
    public LoyaltyInfoResponse getLoyaltyInfoResponseByUsername(String username) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .host(appParams.hostLoyalty)
                        .path(appParams.pathLoyalty + "/user")
                        .port(appParams.portLoyalty)
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("X-User-Name", username)
                .retrieve()
                .onStatus(HttpStatus::isError, error -> {
                    throw new LoyaltyServiceNotAvailableException(error.statusCode());
                })
                .bodyToMono(LoyaltyInfoResponse.class)
                .onErrorMap(Throwable.class, error -> {
                    throw new GatewayErrorException(error.getMessage());
                })
                .block();
    }
    private LoyaltyInfoResponse getLoyaltyInfoResponseByUsername_fallback(String username, Throwable t) {
        log.info(">>> GATEWAY FALLBACK (getPaymentInfoByPaymentUid) username={}: {}", username, t.getMessage());
        throw new LoyaltyServiceNotAvailableException(HttpStatus.SERVICE_UNAVAILABLE); // todo нужно для /loyalty
    }

    @CircuitBreaker(name = "gateway-loyalty", fallbackMethod = "getUserStatus_fallback")
    public Integer getUserStatus(String username) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .host(appParams.hostLoyalty)
                        .path(appParams.pathLoyalty)
                        .port(appParams.portLoyalty)
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("X-User-Name", username)
                .retrieve()
                .onStatus(HttpStatus::isError, error -> {
                    throw new LoyaltyServiceNotAvailableException(error.statusCode());
                })
                .bodyToMono(Integer.class)
                .onErrorMap(Throwable.class, error -> {
                    throw new GatewayErrorException(error.getMessage());
                })
                .block();
    }
    private Integer getUserStatus_fallback(String username, Throwable t) {
        log.info(">>> GATEWAY FALLBACK (getUserStatus) username={}: {}", username, t.getMessage());
        throw new LoyaltyServiceNotAvailableException(HttpStatus.SERVICE_UNAVAILABLE);
    }

    @CircuitBreaker(name = "gateway-loyalty", fallbackMethod = "getReservationUpdatedPrice_fallback")
    public Integer getReservationUpdatedPrice(Integer price, Integer discount) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .host(appParams.hostLoyalty)
                        .path(appParams.pathLoyalty + "/update")
                        .port(appParams.portLoyalty)
                        .queryParam("price", price)
                        .queryParam("discount", discount)
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .onStatus(HttpStatus::isError, error -> {
                    throw new LoyaltyServiceNotAvailableException(error.statusCode());
                })
                .bodyToMono(Integer.class)
                .onErrorMap(Throwable.class, error -> {
                    throw new GatewayErrorException(error.getMessage());
                })
                .block();
    }
    private Integer getReservationUpdatedPrice_fallback(Integer price, Integer discount, Throwable t) {
        log.info(">>> GATEWAY FALLBACK (getReservationUpdatedPrice) price={}: {}", price, t.getMessage());
        throw new LoyaltyServiceNotAvailableException(HttpStatus.SERVICE_UNAVAILABLE);
    }

    @CircuitBreaker(name = "gateway-loyalty", fallbackMethod = "updateReservationCount_fallback")
    public LoyaltyInfoResponse updateReservationCount(String username) {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .host(appParams.hostLoyalty)
                        .path(appParams.pathLoyalty)
                        .port(appParams.portLoyalty)
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("X-User-Name", username)
                .retrieve()
                .onStatus(HttpStatus::isError, error -> {
                    throw new LoyaltyServiceNotAvailableException(error.statusCode());
                })
                .bodyToMono(LoyaltyInfoResponse.class)
                .onErrorMap(Throwable.class, error -> {
                    throw new GatewayErrorException(error.getMessage());
                })
                .block();
    }
    private LoyaltyInfoResponse updateReservationCount_fallback(String username, Throwable t) {
        log.info(">>> GATEWAY FALLBACK (updateReservationCount) username={}: {}", username, t.getMessage());
        throw new LoyaltyServiceNotAvailableException(HttpStatus.SERVICE_UNAVAILABLE);
    }

    @CircuitBreaker(name = "gateway-loyalty", fallbackMethod = "cancelLoyalty_fallback")
    public LoyaltyInfoResponse cancelLoyalty(String username) {
        return webClient
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .host(appParams.hostLoyalty)
                        .path(appParams.pathLoyalty)
                        .port(appParams.portLoyalty)
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("X-User-Name", username)
                .retrieve()
                .onStatus(HttpStatus::isError, error -> {
                    throw new LoyaltyServiceNotAvailableException(error.statusCode());
                })
                .bodyToMono(LoyaltyInfoResponse.class)
                .onErrorMap(Throwable.class, error -> {
                    throw new GatewayErrorException(error.getMessage());
                })
                .block();
    }
    private LoyaltyInfoResponse cancelLoyalty_fallback(String username, Throwable t) {
        log.info(">>> GATEWAY FALLBACK (updateReservationCount) username={}: {}", username, t.getMessage());
        throw new LoyaltyServiceNotAvailableException(HttpStatus.SERVICE_UNAVAILABLE);
    }
}
