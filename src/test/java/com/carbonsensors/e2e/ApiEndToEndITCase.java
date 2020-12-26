package com.carbonsensors.e2e;

import static java.text.MessageFormat.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.carbonsensors.dto.SensorCreatedDto;
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

import java.util.UUID;

@SpringBootTest
class ApiEndToEndITCase {

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
    SensorCreatedDto sensorCreatedDto = createSensor();
    checkSensorStatus(sensorCreatedDto.getId(), Status.OK);

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
}
