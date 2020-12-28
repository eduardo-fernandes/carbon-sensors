package com.carbonsensors.service;

import static com.google.common.base.Preconditions.checkArgument;

import com.carbonsensors.config.ConfigurationProperties;
import com.carbonsensors.model.Sensor;
import com.carbonsensors.model.Status;
import com.carbonsensors.model.projection.SensorMetrics;
import com.carbonsensors.repository.MeasurementRepository;
import com.carbonsensors.repository.SensorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class SensorService {

  private final MeasurementRepository measurementRepository;
  private final SensorRepository sensorRepository;
  private final ConfigurationProperties configurationProperties;

  public SensorService(MeasurementRepository measurementRepository,
                       SensorRepository sensorRepository,
                       ConfigurationProperties configurationProperties) {
    this.measurementRepository = measurementRepository;
    this.sensorRepository = sensorRepository;
    this.configurationProperties = configurationProperties;
  }

  public Sensor createSensor() {
    Sensor sensor = Sensor.builder()
        .id(UUID.randomUUID())
        .status(Status.OK)
        .build();

    return sensorRepository.save(sensor);
  }

  public Sensor findSensorById(UUID sensorId) {
    checkArgument(sensorId != null, "Sensor Id cannot be null");

    return sensorRepository.findById(sensorId)
        .orElseThrow(() -> new IllegalArgumentException("Sensor entity could be be found given the id: " + sensorId));
  }

  @Transactional
  public SensorMetrics findMetricsBySensorId(UUID sensorId) {
    checkArgument(sensorId != null, "Sensor Id cannot be null");

    Integer metricsCo2Days = configurationProperties.getMetricsCo2Days();

    return measurementRepository.computeMetricsById(sensorId, ZonedDateTime.now().minusDays(metricsCo2Days));
  }
}
