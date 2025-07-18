package com.heybit.backend.domain.vote;


import com.heybit.backend.domain.BaseTimeEntity;
import com.heybit.backend.domain.user.User;
import com.heybit.backend.domain.votepost.ProductVotePost;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_vote_post_user", columnNames = {"product_vote_post_id",
            "user_id"})
    }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vote extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_vote_post_id", nullable = false)
  private ProductVotePost productVotePost;

  private Boolean result;
}
