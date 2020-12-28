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
import org.springframework.data.domain.PageRequest;

import java.time.ZonedDateTime;
import java.util.List;

@DataJpaTest
class MeasurementRepositoryITCase {

  private static final double MEASUREMENT_TODAY = 100d;
  private static final double MEASUREMENT_YESTERDAY = 150d;
  private static final double MEASUREMENT_DAY_BEFORE_YESTERDAY = 175d;
  private static final double MEASUREMENT_3_DAYS_AGO = 165d;

  @Autowired
  private MeasurementRepository measurementRepository;

  @Autowired
  private SensorRepository sensorRepository;

  @Test
  void computeMetricsById_whenDataIsOk_thenReturnComputedData() {
    ZonedDateTime today = ZonedDateTime.now();
    ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1);
    ZonedDateTime dayBeforeYesterday = ZonedDateTime.now().minusDays(2);

    Sensor sensor = create3Measurements(today, yesterday, dayBeforeYesterday);

    SensorMetrics sensorMetrics =
        measurementRepository.computeMetricsById(sensor.getId(), yesterday);

    assertNotNull(sensorMetrics);
    assertEquals(MEASUREMENT_YESTERDAY, sensorMetrics.getMaxLastNDays());
    assertEquals(125, sensorMetrics.getAverageLastNDays());

    sensorMetrics = measurementRepository.computeMetricsById(sensor.getId(), dayBeforeYesterday);
    assertEquals(MEASUREMENT_DAY_BEFORE_YESTERDAY, sensorMetrics.getMaxLastNDays());
    assertEquals(141.67d, Math.round(sensorMetrics.getAverageLastNDays() * 100d) / 100d);
  }

  @Test
  void findBySensorIdOrderByCreatedDesc_whenMeasurementsToBeFoundAre3_thenReturnSet() {
    ZonedDateTime today = ZonedDateTime.now();
    ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1);
    ZonedDateTime dayBeforeYesterday = ZonedDateTime.now().minusDays(2);
    ZonedDateTime threeDaysAgo = ZonedDateTime.now().minusDays(3);

    Sensor sensor = create3Measurements(today, yesterday, dayBeforeYesterday);

    Measurement measurementThreeDaysAgo = Measurement.builder()
        .created(threeDaysAgo)
        .co2Quantity(MEASUREMENT_3_DAYS_AGO)
        .sensor(sensor)
        .build();
    measurementRepository.save(measurementThreeDaysAgo);

    List<Measurement> measurements = measurementRepository.findBySensorIdOrderByCreatedDesc(sensor.getId(), PageRequest.of(0, 3));
    assertNotNull(measurements);
    assertEquals(3, measurements.size());
    assertTrue(measurements.stream().anyMatch(m -> m.getCo2Quantity() == MEASUREMENT_TODAY));
    assertTrue(measurements.stream().anyMatch(m -> m.getCo2Quantity() == MEASUREMENT_YESTERDAY));
    assertTrue(measurements.stream().anyMatch(m -> m.getCo2Quantity() == MEASUREMENT_DAY_BEFORE_YESTERDAY));
    assertTrue(measurements.stream().noneMatch(m -> m.getCo2Quantity() == MEASUREMENT_3_DAYS_AGO));
  }

  private Sensor create3Measurements(ZonedDateTime dateFirstMeasurement, ZonedDateTime dateSecondMesurement,
                                     ZonedDateTime dateThirdMeasurement) {
    Sensor sensor = Sensor.builder()
        .status(Status.OK)
        .build();

    sensor = sensorRepository.save(sensor);

    Measurement measurementToday = Measurement.builder()
        .created(dateFirstMeasurement)
        .co2Quantity(MEASUREMENT_TODAY)
        .sensor(sensor)
        .build();
    measurementRepository.save(measurementToday);

    Measurement measurementYesterday = Measurement.builder()
        .created(dateSecondMesurement)
        .co2Quantity(MEASUREMENT_YESTERDAY)
        .sensor(sensor)
        .build();
    measurementRepository.save(measurementYesterday);

    Measurement measurementDayBeforeYesterday = Measurement.builder()
        .created(dateThirdMeasurement)
        .co2Quantity(MEASUREMENT_DAY_BEFORE_YESTERDAY)
        .sensor(sensor)
        .build();
    measurementRepository.save(measurementDayBeforeYesterday);

    return sensor;
  }
}