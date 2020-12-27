package com.carbonsensors.model;

import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
public class Sensor {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Status status;

  @OneToMany(
      mappedBy = "sensor",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL
  )
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<Alert> alerts;
}
