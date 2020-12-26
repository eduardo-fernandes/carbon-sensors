package com.carbonsensors.controller;

import com.carbonsensors.dto.SensorCreatedDto;
import com.carbonsensors.dto.SensorStatusDto;
import com.carbonsensors.model.service.MeasurementService;
import com.carbonsensors.model.service.SensorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Api(value = "sensorController", description = "Provide services for sensor management")
@RestController
@RequestMapping(path = "/api/v1/sensors")
public class SensorController {

  private final SensorService sensorService;
  private final MeasurementService measurementService;

  public SensorController(SensorService sensorService,
                          MeasurementService measurementService) {
    this.sensorService = sensorService;
    this.measurementService = measurementService;
  }

  @ApiOperation(value = "Create a Sensor", response = SensorCreatedDto.class)
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Sensor was created successfully.")
  }
  )
  @PostMapping
  public SensorCreatedDto createSensor() {
    return SensorCreatedDto.fromSensor(sensorService.createSensor());
  }

  @ApiOperation(value = "Get a Sensor status based on its Id", response = SensorStatusDto.class)
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Successful retrieval of a Sensor status by its id"),
      @ApiResponse(code = 400, message = "Sensor Id is not valid or null")
  }
  )
  @GetMapping(path = "/{uuid}")
  public @ResponseBody SensorStatusDto findSensorStatus(@PathVariable(value = "uuid") UUID sensorId) {
    return SensorStatusDto.fromSensor(sensorService.findSensorById(sensorId));
  }
}
