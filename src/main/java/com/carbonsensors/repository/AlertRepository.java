package com.carbonsensors.repository;

import com.carbonsensors.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlertRepository extends JpaRepository<Alert, UUID> {
  List<Alert> findBySensorIdOrderByCreatedDesc(UUID sensorId);

  Optional<Alert> findTop1BySensorIdOrderByCreatedDesc(UUID sensorId);
}
