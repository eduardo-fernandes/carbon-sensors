package com.carbonsensors.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.carbonsensors.dto.SensorCreatedDto;
import com.carbonsensors.dto.SensorStatusDto;
import com.carbonsensors.model.Sensor;
import com.carbonsensors.model.Status;
import com.carbonsensors.model.service.MeasurementService;
import com.carbonsensors.model.service.SensorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.UUID;

class SensorControllerTest {

  @Mock
  private SensorService sensorService;

  @Mock
  private MeasurementService measurementService;

  private SensorController sensorController;

  @BeforeEach
  void setup() {
    initMocks(this);
    sensorController = new SensorController(sensorService, measurementService);
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
}