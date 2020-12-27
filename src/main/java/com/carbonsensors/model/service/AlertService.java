package com.carbonsensors.model.service;

import com.carbonsensors.model.Alert;
import com.carbonsensors.repository.AlertRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AlertService {

  private final AlertRepository alertRepository;

  public AlertService(AlertRepository alertRepository) {
    this.alertRepository = alertRepository;
  }

  public Alert findAlertBySensorId(UUID sensorId) {
    return alertRepository.findBySensorId(sensorId).orElseThrow(
        () -> new IllegalArgumentException("There is no Alert related the the entered sensor id: " + sensorId));
  }
}
