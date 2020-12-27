# carbon-sensors

## Instructions

Problems in the description:
- No rule for the case when the sensor status is set to WARN, and the next reading is below 2000 ppm. Here will be assumed that for this case, the status is set back to OK.
- the `POST /api/v1/sensors/{uuid}/mesurements` has a typo in measurements. It should be: `POST /api/v1/sensors/{uuid}/measurements`. It will be corrected in this application