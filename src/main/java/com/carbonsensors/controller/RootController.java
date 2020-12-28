package com.carbonsensors.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "rootController", description = "Holds only the initial URL with a welcoming message pointing to Swagger")
@RestController
@RequestMapping(path = "/")
public class RootController {

  @ApiOperation(value = "Shows a welcome message", response = String.class)
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Welcome message is successfully shown")
  }
  )
  @GetMapping(path = "")
  public String home() {
    return "Welcome to the carbon measurement manager application. Access here our <a href=\"/swagger-ui.html\">Swagger APIn</a>";
  }
}
