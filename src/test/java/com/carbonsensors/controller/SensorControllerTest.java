package com.carbonsensors.controller;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.carbonsensors.dto.AlertDto;
import com.carbonsensors.dto.CreateMeasurementDto;
import com.carbonsensors.dto.SensorCreatedDto;
import com.carbonsensors.dto.SensorMetricsDto;
import com.carbonsensors.dto.SensorStatusDto;
import com.carbonsensors.model.Alert;
import com.carbonsensors.model.Measurement;
import com.carbonsensors.model.Sensor;
import com.carbonsensors.model.Status;
import com.carbonsensors.model.projection.SensorMetrics;
import com.carbonsensors.model.service.AlertService;
import com.carbonsensors.model.service.MeasurementService;
import com.carbonsensors.model.service.SensorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

class SensorControllerTest {

  private static final double FIRST_MEASUREMENT = 2001d;
  private static final double SECOND_MEASUREMENT = 2002d;
  private static final double THIRD_MEASUREMENT = 2003d;
  private static final LocalDateTime START = LocalDateTime.now();
  private static final LocalDateTime MIDDLE_DATE = LocalDateTime.now().plusDays(1);
  private static final LocalDateTime END = LocalDateTime.now().plusDays(2);

  @Mock
  private SensorService sensorService;
  @Mock
  private MeasurementService measurementService;
  @Mock
  private AlertService alertService;

  private SensorController sensorController;

  @BeforeEach
  void setup() {
    initMocks(this);
    sensorController = new SensorController(sensorService, measurementService, alertService);
  }

  @Test
  void createSensor_whenSystemIsOk_thenReturnCreatedSensor() {
    UUID sensorId = UUID.randomUUID();
    Sensor sensor = Sensor.builder()
        .id(sensorId)
        .build();

    when(sensorService.createSensor()).thenReturn(sensor);

    SensorCreatedDto result = sensorController.createSensor();
    assertNotNull(result);
    assertEquals(sensorId, result.getId());
    verify(sensorService).createSensor();
  }

  @Test
  void findSensorStatus_whenSensorIdIsValid_thenReturnSensorStatus() {
    UUID sensorId = UUID.randomUUID();
    Sensor sensor = Sensor.builder()
        .status(Status.OK)
        .build();

    when(sensorService.findSensorById(sensorId)).thenReturn(sensor);

    SensorStatusDto result = sensorController.findSensorStatus(sensorId);

    assertNotNull(result);
    assertEquals(Status.OK.name(), result.getStatus());
    verify(sensorService).findSensorById(sensorId);
  }

  @Test
  void createMeasurement_whenParametersAreValid_thenCreateMeasurement() {
    CreateMeasurementDto createMeasurementDto = new CreateMeasurementDto(20d, LocalDateTime.now());
    UUID sensorId = UUID.randomUUID();

    when(measurementService
        .createMeasurement(sensorId, createMeasurementDto.getCo2Quantity(), createMeasurementDto.getTime()))
        .thenReturn(null);

    sensorController.createMeasurement(sensorId, createMeasurementDto);

    verify(measurementService)
        .createMeasurement(sensorId, createMeasurementDto.getCo2Quantity(), createMeasurementDto.getTime());
  }

  @Test
  void computeMetrics_whenSensorIdIsValid_thenReturnComputedMetrics() {
    UUID sensorId = UUID.randomUUID();
    SensorMetrics metrics = new SensorMetrics(1d, 2d);

    when(sensorService.findMetricsBySensorId(sensorId)).thenReturn(metrics);

    SensorMetricsDto result = sensorController.computeMetrics(sensorId);

    assertNotNull(result);
    assertEquals(metrics.getMaxLastNDays(), result.getMaxLast30Days());
    assertEquals(metrics.getAverageLastNDays(), result.getAvgLast30Days());
    verify(sensorService).findMetricsBySensorId(sensorId);
  }

  @Test
  void findAlerts_whenSensorIsValid_thenReturnAlerts() {
    UUID sensorId = UUID.randomUUID();
    Alert alert = Alert.builder()
        .measurements(Arrays.asList(
            Measurement.builder()
                .co2Quantity(FIRST_MEASUREMENT)
                .created(START)
                .build()
            ,
            Measurement.builder()
                .co2Quantity(SECOND_MEASUREMENT)
                .created(MIDDLE_DATE)
                .build(),
            Measurement.builder()
                .co2Quantity(THIRD_MEASUREMENT)
                .created(END)
                .build()
        ))
        .build();

    when(alertService.findAlertsBySensorId(sensorId)).thenReturn(singletonList(alert));

    List<AlertDto> result = sensorController.findAlerts(sensorId);

    assertNotNull(result);
    assertEquals(1, result.size());
    AlertDto alertResult = result.get(0);
    assertNotNull(alertResult);
    assertEquals(START, alertResult.getStartTime());
    assertEquals(END, alertResult.getEndTime());
    assertEquals(alert.getMeasurements().stream().map(Measurement::getCo2Quantity).collect(Collectors.toList()),
        alertResult.getMesurements());
  }
}