package ru.bmstu.reservationapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.SqlResultSetMapping;
import lombok.AllArgsConstructor;

@SqlResultSetMapping(
        name = "PopularHotelMapping",
        classes = @ConstructorResult(
                targetClass = PopularHotel.class,
                columns = {
                        @ColumnResult(name = "id", type = Integer.class),
                        @ColumnResult(name = "name", type = String.class),
                        @ColumnResult(name = "cnt", type = Integer.class)
                }
        )
)
@AllArgsConstructor
public class PopularHotel {
    public Integer id;
    public String name;
    public Integer cnt;
}
