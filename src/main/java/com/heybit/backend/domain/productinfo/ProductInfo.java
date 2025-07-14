package com.heybit.backend.domain.productinfo;

import com.heybit.backend.domain.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductInfo extends BaseTimeEntity {

  @Id
  @GeneratedValue
  private Long id;

  private String name;
  private int amount;
  private String description;
  private String imageUrl;

  @Enumerated(EnumType.STRING)
  private Category category;

}
