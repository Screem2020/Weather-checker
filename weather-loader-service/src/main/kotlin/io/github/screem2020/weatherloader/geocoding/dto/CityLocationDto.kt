package io.github.screem2020.weatherloader.geocoding.dto

data class CityLocationDto(
    val name: String,
    val region: String?,
    val country: String,
    val latitude: Double,
    val longitude: Double
)