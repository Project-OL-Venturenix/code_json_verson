package com.venturenix.cmc.payload.response;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class EventGroupResponse {
  private String token;
  private String type = "Bearer";
  private Long id; 
  private Integer groupid;
  private Integer eventid;
  private String status;
  
  private LocalDateTime createddate;

  
  private Integer createdby;

  
  private LocalDateTime updateddate;

  
  private Integer updatedby;
  
  
  public EventGroupResponse(String accessToken, Long id, Integer groupid, Integer eventid, String name, String status, LocalDateTime createddate, Integer createdby, LocalDateTime updateddate, Integer updatedby) {
    this.token = accessToken;
    this.id = id;
    this.groupid = groupid;
    this.eventid = eventid;
    this.status = status;
    this.createddate = createddate;
    this.createdby = createdby;
    this.updateddate = updateddate;
    this.updatedby = updatedby;
  
  }

  public String getAccessToken() {
    return token;
  }

  public void setAccessToken(String accessToken) {
    this.token = accessToken;
  }

  public String getTokenType() {
    return type;
  }

  public void setTokenType(String tokenType) {
    this.type = tokenType;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getGroupid() {
    return groupid;
  }

  public void setGroupid(Integer groupid) {
    this.groupid = groupid;
  }  

  public Integer getEventid() {
    return eventid;
  }

  public void setEventid(Integer eventid) {
    this.eventid = eventid;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

public Integer getCreatedby() {
    return createdby;
  }

  public void setCreatedby(Integer createdby) {
    this.createdby = createdby;
  }

  public LocalDateTime getCreateddate() {
    return createddate;
  }

  public void setCreateddate(LocalDateTime createddate) {
    this.createddate = createddate;
  }

   public Integer getUpdatedby() {
    return updatedby;
  }

  public void setUpdatedby(Integer updatedby) {
    this.updatedby = updatedby;
  }

  public LocalDateTime getUpdateddate() {
    return updateddate;
  }

  public void setUpdateddate(LocalDateTime updateddate) {
    this.updateddate = updateddate;
  }
}
