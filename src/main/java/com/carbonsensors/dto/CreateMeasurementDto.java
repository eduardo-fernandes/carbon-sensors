package com.carbonsensors.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ApiModel(description = "Holds value for a new measurement entry creation")
public class CreateMeasurementDto {

  @ApiModelProperty(notes = "CO2 quantity", example = "2000.0")
  private Double co2Quantity;

  @ApiModelProperty(notes = "time of the measurement", example = "2020-12-28T18:55:47+00:00")
  private ZonedDateTime time;
}
