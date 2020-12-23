package com.carbonsensors.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.carbonsensors.model.Measurement;
import com.carbonsensors.model.Sensor;
import com.carbonsensors.model.Status;
import com.carbonsensors.model.projection.SensorMetrics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Set;

@DataJpaTest
class MeasurementRepositoryITCase {

  private static final String MEASUREMENT_TODAY = "measurementToday";
  private static final String MEASUREMENT_YESTERDAY = "measurementYesterday";
  private static final String MEASUREMENT_DAY_BEFORE_YESTERDAY = "measurementDayBeforeYesterday";
  private static final String MEASUREMENT_THREE_DAYS_AGO = "measurementThreeDaysAgo";
  
  @Autowired
  private MeasurementRepository measurementRepository;

  @Autowired
  private SensorRepository sensorRepository;

  @Test
  void computeMetricsById_whenDataIsOk_thenReturnComputedData() {
    LocalDateTime today = LocalDateTime.now();
    LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
    LocalDateTime dayBeforeYesterday = LocalDateTime.now().minusDays(2);

    Sensor sensor = create3Measurements(today, yesterday, dayBeforeYesterday);

    SensorMetrics sensorMetrics =
        measurementRepository.computeMetricsById(sensor.getId(), yesterday);

    assertNotNull(sensorMetrics);
    assertEquals(150, sensorMetrics.getMaxLastNDays());
    assertEquals(125, sensorMetrics.getAverageLastNDays());

    sensorMetrics = measurementRepository.computeMetricsById(sensor.getId(), dayBeforeYesterday);
    assertEquals(175, sensorMetrics.getMaxLastNDays());
    assertEquals(141.67d, Math.round(sensorMetrics.getAverageLastNDays() * 100d) / 100d);
  }

  @Test
  void findTop3BySensorIdOrderByCreatedDesc_whenExists3MeasurementPerSensor_thenReturnSet() {
    LocalDateTime today = LocalDateTime.now();
    LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
    LocalDateTime dayBeforeYesterday = LocalDateTime.now().minusDays(2);
    LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);

    Sensor sensor = create3Measurements(today, yesterday, dayBeforeYesterday);

    Measurement measurementDayBeforeYesterday = Measurement.builder()
        .created(threeDaysAgo)
        .co2Quantity(165d)
        .name(MEASUREMENT_THREE_DAYS_AGO)
        .sensor(sensor)
        .build();
    measurementRepository.save(measurementDayBeforeYesterday);

    Set<Measurement> measurements = measurementRepository.findTop3BySensorIdOrderByCreatedDesc(sensor.getId());
    assertNotNull(measurements);
    assertEquals(3, measurements.size());
    assertTrue(measurements.stream().anyMatch(m -> m.getName().equals(MEASUREMENT_TODAY)));
    assertTrue(measurements.stream().anyMatch(m -> m.getName().equals(MEASUREMENT_YESTERDAY)));
    assertTrue(measurements.stream().anyMatch(m -> m.getName().equals(MEASUREMENT_DAY_BEFORE_YESTERDAY)));
    assertTrue(measurements.stream().noneMatch(m -> m.getName().equals(MEASUREMENT_THREE_DAYS_AGO)));
  }

  private Sensor create3Measurements(LocalDateTime dateFirstMeasurement, LocalDateTime dateSecondMesurement,
                                     LocalDateTime dateThirdMeasurement) {
    Sensor sensor = Sensor.builder()
        .status(Status.OK)
        .build();

    sensor = sensorRepository.save(sensor);

    Measurement measurementToday = Measurement.builder()
        .created(dateFirstMeasurement)
        .co2Quantity(100d)
        .name(MEASUREMENT_TODAY)
        .sensor(sensor)
        .build();
    measurementRepository.save(measurementToday);

    Measurement measurementYesterday = Measurement.builder()
        .created(dateSecondMesurement)
        .co2Quantity(150d)
        .name(MEASUREMENT_YESTERDAY)
        .sensor(sensor)
        .build();
    measurementRepository.save(measurementYesterday);

    Measurement measurementDayBeforeYesterday = Measurement.builder()
        .created(dateThirdMeasurement)
        .co2Quantity(175d)
        .name(MEASUREMENT_DAY_BEFORE_YESTERDAY)
        .sensor(sensor)
        .build();
    measurementRepository.save(measurementDayBeforeYesterday);
    
    return sensor;
  }
}