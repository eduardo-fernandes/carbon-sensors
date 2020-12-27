package com.carbonsensors.model.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.carbonsensors.config.ConfigurationProperties;
import com.carbonsensors.model.Sensor;
import com.carbonsensors.model.Status;
import com.carbonsensors.model.projection.SensorMetrics;
import com.carbonsensors.repository.MeasurementRepository;
import com.carbonsensors.repository.SensorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

class SensorServiceTest {

  private static final UUID SENSOR_ID = UUID.randomUUID();
  private static final Integer METRICS_CO2_DAYS = 30;

  @Mock
  private MeasurementRepository measurementRepository;
  @Mock
  private SensorRepository sensorRepository;

  private SensorService sensorService;

  @BeforeEach
  void setup() {
    initMocks(this);

    ConfigurationProperties configurationProperties = new ConfigurationProperties();
    configurationProperties.setMetricsCo2Days(METRICS_CO2_DAYS);

    sensorService = new SensorService(measurementRepository, sensorRepository, configurationProperties);
  }

  @Test
  void createSensor_whenSystemIsOk_thenCreateSensor() {
    Sensor sensor = Sensor.builder()
        .id(UUID.randomUUID())
        .status(Status.OK)
        .build();
    when(sensorRepository.save(any())).thenReturn(sensor);

    Sensor result = sensorService.createSensor();
    assertNotNull(sensor);
    assertEquals(result, sensor);
    verify(sensorRepository).save(any());
  }

  @Test
  void findSensorById_whenSensorIdIsValid_thenReturnSensor() {
    Sensor sensor = Sensor.builder()
        .id(SENSOR_ID)
        .build();

    when(sensorRepository.findById(SENSOR_ID)).thenReturn(Optional.of(sensor));

    Sensor result = sensorService.findSensorById(SENSOR_ID);
    assertNotNull(result);
    assertEquals(sensor, result);
  }

  @Test
  void findSensorById_whenSensorIdIsNull_thenThrowException() {
    assertThrows(IllegalArgumentException.class, () -> sensorService.findSensorById(null));
  }

  @Test
  void findSensorById_whenSensorIdIsInvalid_thenThrowException() {
    when(sensorRepository.findById(SENSOR_ID)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> sensorService.findSensorById(null));
  }

  @Test
  void findMetricsBySensorId_whenSensorIdIsNull_thenThrowException() {
    assertThrows(IllegalArgumentException.class, () -> sensorService.findMetricsBySensorId(null));
  }

  @Test
  void findMetricsBySensorId_whenSensorIdIsValid_thenReturnMetrics() {
    SensorMetrics sensorMetrics = new SensorMetrics(1d, 1d);
    when(measurementRepository.computeMetricsById(any(), any()))
        .thenReturn(sensorMetrics);

    SensorMetrics result = sensorService.findMetricsBySensorId(SENSOR_ID);

    assertNotNull(result);
    assertEquals(sensorMetrics, result);
    verify(measurementRepository).computeMetricsById(any(), any());
  }
}