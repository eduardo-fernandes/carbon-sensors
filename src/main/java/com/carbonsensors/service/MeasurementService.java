package com.carbonsensors.service;

import static com.google.common.base.Preconditions.checkArgument;

import com.carbonsensors.config.ConfigurationProperties;
import com.carbonsensors.model.Alert;
import com.carbonsensors.model.Measurement;
import com.carbonsensors.model.Sensor;
import com.carbonsensors.model.Status;
import com.carbonsensors.repository.AlertRepository;
import com.carbonsensors.repository.MeasurementRepository;
import com.carbonsensors.repository.SensorRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MeasurementService {

  private final MeasurementRepository measurementRepository;
  private final SensorRepository sensorRepository;
  private final AlertRepository alertRepository;
  private final ConfigurationProperties configurationProperties;

  public MeasurementService(MeasurementRepository measurementRepository,
                            SensorRepository sensorRepository,
                            AlertRepository alertRepository,
                            ConfigurationProperties configurationProperties) {
    this.measurementRepository = measurementRepository;
    this.sensorRepository = sensorRepository;
    this.alertRepository = alertRepository;
    this.configurationProperties = configurationProperties;
  }

  @Transactional
  public Measurement createMeasurement(UUID sensorId, Double co2Quantity, ZonedDateTime createdAt) {

    validateCreateMeasurementParameters(sensorId, co2Quantity, createdAt);

    Sensor sensor =
        sensorRepository.findById(sensorId).orElseThrow(() -> new IllegalArgumentException(
            "The entered sensor id does not represent any entity in the database. Sensor Id: " + sensorId));

    Measurement measurement = Measurement.builder()
        .sensor(sensor)
        .created(createdAt)
        .co2Quantity(co2Quantity)
        .build();

    measurement = measurementRepository.save(measurement);

    updateSensorStatus(sensor, createdAt);

    return measurement;
  }

  void updateSensorStatus(Sensor sensor, ZonedDateTime createdAt) {
    Integer consecutiveMeasurementsForAlert = configurationProperties.getConsecutiveMeasurementsForAlert();
    Integer consecutiveMeasurementsForOk = configurationProperties.getConsecutiveMeasurementsForOk();
    Integer co2Threshold = configurationProperties.getCo2LevelThreshold();

    List<Measurement> lastThreeMeasurements =
        measurementRepository
            .findBySensorIdOrderByCreatedDesc(sensor.getId(), PageRequest.of(0, consecutiveMeasurementsForAlert));

    if (lastThreeMeasurements.size() == consecutiveMeasurementsForAlert && lastThreeMeasurements.stream()
        .allMatch(m -> m.getCo2Quantity() > co2Threshold)) {
      setSensorToAStatusAlert(createdAt, sensor, lastThreeMeasurements);
    } else if (lastThreeMeasurements.size() == consecutiveMeasurementsForOk && lastThreeMeasurements.stream()
        .allMatch(m -> m.getCo2Quantity() <= co2Threshold)) {
      setSensorToStatusOk(sensor);
    } else if (sensor.getStatus() != Status.ALERT) {
      lastThreeMeasurements.stream().findFirst().ifPresent(
          mostRecentMeasurement -> {
            if (mostRecentMeasurement.getCo2Quantity() <= co2Threshold) {
              setSensorToStatusOk(sensor);
            } else {
              setSensorToStatusWaring(sensor);
            }
          }
      );
    }
  }

  private void validateCreateMeasurementParameters(UUID sensorId, Double co2Quantity, ZonedDateTime createdAt) {
    checkArgument(sensorId != null, "Sensor Id cannot be null");
    checkArgument(co2Quantity != null && co2Quantity >= 0,
        "co2Quantity must be greater or equal than zero and not null. Entered value: " + co2Quantity);
    checkArgument(createdAt != null, "Creation date cannot be null");
  }

  private void setSensorToAStatusAlert(ZonedDateTime createdAt, Sensor sensor,
                                       List<Measurement> lastThreeMeasurements) {
    if (sensor.getStatus() != null && sensor.getStatus() == Status.ALERT) {
      Alert alert =
          alertRepository.findTop1BySensorIdOrderByCreatedDesc(sensor.getId()).orElseThrow(() -> new IllegalStateException(
              "Sensor " + sensor.getId() + " should have an alert associated, since it is in AlERT state."));
      alert.getMeasurements().add(lastThreeMeasurements.stream().findFirst().orElseThrow(
          () -> new IllegalStateException(
              "Sensor " + sensor.getId() + " should have measurements for adding in an existing alert with id " + alert
                  .getId())));

      alertRepository.save(alert);
    } else {
      sensor.setStatus(Status.ALERT);
      Alert alert = Alert.builder()
          .created(createdAt)
          .sensor(sensor)
          .measurements(lastThreeMeasurements)
          .build();

      sensor.getAlerts().add(alert);
      sensorRepository.save(sensor);
    }
  }

  private void setSensorToStatusOk(Sensor sensor) {
    sensor.setStatus(Status.OK);
    sensorRepository.save(sensor);
  }

  private void setSensorToStatusWaring(Sensor sensor) {
    sensor.setStatus(Status.WARM);
    sensorRepository.save(sensor);
  }
}
