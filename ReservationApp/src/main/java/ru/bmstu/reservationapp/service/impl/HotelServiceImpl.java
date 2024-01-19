package ru.bmstu.reservationapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bmstu.reservationapp.dto.HotelResponse;
import ru.bmstu.reservationapp.dto.PopularHotelDTO;
import ru.bmstu.reservationapp.model.HotelEntity;
import ru.bmstu.reservationapp.model.PopularHotel;
import ru.bmstu.reservationapp.repository.HotelRepository;
import ru.bmstu.reservationapp.service.HotelService;
import ru.bmstu.reservationapp.service.converter.HotelConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {
    private final HotelRepository hotelRepository;

    @Transactional(readOnly = true)
    public Page<HotelResponse> getHotels(Pageable pageable) {
        return hotelRepository
                .findAll(pageable)
                .map(HotelConverter::fromHotelsEntityToHotelResponse);
    }

    @Transactional(readOnly = true)
    public HotelResponse getHotelByHotelId(Integer hotelId) {
        return HotelConverter.fromHotelsEntityToHotelResponse(hotelRepository
                .getHotelById(hotelId));
    }

    @Transactional(readOnly = true)
    public HotelResponse getHotelByHotelUid(UUID hotelUid) {
        return HotelConverter.fromHotelsEntityToHotelResponse(hotelRepository
                .getHotelByUid(hotelUid));
    }

    @Transactional(readOnly = true)
    public Integer getHotelIdByHotelUid(UUID hotelUid) {
        return hotelRepository.getHotelIdByHotelUid(hotelUid);
    }

    public String getHotelImageUrlByHotelUid(UUID hotelUid) {
        return hotelRepository.getHotelImageUrlByHotelUid(hotelUid);
    }

    @Transactional(readOnly = true)
    public Integer getHotelDatePrice(UUID hotelUid, Date startDate, Date endDate) {
        HotelEntity hotelEntity = hotelRepository.getHotelByUid(hotelUid);
        if (hotelEntity == null)
            return null;

        HotelResponse hotelResponse = HotelConverter.fromHotelsEntityToHotelResponse(hotelEntity);
        long diffMillis = endDate.getTime() - startDate.getTime();
        long days = TimeUnit.DAYS.convert(diffMillis, TimeUnit.MILLISECONDS);
        return Math.toIntExact(days * hotelResponse.getPrice());
    }

    public List<PopularHotelDTO> getPopularHotels() {
        List<Object[]> hotelArr = hotelRepository.getPopularHotels();
        ArrayList<PopularHotelDTO> hotelDTOArr = new ArrayList<>();

        for (Object[] hotel: hotelArr) {
            Integer id = (Integer) hotel[0];
            String name = (String) hotel[1];
            Long cnt = (Long) hotel[2];

            PopularHotelDTO hotelDto = new PopularHotelDTO(id, name, cnt);
            hotelDTOArr.add(hotelDto);
        }

        return hotelDTOArr;
    }
}
