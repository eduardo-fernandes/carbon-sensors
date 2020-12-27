package com.carbonsensors.e2e;

import static java.text.MessageFormat.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.carbonsensors.dto.CreateMeasurementDto;
import com.carbonsensors.dto.SensorCreatedDto;
import com.carbonsensors.dto.SensorMetricsDto;
import com.carbonsensors.dto.SensorStatusDto;
import com.carbonsensors.model.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootTest
class ApiEndToEndITCase {

  private static final Double CO2_QUANTITY_LIMIT = 2000d;
  private static final LocalDateTime NOW = LocalDateTime.now();

  private static final String ENCODING_UTF8 = "UTF-8";

  private MockMvc mockMvc;

  @Autowired
  protected WebApplicationContext wac;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  public void setUp(WebApplicationContext webApplicationContext) {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .build();
  }

  @Test
  void goThroughSensorCreationAndMeasurementFlow() throws Exception {
    int numberOfDays = 30;
    SensorCreatedDto sensorCreatedDto = createSensor();
    UUID sensorId = sensorCreatedDto.getId();
    checkSensorStatus(sensorId, Status.OK);
    createMeasurementWithLimitLevelOfCo2(sensorId, NOW.minusDays(numberOfDays));
    checkSensorStatus(sensorId, Status.OK);

    createMeasurementAboveTheLimitLevelOfCo2(sensorId, NOW.minusDays(--numberOfDays));
    checkSensorStatus(sensorId, Status.WARM);
    createMeasurementAboveTheLimitLevelOfCo2(sensorId, NOW.minusDays(--numberOfDays));
    checkSensorStatus(sensorId, Status.WARM);
    createMeasurementAboveTheLimitLevelOfCo2(sensorId, NOW.minusDays(--numberOfDays));
    checkSensorStatus(sensorId, Status.ALERT);

    createMeasurementBelowTheLimitLevelOfCo2(sensorId, NOW.minusDays(--numberOfDays));
    checkSensorStatus(sensorId, Status.ALERT);
    createMeasurementBelowTheLimitLevelOfCo2(sensorId, NOW.minusDays(--numberOfDays));
    checkSensorStatus(sensorId, Status.ALERT);
    createMeasurementBelowTheLimitLevelOfCo2(sensorId, NOW.minusDays(--numberOfDays));
    checkSensorStatus(sensorId, Status.OK);

    createMeasurementAboveTheLimitLevelOfCo2(sensorId, NOW.minusDays(--numberOfDays));
    checkSensorStatus(sensorId, Status.WARM);
    createMeasurementBelowTheLimitLevelOfCo2(sensorId, NOW.minusDays(--numberOfDays));
    checkSensorStatus(sensorId, Status.OK);

    SensorMetricsDto metricsDto = calculateSensorMetrics(sensorId);
    assertEquals(CO2_QUANTITY_LIMIT + 1, metricsDto.getMaxLast30Days());
    assertEquals(CO2_QUANTITY_LIMIT, metricsDto.getAvgLast30Days());
  }

  private SensorMetricsDto calculateSensorMetrics(UUID sensorId) throws Exception {
    MvcResult mvcResult = mockMvc.perform(get(format("/api/v1/sensors/{0}/metrics", sensorId))
        .contentType(MediaType.APPLICATION_JSON)
        .characterEncoding(ENCODING_UTF8))
        .andDo(print())
        .andReturn();

    assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());

    String responseAsString = mvcResult.getResponse().getContentAsString();
    return objectMapper.readValue(responseAsString, SensorMetricsDto.class);
  }

  private void checkSensorStatus(UUID sensorId, Status status) throws Exception {
    MvcResult mvcResult = mockMvc.perform(get(format("/api/v1/sensors/{0}", sensorId))
        .contentType(MediaType.APPLICATION_JSON)
        .characterEncoding(ENCODING_UTF8))
        .andDo(print())
        .andReturn();

    assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());

    String responseAsString = mvcResult.getResponse().getContentAsString();
    SensorStatusDto sensorStatusDto = objectMapper.readValue(responseAsString, SensorStatusDto.class);
    assertEquals(status.name(), sensorStatusDto.getStatus());
  }

  private SensorCreatedDto createSensor() throws Exception {
    MvcResult mvcResult = mockMvc.perform(post("/api/v1/sensors")
        .contentType(MediaType.APPLICATION_JSON)
        .characterEncoding(ENCODING_UTF8))
        .andDo(print())
        .andReturn();

    assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());

    String responseAsString = mvcResult.getResponse().getContentAsString();
    SensorCreatedDto sensorCreatedDto = objectMapper.readValue(responseAsString, SensorCreatedDto.class);

    assertNotNull(sensorCreatedDto);
    assertNotNull(sensorCreatedDto.getId());

    return sensorCreatedDto;
  }

  private void createMeasurementWithLimitLevelOfCo2(UUID sensorId, LocalDateTime time) throws Exception {
    createMeasurement(sensorId, CO2_QUANTITY_LIMIT, time);
  }

  private void createMeasurementAboveTheLimitLevelOfCo2(UUID sensorId, LocalDateTime time) throws Exception {
    createMeasurement(sensorId, CO2_QUANTITY_LIMIT + 1, time);
  }

  private void createMeasurementBelowTheLimitLevelOfCo2(UUID sensorId, LocalDateTime time) throws Exception {
    createMeasurement(sensorId, CO2_QUANTITY_LIMIT - 1, time);
  }

  private void createMeasurement(UUID sensorId, Double co2Quantity, LocalDateTime time) throws Exception {
    CreateMeasurementDto createMeasurementDto = new CreateMeasurementDto(co2Quantity, time);
    String parametersRequest = objectMapper.writeValueAsString(createMeasurementDto);

    MvcResult mvcResult = mockMvc.perform(post(MessageFormat.format("/api/v1/sensors/{0}/measurements", sensorId))
        .contentType(MediaType.APPLICATION_JSON)
        .characterEncoding(ENCODING_UTF8)
        .content(parametersRequest))
        .andDo(print())
        .andReturn();

    assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
  }
}
