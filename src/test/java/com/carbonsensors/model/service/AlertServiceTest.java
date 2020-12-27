package com.carbonsensors.model.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.carbonsensors.model.Alert;
import com.carbonsensors.repository.AlertRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Optional;
import java.util.UUID;

class AlertServiceTest {

  private static final UUID SENSOR_ID = UUID.randomUUID();

  @Mock
  private AlertRepository alertRepository;

  private AlertService alertService;

  @BeforeEach
  void setup() {
    initMocks(this);
    alertService = new AlertService(alertRepository);
  }

  @Test
  void findAlertBySensorId_whenSensorIdRelatesToAlert_thenReturnAlert() {
    Alert alert = Alert.builder().build();
    when(alertRepository.findBySensorId(SENSOR_ID)).thenReturn(Optional.of(alert));

    Alert result = alertService.findAlertBySensorId(SENSOR_ID);

    assertNotNull(result);
    assertEquals(alert, result);
    verify(alertRepository).findBySensorId(SENSOR_ID);
  }

  @Test
  void findAlertBySensorId_whenSensorIdDoesNotRelateToAlert_thenThrowException() {
    when(alertRepository.findBySensorId(SENSOR_ID)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> alertService.findAlertBySensorId(SENSOR_ID));
    verify(alertRepository).findBySensorId(SENSOR_ID);
  }
}