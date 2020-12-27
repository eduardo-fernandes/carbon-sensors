package com.carbonsensors.model.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.carbonsensors.config.ConfigurationProperties;
import com.carbonsensors.model.Alert;
import com.carbonsensors.model.Measurement;
import com.carbonsensors.model.Sensor;
import com.carbonsensors.model.Status;
import com.carbonsensors.repository.AlertRepository;
import com.carbonsensors.repository.MeasurementRepository;
import com.carbonsensors.repository.SensorRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

class MeasurementServiceTest {

  private static final LocalDateTime NOW = LocalDateTime.now();
  private static final UUID SENSOR_ID = UUID.randomUUID();
  private static final Double CO2_QUANTITY_LIMIT = 2000d;

  @Mock
  private MeasurementRepository measurementRepository;
  @Mock
  private SensorRepository sensorRepository;
  @Mock
  private AlertRepository alertRepository;

  private ConfigurationProperties configurationProperties;

  private MeasurementService measurementService;

  @BeforeEach
  void setup() {
    initMocks(this);

    configurationProperties = new ConfigurationProperties();
    configurationProperties.setCo2LevelThreshold((CO2_QUANTITY_LIMIT.intValue()));
    configurationProperties.setConsecutiveMeasurementsForAlert(3);
    configurationProperties.setConsecutiveMeasurementsForOk(3);

    measurementService =
        new MeasurementService(measurementRepository, sensorRepository, alertRepository, configurationProperties);
  }

  @Test
  void createMeasurement_whenParametersAreNull_thenThrowException() {
    assertAll("Failed testing invalid parameters", () -> {
      Assertions.assertThrows(IllegalArgumentException.class,
          () -> measurementService.createMeasurement(null, CO2_QUANTITY_LIMIT, NOW));
      Assertions.assertThrows(IllegalArgumentException.class,
          () -> measurementService.createMeasurement(SENSOR_ID, null, NOW));
      Assertions.assertThrows(IllegalArgumentException.class,
          () -> measurementService.createMeasurement(SENSOR_ID, CO2_QUANTITY_LIMIT, null));
    });
  }

