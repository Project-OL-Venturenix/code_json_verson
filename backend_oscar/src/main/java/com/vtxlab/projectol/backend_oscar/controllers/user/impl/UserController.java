package com.vtxlab.projectol.backend_oscar.controllers.user.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.vtxlab.projectol.backend_oscar.controllers.user.UserOperation;
import com.vtxlab.projectol.backend_oscar.entity.event.Event;
import com.vtxlab.projectol.backend_oscar.entity.user.User;
import com.vtxlab.projectol.backend_oscar.exception.UserNotInEventException;
import com.vtxlab.projectol.backend_oscar.exception.exceptionEnum.Syscode;
import com.vtxlab.projectol.backend_oscar.payload.response.user.MessageResponse;
import com.vtxlab.projectol.backend_oscar.repository.event.EventRepository;
import com.vtxlab.projectol.backend_oscar.repository.user.RoleRepository;
import com.vtxlab.projectol.backend_oscar.repository.user.UserQuestionSubmissionRepository;
import com.vtxlab.projectol.backend_oscar.repository.user.UserRepository;
import com.vtxlab.projectol.backend_oscar.security.jwt.JwtUtils;
import com.vtxlab.projectol.backend_oscar.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class UserController implements UserOperation {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  EventRepository eventRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  UserQuestionSubmissionRepository userscoreRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @Autowired
  private UserService userService;

  public ResponseEntity<List<User>> getAllUsers() {
    try {
      List<User> users = userRepository.findAll();
      if (users.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }
      return new ResponseEntity<>(users, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

    }

  }

  public ResponseEntity<User> getUserById(long id) {
    Optional<User> userData = userRepository.findById(id);
    if (userData.isPresent()) {
      return new ResponseEntity<>(userData.get(), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  public ResponseEntity<User> updateUser(long id, User userRequest) {
    Optional<User> userData = userRepository.findById(id);

    if (userData.isPresent()) {
      User builder = User.builder()//
          .firstName(userRequest.getFirstName())//
          .lastName(userRequest.getLastName())//
          .mobile(userRequest.getMobile())//
          .email(userRequest.getEmail())//
          .userName(userRequest.getUserName())//
          .password(encoder.encode(userRequest.getPassword()))//
          .company(userRequest.getCompany())//
          .title(userRequest.getTitle())//
          .pyExperience(userRequest.getPyExperience())//
          .jvExperience(userRequest.getJvExperience())//
          .jsExperience(userRequest.getJsExperience())//
          .csExperience(userRequest.getCsExperience())//
          .saExperience(userRequest.getSaExperience())//
          .status(userRequest.getStatus())//
          .createdDate(LocalDateTime.now())//
          .createdBy(userRequest.getCreatedBy())//
          .updatedDate(LocalDateTime.now())//
          .updatedBy(userRequest.getUpdatedBy())//
          .build();
      return new ResponseEntity<>(userRepository.save(builder), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  public ResponseEntity<?> deleteUser(long id) {
    try {
      userRepository.deleteById(id);
      return ResponseEntity
          .ok(new MessageResponse("Delete User " + id + " successfully!"));
    } catch (Exception e) {
      return ResponseEntity
          .ok(new MessageResponse("HttpStatus.INTERNAL_SERVER_ERROR"));
    }
  }

  @Override
  public ResponseEntity<?> addUserInEvent(Long userId, Long eventId) {
    Optional<User> optionalUser = userRepository.findById(userId);
    Optional<Event> optionalEvent = eventRepository.findById(eventId);
    if (optionalUser.isPresent() && optionalEvent.isPresent()) {
      User user = optionalUser.get();
      Event event = optionalEvent.get();
      user.getEvents().add(event);
      userRepository.save(user);
      return ResponseEntity
          .ok(new MessageResponse("User added to event successfully!"));
    } else {
      return ResponseEntity.ok(new MessageResponse("User or event not found!"));
    }

  }

  private String parseJwt(HttpServletRequest request) {
    String headerAuth = request.getHeader("Authorization");

    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
      return headerAuth.substring(7);
    }

    return null;
  }

  @Override
  // public ResponseEntity<User> getUserByEventId(String eventid,
  // String jwt)
  public ResponseEntity<User> getUserByEventId(@PathVariable String eventid,
      HttpServletRequest request) {
    // Parse JWT token from request
    String jwt = parseJwt(request);
    Long eventId = Long.valueOf(eventid);

    // Check if JWT token is valid and extract username
    String userName = null;
    if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
      userName = jwtUtils.getUserNameFromJwtToken(jwt);
    }

    // Find the user by username
    Optional<User> optionalUser = userRepository.findByUserName(userName);
    if (optionalUser.isPresent()) {
      User user = optionalUser.get();
      log.info("user: " + user);

      // Check if the user is associated with the given event ID
      boolean userAssociatedWithEvent = user.getEvents().stream()
          .anyMatch(event -> event.getId().equals(eventId));

      // If user is associated with the event, return the user
      if (userAssociatedWithEvent) {
        return new ResponseEntity<>(user, HttpStatus.OK);
      } else {
        // If user is not associated with the event, throw UserNotInEventException
        throw new UserNotInEventException(Syscode.USER_NOT_IN_EVENT);
      }
    } else {
      // If user is not found, return HTTP status 404
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  // @Override
  // public ResponseEntity<UserScoreDTO> getUserTestCaseByEventId(String eventid) {
  // Long eventId = Long.valueOf(eventid);
  // List<UserScore> target = userscoreRepository.findByEventId(eventId);

  // Map<Long, UserScoreDTO.UserResult> userResultMap = new HashMap<>();

  // for (UserScore userScore : target) {
  // Optional<User> userOptional =
  // userRepository.findById(userScore.getUser().getId());
  // if (!userResultMap.containsKey(userScore.getUser().getId())) {
  // UserScoreDTO.UserResult userResult = new UserScoreDTO.UserResult();
  // userResult.setName(userOptional.get().getUserName()); // Assuming user id as name
  // userResult.setScore(new HashMap<>());
  // userResultMap.put(userScore.getUser().getId(), userResult);
  // }

  // String questionKey = "Q" + userScore.getQuestion().getQuestionId();
  // int score = userScore.getResultOfPassingTestecase();

  // UserScoreDTO.UserResult userResult =
  // userResultMap.get(userScore.getUser().getId());
  // userResult.getScore().put(questionKey, score);
  // }

  // List<UserScoreDTO.UserResult> userResults =
  // new ArrayList<>(userResultMap.values());

  // UserScoreDTO userScoreDTO = new UserScoreDTO();
  // userScoreDTO.setEventId(eventId.intValue());
  // userScoreDTO.setResult(userResults);

  // return ResponseEntity.ok(userScoreDTO);
  // }
}

