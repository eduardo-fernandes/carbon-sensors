package com.carbonsensors.dto;

import com.carbonsensors.model.projection.SensorMetrics;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ApiModel(description = "Sensor Metrics")
public class SensorMetricsDto {

  @ApiModelProperty(notes = "Max CO2 quantity measurement within the last 30 days", example = "200.0")
  private Double maxLast30Days;

  @ApiModelProperty(notes = "CO2 quantity measurement average within the last 30 days", example = "15.5")
  private Double avgLast30Days;

  public static SensorMetricsDto fromSensorMetrics(SensorMetrics sensorMetrics) {
    SensorMetricsDto dto = null;
    if (sensorMetrics != null) {
      Double maxLastNDays = sensorMetrics.getMaxLastNDays() != null ? sensorMetrics.getMaxLastNDays() : 0d;
      Double avgLastNDays = sensorMetrics.getAverageLastNDays() != null ? sensorMetrics.getAverageLastNDays() : 0d;
      dto = new SensorMetricsDto(maxLastNDays, avgLastNDays);
    }
    return dto;
  }
}
