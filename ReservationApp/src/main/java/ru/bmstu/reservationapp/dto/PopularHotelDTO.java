package ru.bmstu.reservationapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Accessors(chain = true)
@AllArgsConstructor
public class PopularHotelDTO {
    @JsonProperty("id")
    public Integer id;


    @JsonProperty("name")
    public String name;

    @JsonProperty("cnt")
    public Long cnt;
}
