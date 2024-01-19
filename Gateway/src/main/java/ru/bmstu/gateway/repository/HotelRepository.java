package ru.bmstu.gateway.repository;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import ru.bmstu.gateway.controller.exception.service.GatewayErrorException;
import ru.bmstu.gateway.controller.exception.service.HotelServiceNotAvailableException;
import ru.bmstu.gateway.dto.HotelResponse;
import ru.bmstu.gateway.dto.PaginationResponse;

import java.util.UUID;

@Slf4j
@Repository
public class HotelRepository extends BaseRepository {
    @CircuitBreaker(name = "gateway-hotels", fallbackMethod = "getHotels_fallback")
    public ResponseEntity<?> getHotels(Integer page, Integer size) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .host(appParams.hostHotel)
                        .path(appParams.pathHotel + "/all")
                        .port(appParams.portHotel)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .onStatus(HttpStatus::isError, error -> {
                    throw new HotelServiceNotAvailableException(error.statusCode());
                })
                .toEntity(PaginationResponse.class)
                .onErrorMap(Throwable.class, error -> {
                    throw new GatewayErrorException(error.getMessage());
                })
                .block();
    }
    private ResponseEntity<?> getHotels_fallback(Integer page, Integer size, Throwable t) {
        log.error(">>> GATEWAY FALLBACK (getHotels), page={}, size={}: {}", page, size, t.getMessage());
        return ResponseEntity
                .internalServerError()
                .build();
    }

    @CircuitBreaker(name = "gateway-hotels", fallbackMethod = "getHotelResponseByHotelId_fallback")
    public HotelResponse getHotelResponseByHotelId(Integer hotelId) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .host(appParams.hostHotel)
                        .path(appParams.pathHotel + "/{hotelId}")
                        .port(appParams.portHotel)
                        .build(hotelId))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .onStatus(HttpStatus::isError, error -> {
                    throw new HotelServiceNotAvailableException(error.statusCode());
                })
                .bodyToMono(HotelResponse.class)
                .onErrorMap(Throwable.class, error -> {
                    throw new GatewayErrorException(error.getMessage());
                })
                .block();
    }
    private HotelResponse getHotelResponseByHotelId_fallback(Integer hotelId, Throwable t) {
        log.error(">>> GATEWAY FALLBACK (getReservationsArrByUsername) hotelId={}: {}", hotelId, t.getMessage());
        throw new HotelServiceNotAvailableException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @CircuitBreaker(name = "gateway-hotels", fallbackMethod = "getHotelResponseByHotelUid_fallback")
    public HotelResponse getHotelResponseByHotelUid(UUID hotelUid) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .host(appParams.hostHotel)
                        .path(appParams.pathHotel + "/exists/{hotelUid}")
                        .port(appParams.portHotel)
                        .build(hotelUid))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .onStatus(HttpStatus::isError, error -> {
                    throw new HotelServiceNotAvailableException(error.statusCode());
                })
                .bodyToMono(HotelResponse.class)
                .onErrorMap(Throwable.class, error -> {
                    throw new GatewayErrorException(error.getMessage());
                })
                .block();
    }
    private HotelResponse getHotelResponseByHotelUid_fallback(UUID hotelUid, Throwable t) {
        log.error(">>> GATEWAY FALLBACK (getHotelResponseByHotelUid) hotelUid={}: {}", hotelUid, t.getMessage());
        throw new HotelServiceNotAvailableException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @CircuitBreaker(name = "gateway-hotels", fallbackMethod = "getHotelIdByHotelUid_fallback")
    public Integer getHotelIdByHotelUid(UUID hotelUid) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .host(appParams.hostHotel)
                        .path(appParams.pathHotel + "/{hotelUid}/id")
                        .port(appParams.portHotel)
                        .build(hotelUid))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .onStatus(HttpStatus::isError, error -> {
                    throw new HotelServiceNotAvailableException(error.statusCode());
                })
                .bodyToMono(Integer.class)
                .onErrorMap(Throwable.class, error -> {
                    throw new GatewayErrorException(error.getMessage());
                })
                .block();
    }
    private Integer getHotelIdByHotelUid_fallback(UUID hotelUid, Throwable t) {
        log.error(">>> GATEWAY FALLBACK (getHotelIdByHotelUid) hotelUid={}: {}", hotelUid, t.getMessage());
        throw new HotelServiceNotAvailableException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
