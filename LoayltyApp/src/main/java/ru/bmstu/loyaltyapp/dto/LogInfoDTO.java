package ru.bmstu.loyaltyapp.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class LogInfoDTO {
    @JsonProperty("eventUuid")
    public UUID eventUuid;

    @JsonProperty("eventStart")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    public Date eventStart;

    @JsonProperty("eventEnd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    public Date eventEnd;

    @JsonProperty("username")
    public String username;

    @JsonProperty("action")
    public String action;

    @JsonProperty("params")
    public Map<String, Object> params;

    @JsonAnySetter
    void setParams(String key, Object value) {
        params.put(key, value);
    }

    @JsonProperty("service")
    public String service;


    public LogInfoDTO(String user, Date eventStart, Date eventEnd, String action, Object params) {
        ObjectMapper mapper = new ObjectMapper();

        this.eventUuid = UUID.randomUUID();
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
        this.username = user;
        this.action = action;
        this.params = mapper.convertValue(params, Map.class);
        this.service = "LOYALTY";
    }
}
