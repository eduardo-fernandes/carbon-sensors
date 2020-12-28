package com.carbonsensors.repository;

import static java.util.Arrays.asList;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.carbonsensors.model.Alert;
import com.carbonsensors.model.Sensor;
import com.carbonsensors.model.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@DataJpaTest
class AlertRepositoryITCase {

  @Autowired
  private AlertRepository alertRepository;

  @Autowired
  private SensorRepository sensorRepository;

  @Test
  void findBySensorId_whenExistsAlert_thenReturnAlert() {
    ZonedDateTime today = ZonedDateTime.now();
    ZonedDateTime tomorrow = ZonedDateTime.now().plusDays(1);

    Sensor sensor = createSensorAnd2Alerts(today, tomorrow);

    List<Alert> result = alertRepository.findBySensorIdOrderByCreatedDesc(sensor.getId());

    assertTrue(isNotEmpty(result));
    assertEquals(2, result.size());
    assertEquals(tomorrow, result.get(0).getCreated());
    assertEquals(today, result.get(1).getCreated());
  }

  @Test
  void findTop1BySensorIdOrderByCreatedDesc_whenSensorHaveSeveralAlerts_thenReturnMostRecentOne() {
    ZonedDateTime today = ZonedDateTime.now();
    ZonedDateTime tomorrow = ZonedDateTime.now().plusDays(1);

    Sensor sensor = createSensorAnd2Alerts(today, tomorrow);

    Optional<Alert> result = alertRepository.findTop1BySensorIdOrderByCreatedDesc(sensor.getId());

    assertNotNull(result);
    assertTrue(result.isPresent());
    assertEquals(tomorrow, result.get().getCreated());
  }

  private Sensor createSensorAnd2Alerts(ZonedDateTime today, ZonedDateTime tomorrow) {
    Sensor sensor = Sensor.builder()
        .status(Status.OK)
        .build();

    sensor = sensorRepository.save(sensor);

    Alert alert1 = Alert.builder()
        .sensor(sensor)
        .created(today)
        .build();

    Alert alert2 = Alert.builder()
        .sensor(sensor)
        .created(tomorrow)
        .build();

    sensor.setAlerts(new HashSet<>(asList(alert1, alert2)));

    return sensorRepository.save(sensor);
  }
}