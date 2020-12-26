package com.carbonsensors.dto;

import com.carbonsensors.model.Sensor;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ApiModel(description = "Created sensor values")
public class SensorCreatedDto {

  @ApiModelProperty(notes = "Unique id", example = "abc-eslsd-32-sdfsdf")
  private UUID id = null;

  public static SensorCreatedDto fromSensor(Sensor sensor) {
    SensorCreatedDto dto = null;

    if (sensor != null) {
      dto = new SensorCreatedDto(sensor.getId());
    }

    return dto;
  }
}
