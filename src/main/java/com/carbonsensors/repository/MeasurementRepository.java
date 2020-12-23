package com.carbonsensors.repository;

import com.carbonsensors.model.Measurement;
import com.carbonsensors.model.projection.SensorMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public interface MeasurementRepository extends JpaRepository<Measurement, Long> {

  @Query("select new com.carbonsensors.model.projection.SensorMetrics(max (m.co2Quantity), avg(m.co2Quantity)) "
      + "from Measurement m join m.sensor s "
      + "where s.id = :sensorId and m.created >= :measurementDate ")
  public SensorMetrics computeMetricsById(UUID sensorId, LocalDateTime measurementDate);

  Set<Measurement> findTop3BySensorIdOrderByCreatedDesc(UUID sensorId);
}
