package com.carbonsensors.model.projection;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SensorMetrics {

  private final Double maxLastNDays;
  private final Double averageLastNDays;
}
