package com.vtxlab.projectol.backend_oscar.controllers.user;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.vtxlab.projectol.backend_oscar.entity.user.User;
import com.vtxlab.projectol.backend_oscar.payload.response.user.UserDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface UserOperation {

  @PatchMapping("/join")
  ResponseEntity<?> addUserInEvent(//
      @RequestParam("userId") Long userId, //
      @RequestParam("eventId") Long eventId);

  // @GetMapping("/usertestcases/eventId/{eventId}")
  // ResponseEntity<UserScoreResult> getUserTestCaseByEventId(
  // @PathVariable String eventId);

  @GetMapping("/user/eventId/{eventId}")
  @ResponseStatus(HttpStatus.OK)
  UserDTO getUserByEventId(@PathVariable String eventId,
      HttpServletRequest request);
  // ResponseEntity<User> getUserByEventId(@PathVariable String eventId,String jwt);

  @GetMapping("/users")
  ResponseEntity<List<UserDTO>> getAllUsers();

  @GetMapping("/users/{id}")
  ResponseEntity<UserDTO> getUserById(@PathVariable long id);

  @PutMapping("/users/{id}")
  ResponseEntity<User> updateUser(@PathVariable long id,
      @RequestBody User user);

  @DeleteMapping("/users/{id}")
  ResponseEntity<?> deleteUser(@PathVariable long id);
}
