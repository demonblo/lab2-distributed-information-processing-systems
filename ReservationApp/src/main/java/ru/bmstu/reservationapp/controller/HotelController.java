package ru.bmstu.reservationapp.controller;

import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.reservationapp.config.ActionType;
import ru.bmstu.reservationapp.controller.converter.ResponseConverter;
import ru.bmstu.reservationapp.dto.HotelResponse;
import ru.bmstu.reservationapp.dto.LogInfoDTO;
import ru.bmstu.reservationapp.dto.PaginationResponse;
import ru.bmstu.reservationapp.dto.PopularHotelDTO;
import ru.bmstu.reservationapp.kafka.KafkaProducer;
import ru.bmstu.reservationapp.service.HotelService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Date;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/hotels")
public class HotelController {
    private final HotelService hotelService;
    private final KafkaProducer producer;

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");


    @GetMapping(value = "/all", produces = "application/json")
    public ResponseEntity<PaginationResponse> getHotels(@PathParam(value = "page") Integer page,
                                                     @PathParam(value = "size") Integer size) {
        log.info("[RESERVATION]: Request to get all hotels (page={}, size={}) was caught.", page, size);
        Date startDate = new Date();

        Pageable paging = PageRequest.of(page - 1, size);
        Page<HotelResponse> hotelsResponsePage = hotelService.getHotels(paging);

        PaginationResponse paginationResponse = ResponseConverter.toPaginationResponse(page, size, hotelsResponsePage);

        producer.send(new LogInfoDTO(null, startDate, new Date(),
                ActionType.MICROSERVICE_ALL_HOTELS, new HashMap<String, Integer>() {{
                    put("page", page);
                    put("size", size);
        }}));

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(paginationResponse);
    }

    @GetMapping(value = "/{hotelId}", produces = "application/json")
    public ResponseEntity<HotelResponse> getHotelByHotelId(@PathVariable Integer hotelId) {
        log.info("[RESERVATION]: Request to get hotel by hotelId={} was caught.", hotelId);
        Date startDate = new Date();

        ResponseEntity<HotelResponse> res = ResponseEntity
                .ok()
                .body(hotelService.getHotelByHotelId(hotelId));

        producer.send(new LogInfoDTO(null, startDate, new Date(),
                ActionType.MICROSERVICE_HOTEL_BY_ID, new HashMap<String, Integer>() {{
                    put("hotelId", hotelId);
        }}));

        return res;
    }


    @GetMapping(value = "/exists/{hotelUid}", produces = "application/json")
    public ResponseEntity<HotelResponse> getHotelByUid(@PathVariable UUID hotelUid) {
        log.info("[RESERVATION]: Request to get hotel by hotelUid={} was caught.", hotelUid);
        Date startDate = new Date();

        ResponseEntity<HotelResponse> res = ResponseEntity
                .status(HttpStatus.OK)
                .body(hotelService.getHotelByHotelUid(hotelUid));

        producer.send(new LogInfoDTO(null, startDate, new Date(),
                ActionType.MICROSERVICE_HOTEL_BY_UUID, new HashMap<String, UUID>() {{
            put("hotelUid", hotelUid);
        }}));

        return res;
    }


    @GetMapping(value = "/{hotelUid}/id", produces = "application/json")
    public ResponseEntity<?> getHotelIdByHotelUid(@PathVariable UUID hotelUid) {
        log.info("[RESERVATION]: Request to get hotelId by hotelUid={} was caught.", hotelUid);
        Date startDate = new Date();

        ResponseEntity<?> res = ResponseEntity
                .ok()
                .body(hotelService.getHotelIdByHotelUid(hotelUid));

        producer.send(new LogInfoDTO(null, startDate, new Date(),
                ActionType.MICROSERVICE_HOTELID_BY_UUID, new HashMap<String, UUID>() {{
            put("hotelUid", hotelUid);
        }}));

        return res;
    }

    @GetMapping(value = "/{hotelUid}/image", produces = "application/json")
    public ResponseEntity<?> getHotelImageUrlByHotelUid(@PathVariable UUID hotelUid) {
        log.info("[RESERVATION]: Request to get image url by hotelUid={} was caught.", hotelUid);
        Date startDate = new Date();

        ResponseEntity<?> res = ResponseEntity
                .ok()
                .body(hotelService.getHotelImageUrlByHotelUid(hotelUid));

        producer.send(new LogInfoDTO(null, startDate, new Date(),
                ActionType.MICROSERVICE_IMAGEURL_BY_HOTELID, new HashMap<String, UUID>() {{
            put("hotelUid", hotelUid);
        }}));

        return res;
    }


    @GetMapping(value = "/{hotelUid}/price", produces = "application/json")
    public ResponseEntity<Integer> getHotelDatePrice(@PathVariable UUID hotelUid,
                                                     @PathParam(value = "startDate") String startDate,
                                                     @PathParam(value = "endDate") String endDate) throws ParseException {
        log.info("[RESERVATION]: Request to get reservation's price was caught, hotelUid={}, startDate={}; endDate={}).",
                hotelUid, startDate, endDate);
        Date start = new Date();

        Integer price = hotelService.getHotelDatePrice(hotelUid, formatter.parse(startDate), formatter.parse(endDate));
        HttpStatus httpStatus = (price == null) ? HttpStatus.NOT_FOUND : HttpStatus.OK;

        producer.send(new LogInfoDTO(null, start, new Date(),
                ActionType.MICROSERVICE_RESERVATION_PRICE, new HashMap<String, Object>() {{
            put("hotelUid", hotelUid);
            put("startDate", startDate);
            put("endDate", endDate);
        }}));

        return ResponseEntity
                .status(httpStatus)
                .body(price);
    }

    @GetMapping(value = "/popular", produces = "application/json")
    public ResponseEntity<?> getPopularHotels() {
        log.info("[HOTEL]: Request to get 3 most popular hotels was caught.");
        Date start = new Date();

        List<PopularHotelDTO> hotelArr = hotelService.getPopularHotels();

        producer.send(new LogInfoDTO(null, start, new Date(), ActionType.MICROSERVICE_MOST_POPULAR_HOTELS, null));

        return ResponseEntity
                .ok()
                .body(hotelArr);
    }
}
