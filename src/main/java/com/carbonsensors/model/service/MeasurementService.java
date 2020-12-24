package com.carbonsensors.model.service;

import static com.google.common.base.Preconditions.checkArgument;

import com.carbonsensors.config.ConfigurationProperties;
import com.carbonsensors.model.Alert;
import com.carbonsensors.model.Measurement;
import com.carbonsensors.model.Sensor;
import com.carbonsensors.model.Status;
import com.carbonsensors.repository.MeasurementRepository;
import com.carbonsensors.repository.SensorRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MeasurementService {

  private final MeasurementRepository measurementRepository;
  private final SensorRepository sensorRepository;
  private final ConfigurationProperties configurationProperties;

  public MeasurementService(MeasurementRepository measurementRepository,
                            SensorRepository sensorRepository,
                            ConfigurationProperties configurationProperties) {
    this.measurementRepository = measurementRepository;
    this.sensorRepository = sensorRepository;
    this.configurationProperties = configurationProperties;
  }

  @Transactional
  public Measurement createMeasurement(UUID sensorId, Double co2Quantity, LocalDateTime createdAt) {

    checkArgument(sensorId != null, "Sensor Id cannot be null");
    checkArgument(co2Quantity != null && co2Quantity >= 0,
        "co2Quantity must be greater or equal than zero and not null.");
    checkArgument(createdAt != null, "Creation date cannot be null");

    Sensor sensor =
        sensorRepository.findById(sensorId).orElseThrow(() -> new IllegalArgumentException(
            "The entered sensor id does not represent any entity in the database. Sensor Id: " + sensorId));

    Measurement measurement = Measurement.builder()
        .sensor(sensor)
        .created(createdAt)
        .co2Quantity(co2Quantity)
        .build();

    measurement = measurementRepository.save(measurement);

    Integer consecutiveMeasurementsForAlert = configurationProperties.getConsecutiveMeasurementsForAlert();
    Integer co2Threshold = configurationProperties.getCo2LevelThreshold();

    switch (sensor.getStatus()) {
      case OK:
        handleSensorStatusOk(sensorId, sensor, co2Threshold);
        break;
      case WARM:
        handleSensorStateWarning(sensorId, createdAt, sensor, consecutiveMeasurementsForAlert, co2Threshold);
        break;
      case ALERT:
        handleSensorStateAlert(sensorId, sensor, consecutiveMeasurementsForAlert, co2Threshold);
        break;
      default:
        throw new IllegalStateException("Unexpected value: " + sensor.getStatus());
    }

    return measurement;
  }

  private void handleSensorStateAlert(UUID sensorId, Sensor sensor, Integer consecutiveMeasurementsForAlert,
                                      Integer co2Threshold) {
    List<Measurement> lastThreeMeasurements =
        measurementRepository
            .findBySensorIdOrderByCreatedDesc(sensorId, PageRequest.of(0, consecutiveMeasurementsForAlert));

    if (lastThreeMeasurements.size() == consecutiveMeasurementsForAlert && lastThreeMeasurements.stream()
        .allMatch(m -> m.getCo2Quantity() <= co2Threshold)) {
      setSensorToStatusOk(sensor);
    }
  }

  private void handleSensorStateWarning(UUID sensorId, LocalDateTime createdAt, Sensor sensor,
                                        Integer consecutiveMeasurementsForAlert, Integer co2Threshold) {
    List<Measurement> lastThreeMeasurements =
        measurementRepository
            .findBySensorIdOrderByCreatedDesc(sensorId, PageRequest.of(0, consecutiveMeasurementsForAlert));

    if (lastThreeMeasurements.size() == consecutiveMeasurementsForAlert && lastThreeMeasurements.stream()
        .allMatch(m -> m.getCo2Quantity() > co2Threshold)) {
      setSensorToAStatusAlert(createdAt, sensor, lastThreeMeasurements);
    } else {
       lastThreeMeasurements.stream().findFirst().ifPresentOrElse(
           measurement -> {
             if (measurement.getCo2Quantity() <= co2Threshold) {
               setSensorToStatusOk(sensor);
             }
           },
           () -> setSensorToStatusOk(sensor)
       );
    }
  }

  private void handleSensorStatusOk(UUID sensorId, Sensor sensor, Integer co2Threshold) {
    List<Measurement> lastMeasurementList =
        measurementRepository.findBySensorIdOrderByCreatedDesc(sensorId, PageRequest.of(0, 1));

    lastMeasurementList.stream().findFirst().ifPresent(
        lastMeasurement -> {
          if (lastMeasurement.getCo2Quantity() > co2Threshold) {
            sensor.setStatus(Status.WARM);
            sensorRepository.save(sensor);
          }
        }
    );
  }

  private void setSensorToAStatusAlert(LocalDateTime createdAt, Sensor sensor,
                                       List<Measurement> lastThreeMeasurements) {
    sensor.setStatus(Status.ALERT);
    Alert alert = Alert.builder()
        .created(createdAt)
        .sensor(sensor)
        .measurements(lastThreeMeasurements)
        .build();

    sensor.getAlerts().add(alert);
    sensorRepository.save(sensor);
  }

  private void setSensorToStatusOk(Sensor sensor) {
    sensor.setStatus(Status.OK);
    sensorRepository.save(sensor);
  }

}