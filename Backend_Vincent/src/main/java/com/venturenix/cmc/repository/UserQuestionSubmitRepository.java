package com.venturenix.cmc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.venturenix.cmc.models.UserQuestionSubmit;
import java.util.List;

@Repository
public interface UserQuestionSubmitRepository extends JpaRepository<UserQuestionSubmit, Long> {

    Optional<UserQuestionSubmit> findById(int id);

        List<UserQuestionSubmit> findAll();
    
    

  
}