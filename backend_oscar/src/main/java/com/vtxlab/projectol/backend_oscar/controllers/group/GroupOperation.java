package com.vtxlab.projectol.backend_oscar.controllers.group;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.vtxlab.projectol.backend_oscar.entity.group.Group;
import com.vtxlab.projectol.backend_oscar.payload.response.group.GroupUserDTO;
import com.vtxlab.projectol.backend_oscar.payload.response.user.UserScoreDTO;

public interface GroupOperation {
  // @PostMapping("/groups/add")
  // ResponseEntity<?> addGroup(@Valid @RequestBody GroupRequest groupRequest);

  @PostMapping("/groups/add")
  @ResponseStatus(HttpStatus.CREATED)
  boolean addGroup(@RequestParam String eventID, String userID, String groupId);

  @GetMapping("/groups")
  ResponseEntity<List<Group>> getAllGroups();

  @GetMapping("/groups/{id}")
  @ResponseStatus(HttpStatus.CREATED)
  GroupUserDTO getGroupById(@PathVariable("id") String id);

  @GetMapping("/groups/eventid/{eventid}")
  @ResponseStatus(HttpStatus.FOUND)
  UserScoreDTO getGroupScoreByEventId(@PathVariable String eventid);

  @PutMapping("/groups/{id}")
  ResponseEntity<Group> updateGroup(@PathVariable("id") long id,
      @RequestBody Group group);

  @DeleteMapping("/groups/{id}")
  ResponseEntity<?> deleteGroup(@PathVariable("id") long id);

}
