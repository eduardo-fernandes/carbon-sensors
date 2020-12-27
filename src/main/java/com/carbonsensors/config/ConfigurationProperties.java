package com.carbonsensors.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Component
public class ConfigurationProperties {

  @Value("${co2Level.threshold}")
  private Integer co2LevelThreshold;

  @Value("${consecutive.measurements.for.alert}")
  private Integer consecutiveMeasurementsForAlert;

  @Value("${consecutive.measurements.for.ok}")
  private Integer consecutiveMeasurementsForOk;

  @Value("${metrics.co2.days}")
  private Integer metricsCo2Days;
}
