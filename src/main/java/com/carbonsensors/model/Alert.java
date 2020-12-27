package com.carbonsensors.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Alert {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @Column(nullable = false)
  private LocalDateTime created;

  @ManyToOne(fetch = FetchType.LAZY)
  private Sensor sensor;

  @OneToMany(
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY
  )
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @OrderBy(value = "created desc")
  private List<Measurement> measurements;
}
