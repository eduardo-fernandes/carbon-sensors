package com.carbonsensors.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
public class ConfigurationProperties {

  @Value("${co2Level.threshold}")
  private Integer co2LevelThreshold;

  @Value("${consecutive.measurements.for.alert}")
  private Integer consecutiveMeasurementsForAlert;

  @Value("${consecutive.measurements.for.ok}")
  private Integer consecutiveMeasurementsForOk;

  @Value("${average.level.co2.days}")
  private Integer averageLevelCo2Days;

  @Value("${max.level.co2.days}")
  private Integer maxLevelCo2Days;
}
