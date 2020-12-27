package com.carbonsensors.repository;

import com.carbonsensors.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AlertRepository extends JpaRepository<Alert, UUID> {
  Optional<Alert> findBySensorId(UUID sensorId);
}
