package com.carbonsensors.model.service;

import com.carbonsensors.model.Sensor;
import com.carbonsensors.model.Status;
import com.carbonsensors.repository.SensorRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SensorService {

  private final SensorRepository sensorRepository;

  public SensorService(SensorRepository sensorRepository) {
    this.sensorRepository = sensorRepository;
  }

  public Sensor createSensor() {
    Sensor sensor = Sensor.builder()
        .id(UUID.randomUUID())
        .status(Status.OK)
        .build();

    return sensorRepository.save(sensor);
  }
}
