package com.carbonsensors.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.carbonsensors.model.projection.SensorMetrics;
import org.junit.jupiter.api.Test;

class SensorMetricsDtoTest {

  private static final Double MAX_LAST_30_DAYS = 30.3;
  private static final Double AVG_LAST_30_DAYS = 10.3;

  @Test
  void fromSensorMetrics_whenParameterIsNull_thenReturnNull() {
    assertNull(SensorMetricsDto.fromSensorMetrics(null));
  }

  @Test
  void fromSensorMetrics_whenParameterIsValid_thenReturnDto() {
    SensorMetricsDto result = SensorMetricsDto.fromSensorMetrics(new SensorMetrics(MAX_LAST_30_DAYS, AVG_LAST_30_DAYS));

    assertNotNull(result);
    assertEquals(MAX_LAST_30_DAYS, result.getMaxLast30Days());
    assertEquals(AVG_LAST_30_DAYS, result.getAvgLast30Days());
  }

  @Test
  void fromSensorMetrics_whenSensorMetricsExistsWithNullValue_thenReturnDtoWithZeros() {
    SensorMetricsDto result = SensorMetricsDto.fromSensorMetrics(new SensorMetrics(null, null));

    assertNotNull(result);
    assertEquals(0d, result.getMaxLast30Days());
    assertEquals(0d, result.getAvgLast30Days());
  }
}