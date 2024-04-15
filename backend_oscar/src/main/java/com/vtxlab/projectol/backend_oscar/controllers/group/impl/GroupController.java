package com.vtxlab.projectol.backend_oscar.controllers.group.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.vtxlab.projectol.backend_oscar.controllers.group.GroupOperation;
import com.vtxlab.projectol.backend_oscar.entity.event.Event;
import com.vtxlab.projectol.backend_oscar.entity.group.Group;
import com.vtxlab.projectol.backend_oscar.entity.user.User;
import com.vtxlab.projectol.backend_oscar.entity.user.UserScore;
import com.vtxlab.projectol.backend_oscar.payload.Mapper;
import com.vtxlab.projectol.backend_oscar.payload.response.group.GroupDTO;
import com.vtxlab.projectol.backend_oscar.payload.response.group.GroupUserDTO;
import com.vtxlab.projectol.backend_oscar.payload.response.user.GroupScoreDTO;
import com.vtxlab.projectol.backend_oscar.payload.response.user.MessageResponse;
import com.vtxlab.projectol.backend_oscar.repository.event.EventRepository;
import com.vtxlab.projectol.backend_oscar.repository.group.GroupRepository;
import com.vtxlab.projectol.backend_oscar.repository.user.RoleRepository;
import com.vtxlab.projectol.backend_oscar.repository.user.UserQuestionSubmissionRepository;
import com.vtxlab.projectol.backend_oscar.repository.user.UserRepository;
import com.vtxlab.projectol.backend_oscar.security.jwt.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class GroupController implements GroupOperation {
  @Autowired
  AuthenticationManager authenticationManager;


  @Autowired
  UserRepository userRepository;

  @Autowired
  EventRepository eventRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @Autowired
  GroupRepository groupRepository;

  @Autowired
  UserQuestionSubmissionRepository userscoreRepository;

  // public ResponseEntity<?> addGroup(
  // @Valid @RequestBody GroupRequest groupRequest) {
  // Group builder = Group.builder()//
  // .name(groupRequest.getName())//
  // .status(groupRequest.getStatus())//
  // .createdBy(groupRequest.getCreatedBy())//
  // .createdDate(LocalDateTime.now())//
  // .updatedBy(groupRequest.getUpdatedBy())//
  // .updatedDate(LocalDateTime.now())//
  // .build();

  // groupRepository.save(builder);
  // return ResponseEntity.ok(new MessageResponse("Add Group successfully!"));

  // }

  @Override
  public ResponseEntity<List<GroupDTO>> getAllGroups() {
    try {
      List<Group> groups = groupRepository.findAll();
      List<GroupDTO> result =
          groups.stream().map(e -> Mapper.map(e)).collect(Collectors.toList());

      if (groups.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }
      return new ResponseEntity<>(result, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

    }

  }



  @Override
  public ResponseEntity<Group> updateGroup(long id, Group group) {
    Optional<Group> groupData = groupRepository.findById(id);

    if (groupData.isPresent()) {
      Group _group = groupData.get();
      _group.setName(group.getName());
      _group.setStatus(group.getStatus());
      _group.setUpdatedDate(java.time.LocalDateTime.now());
      _group.setUpdatedBy(group.getUpdatedBy());
      return new ResponseEntity<>(groupRepository.save(_group), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @Override
  public ResponseEntity<?> deleteGroup(long id) {
    try {
      groupRepository.deleteById(id);
      return ResponseEntity
          .ok(new MessageResponse("Delete Group " + id + " successfully!"));
    } catch (Exception e) {
      return ResponseEntity
          .ok(new MessageResponse("HttpStatus.INTERNAL_SERVER_ERROR"));
    }
  }

  @Override
  public boolean addGroup(String eventID, String userID, String groupId) {
    Optional<User> user = userRepository.findById(Long.valueOf(userID));
    Optional<Event> event = eventRepository.findById(Long.valueOf(eventID));
    if (!user.isPresent() || !event.isPresent()) {
      return false;
    }
    Group builder = Group.builder()//
        .groupsId(Long.valueOf(groupId))//
        .users(new HashSet<>())//
        .events(new HashSet<>())//
        .build();
    builder.getUsers().add(user.get());
    builder.getEvents().add(event.get());

    groupRepository.save(builder);
    return true;
  }

  @Override
  public GroupScoreDTO getGroupScoreByEventId(String eventId) {
    Long eventIdLong = Long.valueOf(eventId);
    List<UserScore> userScores = userscoreRepository.findByEventId(eventIdLong);
    Map<Long, GroupScoreDTO.GroupResult> groupResultMap = new HashMap<>();

    for (UserScore userScore : userScores) {
      Optional<User> userOptional =
          userRepository.findById(userScore.getUser().getId());
      Optional<Group> groupOptional = groupRepository
          .findByEventsIdAndUsersId(eventIdLong, userOptional.get().getId());

      Long groupId = groupOptional.get().getGroupsId();

      if (!groupResultMap.containsKey(groupId)) {
        GroupScoreDTO.GroupResult groupResult = new GroupScoreDTO.GroupResult();
        groupResult.setGroupUserDTO(this.getGroupById(String.valueOf(groupId))); // Assuming user id as name
        // passingTestCaseNumber
        groupResult.setPassingTestCaseNumber(new HashMap<>());
        // score
        groupResult.setScore(new HashMap<>());
        // submitTime
        groupResult.setSubmitTime(new HashMap<>());
        // runtime
        groupResult.setRuntime(new HashMap<>());
        groupResultMap.put(groupId, groupResult);
      }

      String questionKey = "Q" + userScore.getQuestion().getQuestionId();
      int score = userScore.getResultOfPassingTestecase() == 10 ? 3 : 0;
      int bonus30Mins = Integer.valueOf(userScore.getBonusUnder30Mins());
      int runTimeBonus =
          Integer.valueOf(userScore.getBonusWithinQuestionRuntime());
      int total = score + bonus30Mins + runTimeBonus;

      GroupScoreDTO.GroupResult groupResult = groupResultMap.get(groupId);
      groupResult.getPassingTestCaseNumber().put(questionKey,
          userScore.getResultOfPassingTestecase());
      groupResult.getScore().put(questionKey, total);
      // submit time
      LocalDateTime submitTime = userScore.getSubmitTime();
      groupResult.getSubmitTime().put(questionKey, submitTime);
      // runtime
      String runtime = String.valueOf(userScore.getRuntimebyMsec());
      groupResult.getRuntime().put(questionKey, runtime);
    }

    Set<GroupScoreDTO.GroupResult> groupResults = new HashSet<>(
        groupResultMap.values().stream().collect(Collectors.toSet()));

    GroupScoreDTO groupScoreDTO = new GroupScoreDTO();
    groupScoreDTO.setEventId(eventIdLong.intValue());
    groupScoreDTO.setResult(groupResults);

    return groupScoreDTO;
  }

  private GroupUserDTO getGroupById(String groupid) {
    Long groupId = Long.valueOf(groupid);
    List<Group> groupData = groupRepository.findByGroupsId(groupId);
    Map<Long, String> userMap = new HashMap<>();
    for (Group group : groupData) {
      for (User user : group.getUsers()) {
        userMap.put(user.getId(), user.getUserName());
      }
    }
    return GroupUserDTO.builder().groupId(groupId).users(userMap).build();
  }


  private String parseJwt(HttpServletRequest request) {
    String headerAuth = request.getHeader("Authorization");

    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
      return headerAuth.substring(7);
    }

    return null;
  }

  @Override
  public GroupUserDTO getGroupById(String eventId, HttpServletRequest request) {
    String jwt = parseJwt(request);
    // Check if JWT token is valid and extract username
    String userName = null;
    if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
      userName = jwtUtils.getUserNameFromJwtToken(jwt);
    }

    // Find the user by username
    Optional<User> optionalUser = userRepository.findByUserName(userName);
    Optional<Event> optionalEvent =
        eventRepository.findById(Long.valueOf(eventId));
    if (optionalUser.isPresent() && optionalEvent.isPresent()) {
      User user = optionalUser.get();
      Event event = optionalEvent.get();
      Optional<Group> groupOptional =
          groupRepository.findByEventsIdAndUsersId(event.getId(), user.getId());
      if (groupOptional.isPresent()) {
        Group group = groupOptional.get();
        Map<Long, String> userMap = new HashMap<>();
        for (User userInGroup : group.getUsers()) {
          userMap.put(userInGroup.getId(), userInGroup.getUserName());
        }
        return GroupUserDTO.builder().groupId(group.getGroupsId())
            .users(userMap).build();
      }
    }
    return null;
  }
}
