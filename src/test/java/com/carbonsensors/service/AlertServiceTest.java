package com.carbonsensors.service;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.carbonsensors.model.Alert;
import com.carbonsensors.repository.AlertRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;
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
    List<Alert> alerts = singletonList(alert);
    when(alertRepository.findBySensorIdOrderByCreatedDesc(SENSOR_ID)).thenReturn(alerts);

    List<Alert> result = alertService.findAlertsBySensorId(SENSOR_ID);

    assertNotNull(result);
    assertEquals(alerts, result);
    verify(alertRepository).findBySensorIdOrderByCreatedDesc(SENSOR_ID);
  }
}