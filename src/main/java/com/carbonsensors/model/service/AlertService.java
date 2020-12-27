package com.carbonsensors.model.service;

import com.carbonsensors.model.Alert;
import com.carbonsensors.repository.AlertRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AlertService {

  private final AlertRepository alertRepository;

  public AlertService(AlertRepository alertRepository) {
    this.alertRepository = alertRepository;
  }

  public List<Alert> findAlertsBySensorId(UUID sensorId) {
    return alertRepository.findBySensorIdOrderByCreatedDesc(sensorId);
  }
}
