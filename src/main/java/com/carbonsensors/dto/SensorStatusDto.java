package com.carbonsensors.dto;

import com.carbonsensors.model.Sensor;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ApiModel(description = "Sensor status of carbon consumption")
public class SensorStatusDto {

  @ApiModelProperty(notes = "Carbon consumption status", example = "OK")
  private String status;

  public static SensorStatusDto fromSensor(Sensor sensor) {
    SensorStatusDto dto = null;
    if (sensor != null && sensor.getStatus() != null) {
      dto = new SensorStatusDto(sensor.getStatus().name());
    }
    return dto;
  }
}
