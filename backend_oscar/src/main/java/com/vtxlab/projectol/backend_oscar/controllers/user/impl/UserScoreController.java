package com.vtxlab.projectol.backend_oscar.controllers.user.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.vtxlab.projectol.backend_oscar.controllers.user.UserScoreOperation;
import com.vtxlab.projectol.backend_oscar.entity.event.Event;
import com.vtxlab.projectol.backend_oscar.entity.questionBank.QuestionBank;
import com.vtxlab.projectol.backend_oscar.entity.questionBank.QuestionBonusRuntime;
import com.vtxlab.projectol.backend_oscar.entity.user.User;
import com.vtxlab.projectol.backend_oscar.entity.user.UserScore;
import com.vtxlab.projectol.backend_oscar.payload.request.question.SubmitTimeRunTimeDTO;
import com.vtxlab.projectol.backend_oscar.payload.request.user.UserScoreRequest;
import com.vtxlab.projectol.backend_oscar.payload.response.user.MessageResponse;
import com.vtxlab.projectol.backend_oscar.payload.response.user.UserScoreDTO;
import com.vtxlab.projectol.backend_oscar.security.jwt.JwtUtils;
import com.vtxlab.projectol.backend_oscar.service.user.UserScoreService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class UserScoreController implements UserScoreOperation {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @Autowired
  private UserScoreService userScoreService;

  public ResponseEntity<?> addUserScore(UserScoreRequest userscoreRequest) {
    if (!userScoreService.addUserScore(userscoreRequest))
      return ResponseEntity.badRequest()
          .body(new MessageResponse("Fail Add User Score"));
    return ResponseEntity
        .ok(new MessageResponse("Add UserScore successfully!"));

  }

  public ResponseEntity<List<UserScore>> getAllUserScores() {
    try {
      List<UserScore> userscores = userScoreService.getAllUserScores();
      if (userscores.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }
      return new ResponseEntity<>(userscores, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

    }

  }

  public ResponseEntity<UserScore> getUserScoreById(String id) {
    Long userId = Long.valueOf(id);
    UserScore userscoreData = userScoreService.getUserScoreById(userId);
    return new ResponseEntity<>(userscoreData, HttpStatus.OK);
  }


  public ResponseEntity<?> deleteUserScore(String id) {
    Long userId = Long.valueOf(id);
    try {
      userScoreService.deleteUserScore(userId);
      return ResponseEntity.ok(
          new MessageResponse("Delete UserScore " + userId + " successfully!"));
    } catch (Exception e) {
      return ResponseEntity
          .ok(new MessageResponse("HttpStatus.INTERNAL_SERVER_ERROR"));
    }
  }

  @Override
  public boolean addScore(String eventid, String userid, String questionid,
      String testcasePassTotal, SubmitTimeRunTimeDTO submitTimeRunTimeDTO) {
    Long eventID = Long.valueOf(eventid);
    Long userID = Long.valueOf(userid);
    Long questionID = Long.valueOf(questionid);
    Integer testcasePass = Integer.valueOf(testcasePassTotal);

    return userScoreService.addScore(eventID, userID, questionID, testcasePass,
        submitTimeRunTimeDTO);
  }

  @Override
  public ResponseEntity<UserScoreDTO> getUserTestCaseByEventId(String eventid) {
    Long eventId = Long.valueOf(eventid);
    UserScoreDTO userScoreDTO =
        userScoreService.getUserTestCaseByEventId(eventId);
    return ResponseEntity.ok(userScoreDTO);
  }
}