  @Test
  void createMeasurement_whenSensorIdIsInvalid_thenThrowException() {
    when(sensorRepository.findById(SENSOR_ID)).thenReturn(Optional.empty());

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> measurementService.createMeasurement(SENSOR_ID, CO2_QUANTITY_LIMIT, NOW));
  }

  @Test
  void createMeasurement_whenParametersAreOk_thenReturnCreatedMeasurement() {
    Sensor sensor = createSensor();

    Measurement measurement = createMeasurement(CO2_QUANTITY_LIMIT, sensor);

    MeasurementService measurementServiceSpy = spy(measurementService);

    when(sensorRepository.findById(SENSOR_ID)).thenReturn(Optional.of(sensor));
    when(measurementRepository.save(measurement)).thenReturn(measurement);
    doNothing().when(measurementServiceSpy).updateSensorStatus(sensor, NOW);

    Measurement result = measurementServiceSpy.createMeasurement(SENSOR_ID, CO2_QUANTITY_LIMIT, NOW);
    assertEquals(measurement, result);
    verify(sensorRepository).findById(SENSOR_ID);
    verify(measurementRepository).save(measurement);
  }

  @Test
  void updateSensorStatus_whenLast3AlertsAreAboveThreshold_thenSaveSensorWithAlertStatus() {
    Sensor sensor = createSensor();
    List<Measurement> measurements = Arrays.asList(
        createMeasurementAboveThreshold(sensor),
        createMeasurementAboveThreshold(sensor),
        createMeasurementAboveThreshold(sensor)
    );

    when(sensorRepository.save(any())).thenReturn(sensor);
    when(measurementRepository.findBySensorIdOrderByCreatedDesc(SENSOR_ID,
        PageRequest.of(0, configurationProperties.getConsecutiveMeasurementsForAlert()))).thenReturn(measurements);

    measurementService.updateSensorStatus(sensor, NOW);

    assertEquals(Status.ALERT, sensor.getStatus());
    assertEquals(1, sensor.getAlerts().size());
    Alert createdAlert = sensor.getAlerts().iterator().next();
    assertEquals(sensor, createdAlert.getSensor());
    assertEquals(3, createdAlert.getMeasurements().size());
    assertTrue(
        createdAlert.getMeasurements().stream().allMatch(m -> m.equals(createMeasurementAboveThreshold(sensor))));

    verify(sensorRepository).save(any());
    verify(measurementRepository).findBySensorIdOrderByCreatedDesc(SENSOR_ID,
        PageRequest.of(0, configurationProperties.getConsecutiveMeasurementsForAlert()));
  }

  @Test
  void updateSensorStatus_whenLast3AlertsAreAboveThresholdAndSensorHasStatusAlert_thenAddMeasurementToAlert() {
    Sensor sensor = createSensor();
    sensor.setStatus(Status.ALERT);
    List<Measurement> measurements = Arrays.asList(
        createMeasurementAboveThreshold(sensor),
        createMeasurementAboveThreshold(sensor),
        createMeasurementAboveThreshold(sensor)
    );

    Alert alert = Alert.builder()
        .sensor(sensor)
        .measurements(new ArrayList<>())
        .build();

    when(sensorRepository.save(any())).thenReturn(sensor);
    when(measurementRepository.findBySensorIdOrderByCreatedDesc(SENSOR_ID,
        PageRequest.of(0, configurationProperties.getConsecutiveMeasurementsForAlert()))).thenReturn(measurements);
    when(alertRepository.findTop1BySensorIdOrderByCreatedDesc(sensor.getId())).thenReturn(Optional.of(alert));
    when(alertRepository.save(alert)).thenReturn(alert);

    measurementService.updateSensorStatus(sensor, NOW);

    assertEquals(Status.ALERT, sensor.getStatus());
    assertEquals(0, sensor.getAlerts().size());
    assertEquals(1, alert.getMeasurements().size());

    verify(sensorRepository, never()).save(any());
    verify(measurementRepository).findBySensorIdOrderByCreatedDesc(SENSOR_ID,
        PageRequest.of(0, configurationProperties.getConsecutiveMeasurementsForAlert()));
    verify(alertRepository).findTop1BySensorIdOrderByCreatedDesc(sensor.getId());
    verify(alertRepository).save(alert);
  }

  @Test
  void updateSensorStatus_whenLast3AlertsAreBelowThreshold_thenSaveSensorWithOkStatus() {
    Sensor sensor = createSensor();
    List<Measurement> measurements = Arrays.asList(
        createMeasurementBelowThreshold(sensor),
        createMeasurementBelowThreshold(sensor),
        createMeasurementBelowThreshold(sensor)
    );

    when(sensorRepository.save(any())).thenReturn(sensor);
    when(measurementRepository.findBySensorIdOrderByCreatedDesc(SENSOR_ID,
        PageRequest.of(0, configurationProperties.getConsecutiveMeasurementsForAlert()))).thenReturn(measurements);

    measurementService.updateSensorStatus(sensor, NOW);

    assertEquals(Status.OK, sensor.getStatus());
    verify(sensorRepository).save(any());
    verify(measurementRepository).findBySensorIdOrderByCreatedDesc(SENSOR_ID,
        PageRequest.of(0, configurationProperties.getConsecutiveMeasurementsForAlert()));
  }

  @Test
  void updateSensorStatus_whenLastAlertIsBelowThresholdAndTwoAbove_thenSaveSensorWithOkStatus() {
    Sensor sensor = createSensor();
    List<Measurement> measurements = Arrays.asList(
        createMeasurementBelowThreshold(sensor),
        createMeasurementAboveThreshold(sensor),
        createMeasurementAboveThreshold(sensor)
    );

    when(sensorRepository.save(any())).thenReturn(sensor);
    when(measurementRepository.findBySensorIdOrderByCreatedDesc(SENSOR_ID,
        PageRequest.of(0, configurationProperties.getConsecutiveMeasurementsForAlert()))).thenReturn(measurements);

    measurementService.updateSensorStatus(sensor, NOW);

    assertEquals(Status.OK, sensor.getStatus());
    verify(sensorRepository).save(any());
    verify(measurementRepository).findBySensorIdOrderByCreatedDesc(SENSOR_ID,
        PageRequest.of(0, configurationProperties.getConsecutiveMeasurementsForAlert()));
  }

  @Test
  void updateSensorStatus_whenLastAlertIsAboveThresholdAndTwoBelow_thenSaveSensorWithWarningStatus() {
    Sensor sensor = createSensor();
    List<Measurement> measurements = Arrays.asList(
        createMeasurementAboveThreshold(sensor),
        createMeasurementBelowThreshold(sensor),
        createMeasurementBelowThreshold(sensor)
    );

    when(sensorRepository.save(any())).thenReturn(sensor);
    when(measurementRepository.findBySensorIdOrderByCreatedDesc(SENSOR_ID,
        PageRequest.of(0, configurationProperties.getConsecutiveMeasurementsForAlert()))).thenReturn(measurements);

    measurementService.updateSensorStatus(sensor, NOW);

    assertEquals(Status.WARM, sensor.getStatus());
    verify(sensorRepository).save(any());
    verify(measurementRepository).findBySensorIdOrderByCreatedDesc(SENSOR_ID,
        PageRequest.of(0, configurationProperties.getConsecutiveMeasurementsForAlert()));
  }

  private Measurement createMeasurementBelowThreshold(Sensor sensor) {
    return createMeasurement(CO2_QUANTITY_LIMIT - 1, sensor);
  }

  private Measurement createMeasurementAboveThreshold(Sensor sensor) {
    return createMeasurement(CO2_QUANTITY_LIMIT + 1, sensor);
  }

  private Measurement createMeasurement(double co2Quantity, Sensor sensor) {
    return Measurement.builder()
        .co2Quantity(co2Quantity)
        .created(NOW)
        .sensor(sensor)
        .build();
  }

  private Sensor createSensor() {
    return Sensor.builder()
        .id(SENSOR_ID)
        .alerts(new HashSet<>())
        .build();
  }
}