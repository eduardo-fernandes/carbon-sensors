package com.carbonsensors.model;

import java.time.LocalDateTime;
import java.util.Set;
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
import lombok.Data;

@Data
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
  private Set<Measurement> measurements;
}
