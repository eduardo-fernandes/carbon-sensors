# carbon-sensors

## Instructions

Problems in the description:
- No rule for the case when the sensor status is set to WARN, and the next reading is below 2000 ppm. Here will be assumed that for this case, the status is set back to OK.
- The `POST /api/v1/sensors/{uuid}/mesurements` has a typo in measurements. It should be: `POST /api/v1/sensors/{uuid}/measurements`. It will be corrected in this application.
- The response payload from the method `GET /api/v1/sensors/{uuid}/alerts` returns a list of measurements, instead of `measurement1`, `measurement2` and `measurement3`. By doing like this, we get an array of N measurements, having a more generic and meaningful way of showing such values.