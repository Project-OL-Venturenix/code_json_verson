package com.vtxlab.projectol.backend_oscar.service.user;

import java.util.List;
import com.vtxlab.projectol.backend_oscar.entity.user.UserScore;
import com.vtxlab.projectol.backend_oscar.payload.request.question.SubmitTimeRunTimeDTO;
import com.vtxlab.projectol.backend_oscar.payload.request.user.UserScoreRequest;
import com.vtxlab.projectol.backend_oscar.payload.response.user.UserScoreResult;

public interface UserScoreService {

    boolean addScore(Long eventId, //
            Long userid, //
            Long questionid, //
            Integer testcasePass,
            SubmitTimeRunTimeDTO submitTimeRunTimeDTO);

    boolean addUserScore(UserScoreRequest userscoreRequest);

    List<UserScore> getAllUserScores();

    UserScore getUserScoreById(Long id);

    void deleteUserScore(Long id);

    UserScoreResult getUserTestCaseByEventId(Long eventId);
}
