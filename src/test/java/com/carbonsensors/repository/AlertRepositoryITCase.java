package com.carbonsensors.repository;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.carbonsensors.model.Alert;
import com.carbonsensors.model.Sensor;
import com.carbonsensors.model.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@DataJpaTest
class AlertRepositoryITCase {

  @Autowired
  private AlertRepository alertRepository;

  @Autowired
  private SensorRepository sensorRepository;

  @Test
  void findBySensorId_whenExistsAlert_thenReturnAlert() {
    LocalDateTime created = LocalDateTime.now();
    Sensor sensor = Sensor.builder()
        .status(Status.OK)
        .build();

    sensor = sensorRepository.save(sensor);

    Alert alert = Alert.builder()
        .sensor(sensor)
        .created(created)
        .build();

    sensor.setAlerts(new HashSet<>(asList(alert)));

    sensorRepository.save(sensor);

    Optional<Alert> result = alertRepository.findBySensorId(sensor.getId());

    assertTrue(result.isPresent());
    assertEquals(created, result.get().getCreated());
  }

  @Test
  void findBySensorId_whenAlertDoesNotExists_thenReturnOptionalEmpty() {
    assertTrue(alertRepository.findBySensorId(UUID.randomUUID()).isEmpty());
  }
}