package com.vtxlab.projectol.server_test_cases.infra.exception.dto;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record ApiErrorResponse(
     @JsonProperty("message")
        String message,

        @JsonProperty("httpStatusCode")
        Integer httpStatusCode,

        @JsonProperty("timestamp")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIMESTAMP_JSON_FORMAT)
        LocalDate timestamp
) {
  public static final String TIMESTAMP_JSON_FORMAT = "yyyy-MM-dd HH:mm:ss";
}