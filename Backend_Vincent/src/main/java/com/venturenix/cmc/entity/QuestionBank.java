package com.venturenix.cmc.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List; // Add missing import statement for List
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "Question_Bank")
public class QuestionBank implements Serializable{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonProperty("question_id")
  @Column(name = "question_id")
  private Long questionId;

  @OneToMany(cascade = CascadeType.ALL)
  private List<TestCase> testCase; // Change field type to List<TestCase>

  @Column(columnDefinition = "TEXT")
  private String question;

  private LocalDateTime createddate;
  private Integer createdby;
  private LocalDateTime updateddate;
  private Integer updatedby;

}
