package ru.bmstu.reservationapp.repository;

import jakarta.persistence.SqlResultSetMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.bmstu.reservationapp.dto.PopularHotelDTO;
import ru.bmstu.reservationapp.model.HotelEntity;
import ru.bmstu.reservationapp.model.PopularHotel;

import java.util.List;
import java.util.UUID;

@Repository
public interface HotelRepository extends JpaRepository<HotelEntity, Integer> {
    @Query(value = "SELECT * FROM hotels WHERE id = ?1",
            nativeQuery = true)
    HotelEntity getHotelById(Integer hotelId);

    @Query(value = "SELECT * FROM hotels WHERE hotel_uid = ?1",
            nativeQuery = true)
    HotelEntity getHotelByUid(UUID hotelUid);

    @Query(value = "SELECT id FROM hotels WHERE hotel_uid = ?1",
            nativeQuery = true)
    Integer getHotelIdByHotelUid(UUID hotelUid);

    @Query(value = "SELECT url FROM images WHERE id = ?1",
            nativeQuery = true)
    String getHotelImageUrlByHotelUid(UUID hotelUid);

    @Query(value = "SELECT hotels.id, name, COUNT(*) AS cnt " +
            "FROM hotels join reservation ON hotels.id = reservation.hotel_id " +
            "GROUP BY hotels.id " +
            "ORDER BY cnt DESC " +
            "LIMIT 3;", nativeQuery = true)
    List<Object[]> getPopularHotels();
}
