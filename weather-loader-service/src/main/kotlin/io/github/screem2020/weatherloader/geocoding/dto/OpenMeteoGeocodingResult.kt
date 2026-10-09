package io.github.screem2020.weatherloader.geocoding.dto

data class OpenMeteoGeocodingResult(
    val name: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val timezone: String? = null,
    val country: String? = null,
    val admin1: String? = null
)