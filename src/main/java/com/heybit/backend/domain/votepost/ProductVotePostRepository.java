package com.heybit.backend.domain.votepost;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVotePostRepository extends JpaRepository<ProductVotePost, Long> {

  Optional<ProductVotePost> findByProductTimerId(Long aLong);
}
