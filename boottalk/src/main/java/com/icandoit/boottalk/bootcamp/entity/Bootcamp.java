package com.icandoit.boottalk.bootcamp.entity;

import com.icandoit.boottalk.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bootcamp")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bootcamp extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "t_bt_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "t_tc_id", nullable = false)
  private TrainingCenter trainingCenter;

  @Column(name = "bt_name", nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "bt_category", nullable = false)
  private CategoryType category;

  @Column(name = "bt_degree", nullable = false)
  private int degree;

  @Column(name = "bt_region", nullable = false)
  private String region;

  @Column(name = "bt_capacity", nullable = false)
  private int capacity;

  @Column(name = "bt_cost", nullable = false)
  private boolean cost;

  @Column(name = "bt_start_date", nullable = false)
  private LocalDate startDate;

  @Column(name = "bt_end_date", nullable = false)
  private LocalDate endDate;

  @Column(name = "bt_link", nullable = false)
  private String link;

}
