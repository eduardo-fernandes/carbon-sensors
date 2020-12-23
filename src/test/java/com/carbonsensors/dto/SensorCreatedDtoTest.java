package com.carbonsensors.dto;

import static org.junit.jupiter.api.Assertions.*;

import com.carbonsensors.model.Sensor;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class SensorCreatedDtoTest {

  @Test
  void fromSensor_whenSensorIsNotNull_thenReturnDto() {
    UUID id = UUID.randomUUID();
    SensorCreatedDto dto = SensorCreatedDto.fromSensor(Sensor.builder()
        .id(id)
        .build()
    );

    assertNotNull(dto);
    assertEquals(id, dto.getId());
  }

  @Test
  void fromSensor_whenSensorIsNull_thenReturnNull() {
    assertNull(SensorCreatedDto.fromSensor(null));
  }
}