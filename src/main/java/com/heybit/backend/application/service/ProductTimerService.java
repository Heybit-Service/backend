package com.heybit.backend.application.service;


import com.heybit.backend.domain.timer.ProductTimer;
import com.heybit.backend.domain.timer.ProductTimerRepository;
import com.heybit.backend.domain.timer.TimerStatus;
import com.heybit.backend.domain.timerresult.ResultType;
import com.heybit.backend.domain.vote.VoteRepository;
import com.heybit.backend.domain.votepost.ProductVotePostRepository;
import com.heybit.backend.global.exception.ApiException;
import com.heybit.backend.global.exception.ErrorCode;
import com.heybit.backend.global.resolver.TimerStatusResolver;
import com.heybit.backend.presentation.timer.dto.ProductTimerDetailResponse;
import com.heybit.backend.presentation.timer.dto.ProductTimerResponse;
import com.heybit.backend.presentation.vote.dto.VoteStatsDto;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductTimerService {

  private final ProductTimerRepository productTimerRepository;
  private final ProductVotePostRepository productVotePostRepository;
  private final VoteRepository voteRepository;
  private final TimerResultService timerResultService;

  // 진행 중인 타이머
  @Transactional(readOnly = true)
  public List<ProductTimerResponse> getProgressAndWaitingTimers(Long userId) {
    List<ProductTimer> timers = productTimerRepository
        .findUncompletedTimersByUserOrderByWaitingFirstAndEndTimeDesc(userId);

    return timers.stream()
        .map(timer -> ProductTimerResponse.from(
            timer,
            productVotePostRepository.existsByProductTimerId(timer.getId())
        ))
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public ProductTimerDetailResponse getProductTimerDetail(Long userId, Long timerId) {
    ProductTimer timer = productTimerRepository.findById(timerId)
        .orElseThrow(() -> new ApiException(ErrorCode.TIMER_NOT_FOUND));

    String statusCode;
    if(timer.getStatus() == TimerStatus.COMPLETED) {
      ResultType result = timerResultService.findByProductTimerId(timerId).getResult();
      statusCode = TimerStatusResolver.resolveStatusCode(timer.getStatus(), result);
    } else {
      statusCode = TimerStatusResolver.resolveStatusCode(timer.getStatus(), null);
    }

    return productVotePostRepository.findByProductTimerId(timerId)
        .flatMap(votePost -> voteRepository.countBuyHoldByPostId(votePost.getId()))
        .map(VoteStatsDto::from)
        .map(dto -> ProductTimerDetailResponse.from(timer, dto, statusCode))
        .orElseGet(() -> ProductTimerDetailResponse.from(timer, statusCode));
  }

  public ProductTimer save(ProductTimer timer) {
    return productTimerRepository.save(timer);
  }

  public ProductTimer findById(Long id) {
    return productTimerRepository.findById(id)
        .orElseThrow(() -> new ApiException(ErrorCode.TIMER_NOT_FOUND));
  }

  void delete(ProductTimer timer) {
    productTimerRepository.delete(timer);
  }
}
