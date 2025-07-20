package com.heybit.backend.presentation.votepost;

import com.heybit.backend.application.service.ProductVotePostService;
import com.heybit.backend.presentation.votepost.dto.MyVotePostResponse;
import com.heybit.backend.presentation.votepost.dto.ProductVotePostResponse;
import com.heybit.backend.security.oauth.LoginUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/votes")
public class ProductVotePostController {

  private final ProductVotePostService votePostService;

  @GetMapping
  public ResponseEntity<List<ProductVotePostResponse>> getAllInProgressPosts(
      @LoginUser Long userId
  ) {
    return ResponseEntity.ok(votePostService.getAllInProgressPosts(userId));
  }

  @GetMapping("/my/progress")
  public ResponseEntity<List<MyVotePostResponse>> getMyInProgressVotePosts(
      @LoginUser Long userId
  ) {
    return ResponseEntity.ok(votePostService.getMyInProgressVotePosts(userId));
  }

  @DeleteMapping("/{votePostId}")
  public ResponseEntity<Void> deleteVotePost(
      @PathVariable Long votePostId,
      @LoginUser Long userId
  ) {
    votePostService.deleteVotePost(votePostId, userId);
    return ResponseEntity.ok().build();
  }
}
