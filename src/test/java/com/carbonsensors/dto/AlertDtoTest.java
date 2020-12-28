package com.carbonsensors.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.carbonsensors.model.Alert;
import com.carbonsensors.model.Measurement;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

class AlertDtoTest {

  private static final double FIRST_MEASUREMENT = 2001d;
  private static final double SECOND_MEASUREMENT = 2002d;
  private static final double THIRD_MEASUREMENT = 2003d;
  private static final ZonedDateTime START = ZonedDateTime.now();
  private static final ZonedDateTime MIDDLE_DATE = ZonedDateTime.now().plusDays(1);
  private static final ZonedDateTime END = ZonedDateTime.now().plusDays(2);

  @Test
  void fromAlert_whenAlertIsNull_thenReturnNull() {
    assertNull(AlertDto.fromAlert(null));
  }

  @Test
  void fromAlert_whenAlertIsOk_thenReturnDto() {

    Alert alert = Alert.builder()
        .measurements(Arrays.asList(
            Measurement.builder()
                .co2Quantity(FIRST_MEASUREMENT)
                .created(START)
                .build()
            ,
            Measurement.builder()
                .co2Quantity(SECOND_MEASUREMENT)
                .created(MIDDLE_DATE)
                .build(),
            Measurement.builder()
                .co2Quantity(THIRD_MEASUREMENT)
                .created(END)
                .build()
        ))
        .build();

    AlertDto result = AlertDto.fromAlert(alert);

    assertNotNull(result);
    assertEquals(START, result.getStartTime());
    assertEquals(END, result.getEndTime());
    assertEquals(alert.getMeasurements().stream().map(Measurement::getCo2Quantity).collect(Collectors.toList()),
        result.getMesurements());
  }

  @Test
  void fromAlert_whenAlertHasNoMeasures_throwException() {
    Alert alert = Alert.builder().build();

    assertThrows(IllegalArgumentException.class, () -> AlertDto.fromAlert(alert));
  }
}