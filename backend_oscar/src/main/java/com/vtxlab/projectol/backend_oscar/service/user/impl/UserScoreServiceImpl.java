package com.vtxlab.projectol.backend_oscar.service.user.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.vtxlab.projectol.backend_oscar.entity.event.Event;
import com.vtxlab.projectol.backend_oscar.entity.questionBank.QuestionBank;
import com.vtxlab.projectol.backend_oscar.entity.user.User;
import com.vtxlab.projectol.backend_oscar.entity.user.UserScore;
import com.vtxlab.projectol.backend_oscar.payload.request.question.SubmitTimeRunTimeDTO;
import com.vtxlab.projectol.backend_oscar.payload.request.user.UserScoreRequest;
import com.vtxlab.projectol.backend_oscar.payload.response.user.UserScoreResult;
import com.vtxlab.projectol.backend_oscar.repository.event.EventRepository;
import com.vtxlab.projectol.backend_oscar.repository.questionBank.QuestionBankRepository;
import com.vtxlab.projectol.backend_oscar.repository.user.RoleRepository;
import com.vtxlab.projectol.backend_oscar.repository.user.UserQuestionSubmissionRepository;
import com.vtxlab.projectol.backend_oscar.repository.user.UserRepository;
import com.vtxlab.projectol.backend_oscar.service.user.UserScoreService;

@Service
public class UserScoreServiceImpl implements UserScoreService {
  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  private UserQuestionSubmissionRepository userscoreRepository;

  @Autowired
  private EventRepository eventRepository;

  @Autowired
  private QuestionBankRepository questionRepository;


  @Override
  public boolean addScore(Long eventid, Long userid, Long questionid,
                          Integer testcasePass, SubmitTimeRunTimeDTO submitTimeRunTimeDTO) {
    Optional<UserScore> builder = userscoreRepository
            .findByEventIdAndUserIdAndQuestionId(eventid, userid, questionid);
    if (!builder.isPresent()) {
      Optional<Event> event = eventRepository.findById(eventid);
      Optional<User> user = userRepository.findById(userid);
      Optional<QuestionBank> question = questionRepository.findById(questionid);

      if (event.isPresent() && user.isPresent() && question.isPresent()) {
        LocalDateTime submissionTime = LocalDateTime.now();
        LocalDateTime targetStartTime = event.get().getTargetStartTime();

        long minutesDifference = Duration.between(targetStartTime, submissionTime).toMinutes();

        boolean isWithin30Minutes = minutesDifference >= 0 && minutesDifference < 30;

        userscoreRepository.saveAndFlush(UserScore.builder().event(event.get())
                .user(user.get()).question(question.get())
                .resultOfPassingTestecase(testcasePass)
                .status(testcasePass == 10 ? "Pass All Test Cases" : "Fail")
                .submitTime(submissionTime)
                .runtimebyMsec(submitTimeRunTimeDTO.getRunTimeByMsec())
                .bonusUnder30Mins(isWithin30Minutes && testcasePass == 10 ? "1" : "0")
                .bonusWithinQuestionRuntime(
                        question.get().getBonusRuntime() > submitTimeRunTimeDTO.getRunTimeByMsec() && testcasePass == 10 ? "1" : "0")
                .createdDate(LocalDateTime.now()).updatedDate(LocalDateTime.now())
                .build());
        return true;
      } else {
        return false;
      }
    } else {
      builder.get().setResultOfPassingTestecase(testcasePass);
      builder.get().setSubmitTime(LocalDateTime.now());
      builder.get().setRuntimebyMsec(submitTimeRunTimeDTO.getRunTimeByMsec());
      userscoreRepository.save(builder.get());
      return true;
    }
  }

  @Override
  public boolean addUserScore(UserScoreRequest userscoreRequest) {
    Optional<Event> event =
        eventRepository.findById(userscoreRequest.getEventId());
    Optional<User> user = userRepository.findById(userscoreRequest.getUserId());
    Optional<QuestionBank> question =
        questionRepository.findById(userscoreRequest.getQuestionId());
    UserScore userscore = UserScore.builder()//
        .event(event.get())//
        .user(user.get())//
        .question(question.get())//
        .resultOfPassingTestecase(
            userscoreRequest.getResultOfPassingTestecase() == 10 ? 3 : 0)//
        .status(userscoreRequest.getResultOfPassingTestecase() == 10
            ? "Pass All Test Cases"
            : "Fail")//
        .createdDate(LocalDateTime.now())//
        .updatedDate(LocalDateTime.now())//
        .build();

    userscoreRepository.save(userscore);
    return true;
  }

  @Override
  public List<UserScore> getAllUserScores() {
    return userscoreRepository.findAll();
  }

  @Override
  public UserScore getUserScoreById(Long id) {
    Optional<UserScore> userscoreData = userscoreRepository.findById(id);
    return userscoreData.get();
  }

  @Override
  public void deleteUserScore(Long id) {
    userscoreRepository.deleteById(id);
  }

  @Override
  public UserScoreResult getUserTestCaseByEventId(Long eventId) {
    List<UserScore> target = userscoreRepository.findByEventId(eventId);

    Map<Long, UserScoreResult.UserResult> userResultMap = new HashMap<>();

    for (UserScore userScore : target) {
      Optional<User> userOptional =
          userRepository.findById(userScore.getUser().getId());
      if (!userResultMap.containsKey(userScore.getUser().getId())) {
        UserScoreResult.UserResult userResult = new UserScoreResult.UserResult();
        userResult.setName(userOptional.get().getUserName()); // Assuming user id as name
        // passingTestCaseNumber
        userResult.setPassingTestCaseNumber(new HashMap<>());
        userResultMap.put(userScore.getUser().getId(), userResult);
        // score
        userResult.setScore(new HashMap<>());
        userResultMap.put(userScore.getUser().getId(), userResult);
        // submitTime
        userResult.setSubmitTime(new HashMap<>());
        userResultMap.put(userScore.getUser().getId(), userResult);
        // runtime
        userResult.setRuntime(new HashMap<>());
        userResultMap.put(userScore.getUser().getId(), userResult);

      }

      String questionKey = "Q" + userScore.getQuestion().getQuestionId();
      int score = userScore.getResultOfPassingTestecase() == 10 ? 3 : 0;
      int bonus30Mins = Integer.valueOf(userScore.getBonusUnder30Mins());
      int runTimeBonus =
          Integer.valueOf(userScore.getBonusWithinQuestionRuntime());
      int total = score + bonus30Mins + runTimeBonus;
      userResultMap.get(userScore.getUser().getId()).getPassingTestCaseNumber()
          .put(questionKey, userScore.getResultOfPassingTestecase());
      UserScoreResult.UserResult userResult =
          userResultMap.get(userScore.getUser().getId());
      userResult.getScore().put(questionKey, total);
      // submit time
      LocalDateTime submitTime = userScore.getSubmitTime();
      userResult.getSubmitTime().put(questionKey, submitTime);
      // rutime
      String runtime = String.valueOf(userScore.getRuntimebyMsec());
      userResult.getRuntime().put(questionKey, runtime);

    }

    List<UserScoreResult.UserResult> userResults =
        new ArrayList<>(userResultMap.values());

    UserScoreResult userScoreDTO = new UserScoreResult();
    userScoreDTO.setEventId(eventId.intValue());
    userScoreDTO.setResult(userResults);

    return userScoreDTO;
  }

}
