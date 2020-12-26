package com.carbonsensors.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.carbonsensors.model.Sensor;
import com.carbonsensors.model.Status;
import org.junit.jupiter.api.Test;

class SensorStatusDtoTest {

  @Test
  void fromSensor_whenParameterIsNull_thenReturnNull() {
    assertNull(SensorStatusDto.fromSensor(null));
  }

  @Test
  void fromSensor_whenSensorStatusIsNull_thenReturnNull() {
    assertNull(SensorStatusDto.fromSensor(Sensor.builder().build()));
  }

  @Test
  void fromSensor_whenSensorStatusIsOk_thenReturnOk() {
    Sensor sensor = Sensor.builder()
        .status(Status.OK)
        .build();

    assertEquals(Status.OK.name(), SensorStatusDto.fromSensor(sensor).getStatus());
  }
}