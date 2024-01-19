package ru.bmstu.reservationapp.dto;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import ru.bmstu.reservationapp.dto.enums.StatusEnum;

import java.sql.Date;
import java.util.UUID;

@Data
@Validated
@ToString
@Accessors(chain = true)
public class ReservationDTO {
    private UUID reservationUid;
    private Integer hotelId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
    private StatusEnum status;
    private UUID paymentUid;
}
