package io.github.screem2020.weatherloader.geocoding.dto

data class OpenMeteoGeocodingResponse(
    val results: List<OpenMeteoGeocodingResult> = emptyList()
)