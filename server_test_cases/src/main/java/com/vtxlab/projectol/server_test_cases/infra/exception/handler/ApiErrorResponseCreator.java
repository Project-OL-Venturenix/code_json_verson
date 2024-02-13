package com.vtxlab.projectol.server_test_cases.infra.exception.handler;

import java.time.LocalDate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.vtxlab.projectol.server_test_cases.infra.exception.dto.ApiErrorResponse;

@Service
public class ApiErrorResponseCreator {

  public ApiErrorResponse buildResponse(String errorMessage,
      HttpStatus httpStatus) {
    return ApiErrorResponse.builder().message(errorMessage)
        .timestamp(LocalDate.now()).httpStatusCode(httpStatus.value())
        .build();
  }

  public ApiErrorResponse buildResponse(Exception exception,
      HttpStatus httpStatus) {
    return buildResponse(exception.getMessage(), httpStatus);
  }
}
