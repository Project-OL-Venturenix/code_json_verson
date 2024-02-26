package com.venturenix.cmc.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.venturenix.cmc.entity.QuestionBank;
import com.venturenix.cmc.payload.request.QuestionRequest;
import com.venturenix.cmc.payload.response.QuestionResponse;
import jakarta.validation.Valid;

public interface QuestionOperation {
  
  @PostMapping("/question/add")
  public ResponseEntity<?> addQuestion(
  @Valid  @RequestBody QuestionRequest questionRequest);

  @GetMapping("/questions")
  public ResponseEntity<List<QuestionBank>> getAllQuestions();

  @GetMapping("/question/{id}")
  public ResponseEntity<QuestionResponse> getQuestionById(
    @PathVariable("id") String id);

  @PutMapping("/question/{id}")
  public ResponseEntity<QuestionBank> updateQuestion(
    @PathVariable("id") long id, @RequestBody QuestionBank question);

  @DeleteMapping("/question/{id}")
  public ResponseEntity<?> deleteQuestion(@PathVariable("id") long id);
}
