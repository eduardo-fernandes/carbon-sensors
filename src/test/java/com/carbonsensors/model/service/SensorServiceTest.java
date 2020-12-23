package com.carbonsensors.model.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.carbonsensors.model.Sensor;
import com.carbonsensors.model.Status;
import com.carbonsensors.repository.SensorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.UUID;

class SensorServiceTest {

  @Mock
  private SensorRepository sensorRepository;

  private SensorService sensorService;

  @BeforeEach
  void setup() {
    initMocks(this);
    sensorService = new SensorService(sensorRepository);
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
}