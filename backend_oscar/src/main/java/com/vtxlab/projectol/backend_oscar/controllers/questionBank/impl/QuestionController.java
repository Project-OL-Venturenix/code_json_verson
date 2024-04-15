package com.vtxlab.projectol.backend_oscar.controllers.questionBank.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.vtxlab.projectol.backend_oscar.controllers.questionBank.QuestionOperation;
import com.vtxlab.projectol.backend_oscar.entity.event.Event;
import com.vtxlab.projectol.backend_oscar.entity.questionBank.QuestionBank;
import com.vtxlab.projectol.backend_oscar.entity.questionBank.TestCase;
import com.vtxlab.projectol.backend_oscar.payload.Mapper;
import com.vtxlab.projectol.backend_oscar.payload.request.question.QuestionRequest;
import com.vtxlab.projectol.backend_oscar.payload.response.question.QuestionBankDTO;
import com.vtxlab.projectol.backend_oscar.payload.response.question.QuestionResponse;
import com.vtxlab.projectol.backend_oscar.payload.response.question.TestCaseDTO;
import com.vtxlab.projectol.backend_oscar.payload.response.user.MessageResponse;
import com.vtxlab.projectol.backend_oscar.repository.event.EventRepository;
import com.vtxlab.projectol.backend_oscar.repository.questionBank.QuestionBankRepository;
import com.vtxlab.projectol.backend_oscar.repository.questionBank.TestCaseRepository;
import com.vtxlab.projectol.backend_oscar.repository.user.RoleRepository;
import com.vtxlab.projectol.backend_oscar.repository.user.UserRepository;
import com.vtxlab.projectol.backend_oscar.security.jwt.JwtUtils;
import com.vtxlab.projectol.backend_oscar.service.questionBank.QuestionBankService;
import com.vtxlab.projectol.backend_oscar.service.questionBank.TestCaseService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class QuestionController implements QuestionOperation {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private QuestionBankService questionBankService;

  @Autowired
  private TestCaseService testCaseService;

  @Autowired
  QuestionBankRepository questionRepository;

  @Autowired
  TestCaseRepository testCaseRepository;

  @Autowired
  EventRepository eventRepository;


  public ResponseEntity<?> addQuestion(QuestionRequest questionRequest) {
    QuestionBank question = QuestionBank.builder()//
        .question(questionRequest.getQuestion())//
        .testComputeCase(questionRequest.getTestComputeCase())//
        .methodSignatures(questionRequest.getMethodSignatures())//
        .bonusRuntime(questionRequest.getBonusRuntime())//
        .createdDate(java.time.LocalDateTime.now())//
        .createdBy(questionRequest.getCreatedBy())//
        .updatedDate(java.time.LocalDateTime.now())//
        .updatedBy(questionRequest.getUpdatedBy())//
        .build();

    questionBankService.save(question);
    return ResponseEntity.ok(new MessageResponse("Add Question successfully!"));

  }

  public ResponseEntity<List<QuestionBankDTO>> getAllQuestions() {
    try {
      List<QuestionBankDTO> questions = questionRepository.findAll().stream()
          .map(e -> Mapper.map(e)).collect(Collectors.toList());
      if (questions.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }
      return new ResponseEntity<>(questions, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

    }

  }

  @Override
  public ResponseEntity<QuestionResponse> getQuestionRunById(String id) {
    Long questionId = Long.parseLong(id);
    Optional<QuestionBank> questionData =
        questionRepository.findById(questionId);
    log.info("questionData : ");

    Optional<List<TestCase>> testcaseData =
        Optional.of(testCaseRepository.getTestCaseByQuestionId(questionId));
    log.info("testcaseData : " + testcaseData.orElse(null));

    List<TestCaseDTO> testCases = testCaseRepository.findAll().stream()//
        .filter(e -> e.getQuestionBank().getQuestionId().equals(questionId))//
        .limit(3)//
        .map(e -> Mapper.map(e))//
        .collect(Collectors.toList());
    log.info("testCases : " + testCases.get(0));


    if (questionData.isPresent() && testcaseData.isPresent()) {
      QuestionResponse questionResponse = QuestionResponse.builder()
          .questionId(questionData.get().getQuestionId())//
          .classDeclaration(
              testCaseService.generateClassDeclaration(questionId))//
          .code(testCaseService.generateFullCode(questionId))//
          .mainMethod(testCaseService.generateMainMethod(questionId) + //
              testCaseService.generateTestCase(testCases, questionId))//
          .createdBy(questionData.get().getCreatedBy())//
          .updatedDate(questionData.get().getUpdatedDate())//
          .updatedBy(questionData.get().getUpdatedBy())//
          .build();
      return new ResponseEntity<>(questionResponse, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @Override
  public ResponseEntity<QuestionResponse> getQuestionSubmitById(String id) {
    Long questionId = Long.parseLong(id);
    Optional<QuestionBank> questionData =
        questionRepository.findById(questionId);
    log.info("questionData : ");

    // Optional<TestCase> testcaseData = testCaseRepository.findAll().stream()//
    // .filter(e -> e.getQuestionBank().getQuestionId().equals(questionId))//
    // .findFirst();
    Optional<List<TestCase>> testcaseData =
        Optional.of(testCaseRepository.getTestCaseByQuestionId(questionId));
    log.info("testcaseData : " + testcaseData.orElse(null));

    List<TestCaseDTO> testCases = testCaseRepository.findAll().stream()//
        .filter(e -> e.getQuestionBank().getQuestionId().equals(questionId))//
        .map(e -> Mapper.map(e))//
        .collect(Collectors.toList());
    log.info("testCases : " + testCases.get(0));


    if (questionData.isPresent() && testcaseData.isPresent()) {
      QuestionResponse questionResponse = QuestionResponse.builder()
          .questionId(questionData.get().getQuestionId())//
          .classDeclaration(
              testCaseService.generateClassDeclaration(questionId))//
          .code(testCaseService.generateFullCode(questionId))//
          // .mainMethod(testcaseData.get().generateMainMethod()
          // + testcaseData.get().getMainMethod() + "\n"
          // + testcaseData.get().generateEndCodeBlock())//
          .mainMethod(testCaseService.generateMainMethod(questionId) + //
              testCaseService.generateTestCase(testCases, questionId))//
          .createdBy(questionData.get().getCreatedBy())//
          .updatedDate(questionData.get().getUpdatedDate())//
          .updatedBy(questionData.get().getUpdatedBy())//
          .build();
      return new ResponseEntity<>(questionResponse, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @Override
  public ResponseEntity<QuestionBank> updateQuestion(String id,
      QuestionBank question) {
    Long questionId = Long.parseLong(id);
    Optional<QuestionBank> questionData =
        questionRepository.findById(questionId);

    if (questionData.isPresent()) {
      QuestionBank builder = QuestionBank.builder()//
          .question(question.getQuestion())//
          .testComputeCase(question.getTestComputeCase())//
          .methodSignatures(question.getMethodSignatures())//
          .createdDate(question.getCreatedDate())//
          .createdBy(question.getCreatedBy())//
          .updatedDate(LocalDateTime.now())//
          .updatedBy(question.getUpdatedBy())//
          .build();

      return new ResponseEntity<>(questionRepository.save(builder),
          HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  public ResponseEntity<?> deleteQuestion(String id) {
    Long questionId = Long.parseLong(id);
    try {
      questionRepository.deleteById(questionId);
      return ResponseEntity
          .ok(new MessageResponse("Delete Question " + id + " successfully!"));
    } catch (Exception e) {
      return ResponseEntity
          .ok(new MessageResponse("HttpStatus.INTERNAL_SERVER_ERROR"));
    }
  }

  // 每個testcase1分，每一題3分-》passalltestcase 3分，bonus（within 2ms） 1分，bonus (under 30mins) 1分 ，total 5分 ，alltestcase pass-》3 ，9/10 -》0

  @Override
  public ResponseEntity<?> addQuestionInEvent(Long eventId, Long questionId) {
    Optional<Event> eventOptional = eventRepository.findById(eventId);
    Optional<QuestionBank> questionOptional =
        questionRepository.findById(questionId);

    if (eventOptional.isPresent() && questionOptional.isPresent()) {
      Event event = eventOptional.get();
      QuestionBank question = questionOptional.get();

      event.getQuestions().add(question);
      log.info("event : " + event);
      eventRepository.save(event);

      return ResponseEntity
          .ok(new MessageResponse("Question added to event successfully!"));
    } else {
      return ResponseEntity.notFound().build();
    }
  }


  @Override
  public ResponseEntity<Set<QuestionBankDTO>> getQuestionByEventId(
      String eventId) {
    Set<QuestionBankDTO> result =
        questionRepository.findByEventsId(Long.valueOf(eventId)).stream()//
            .map(e -> Mapper.map(e))//
            .collect(Collectors.toSet());
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<QuestionResponse> generateQuestionById(String id) {
    Long questionId = Long.parseLong(id);
    QuestionResponse result =
        questionBankService.generateQuestionBank(questionId);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }
}

