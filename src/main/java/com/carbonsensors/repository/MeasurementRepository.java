package com.carbonsensors.repository;

import com.carbonsensors.model.Measurement;
import com.carbonsensors.model.projection.SensorMetrics;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public interface MeasurementRepository extends JpaRepository<Measurement, Long> {

  @Query("select new com.carbonsensors.model.projection.SensorMetrics(max (m.co2Quantity), avg(m.co2Quantity)) "
      + "from Measurement m join m.sensor s "
      + "where s.id = :sensorId and m.created >= :measurementDate ")
  public SensorMetrics computeMetricsById(UUID sensorId, ZonedDateTime measurementDate);

  List<Measurement> findBySensorIdOrderByCreatedDesc(UUID sensorId, Pageable pageable);
}
