package com.carbonsensors.dto;

import static java.util.Collections.emptyList;

import com.carbonsensors.model.Alert;
import com.carbonsensors.model.Measurement;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.collections4.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@ApiModel(description = "Holds value for a certain alert")
public class AlertDto {

  @ApiModelProperty(notes = "Time when the alert was set", example = "2019-02-02T18:55:47+00:00")
  private LocalDateTime startTime;

  @ApiModelProperty(notes = "Time when the alert was removed", example = "2019-02-02T20:00:47+00:00")
  private LocalDateTime endTime;

  @ApiModelProperty(notes = "List of measurements above CO2 limit", example = "[2100, 2200, 2100]")
  private List<Double> mesurements;

  public static List<AlertDto> fromAlerts(List<Alert> alerts) {
    if (CollectionUtils.isEmpty(alerts)) {
      return emptyList();
    }

    return alerts.stream().map(AlertDto::fromAlert).collect(Collectors.toList());
  }

  public static AlertDto fromAlert(Alert alert) {
    AlertDto dto = null;
    if (alert != null) {
      dto = new AlertDto();
      if (CollectionUtils.isEmpty(alert.getMeasurements())) {
        throw new IllegalArgumentException("Alert has no measurements");
      }

      List<Measurement> measurements = alert.getMeasurements();
      dto.setMesurements(
          measurements.stream().map(Measurement::getCo2Quantity).collect(Collectors.toList()));

      dto.setStartTime(
          measurements.stream().findFirst().orElseThrow(
              () -> new IllegalArgumentException("Alert does not have a measurement value. Alert Id: " + alert.getId()))
              .getCreated());

      dto.setEndTime(measurements.stream().reduce((first, second) -> second).orElseThrow(
          () -> new IllegalArgumentException("Alert does not have a measurement value. Alert Id: " + alert.getId()))
          .getCreated());
    }
    return dto;
  }
}