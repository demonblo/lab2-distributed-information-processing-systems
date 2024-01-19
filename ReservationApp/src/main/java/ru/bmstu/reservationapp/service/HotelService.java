package ru.bmstu.reservationapp.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.bmstu.reservationapp.dto.HotelResponse;
import ru.bmstu.reservationapp.dto.PopularHotelDTO;

import java.util.Date;
import java.util.List;
import java.util.UUID;


public interface HotelService {
    Page<HotelResponse> getHotels(Pageable pageable);
    HotelResponse getHotelByHotelId(Integer hotelId);
    HotelResponse getHotelByHotelUid(UUID hotelUid);
    Integer getHotelIdByHotelUid(UUID hotelUid);
    String getHotelImageUrlByHotelUid(UUID hotelUid);
    Integer getHotelDatePrice(UUID hotelUid, Date startDate, Date endDate);
    List<PopularHotelDTO> getPopularHotels();
}
