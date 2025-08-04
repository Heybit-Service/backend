package com.heybit.backend.domain.usersurvey;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum PurchaseTrigger {

  SELF_REWARD("나에게 보상을 주어야 할 때"),
  DISTRACTED_BY_OTHER_PRODUCTS("쇼핑 중 다른 상품이 눈에 들어왔을 때"),
  INFLUENCED_BY_SNS_OR_YOUTUBE("즐겨보는 SNS/유튜브에서 추천해줄 때"),
  BORED_OR_FREE_TIME("심심하거나 시간적 여유가 생겼을 때"),
  FRIEND_RECOMMENDATION("지인이 계획에 없던 물건을 추천해주었을 때");

  private final String label;
}
