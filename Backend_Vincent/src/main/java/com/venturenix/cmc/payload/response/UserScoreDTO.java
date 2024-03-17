package com.venturenix.cmc.payload.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserScoreDTO {
  private int eventId;
  private List<UserResult> result;

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class UserResult {
    private String name;
    private Map<String, Integer> score;
    private Map<String, LocalDateTime> submitTime;
    private Map<String, String> runtime;
  }
}