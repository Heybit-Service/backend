package com.heybit.backend.presentation.vote;


import com.heybit.backend.application.service.VoteService;
import com.heybit.backend.domain.vote.VoteResultType;
import com.heybit.backend.global.response.ApiResponseEntity;
import com.heybit.backend.presentation.vote.dto.VotedPostResponse;
import com.heybit.backend.security.oauth.LoginUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/votes")
public class VoteController {

  private final VoteService voteService;

  @GetMapping("/my-votes")
  public ApiResponseEntity<List<VotedPostResponse>> getMyVotedPosts(@LoginUser Long userId) {
    List<VotedPostResponse> votedPosts = voteService.getMyVotedPosts(userId);
    return ApiResponseEntity.success(votedPosts);
  }

  @PostMapping("/{votePostId}/vote")
  public ApiResponseEntity<Void> vote(
      @PathVariable Long votePostId,
      @RequestParam VoteResultType result,
      @LoginUser Long userId
  ) {
    voteService.vote(votePostId, userId, result);
    return ApiResponseEntity.success();
  }

  @DeleteMapping("/{votePostId}/vote")
  public ApiResponseEntity<Void> cancelVote(
      @PathVariable Long votePostId,
      @LoginUser Long userId
  ) {
    voteService.cancelVote(votePostId, userId);
    return ApiResponseEntity.success();
  }
}
