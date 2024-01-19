package ru.bmstu.reservationapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.validation.annotation.Validated;

import java.math.BigInteger;

@Data
@Validated
@Accessors(chain = true)
public class ActiveClient {
    @JsonProperty("username")
    public String username;

    @JsonProperty("cnt")
    public BigInteger cnt;
}
