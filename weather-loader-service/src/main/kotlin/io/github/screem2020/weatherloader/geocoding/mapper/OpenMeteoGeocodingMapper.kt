package io.github.screem2020.weatherloader.geocoding.mapper

import io.github.screem2020.weatherloader.geocoding.dto.CityLocationDto
import io.github.screem2020.weatherloader.geocoding.dto.OpenMeteoGeocodingResult
import io.github.screem2020.weatherloader.geocoding.exception.GeocodingInvalidResponseException
import org.springframework.stereotype.Component

@Component
class OpenMeteoGeocodingMapper {
    fun toCityLocationDto(response: List<OpenMeteoGeocodingResult>): List<CityLocationDto> {
        return response.map { city ->
            CityLocationDto(
                name = city.name ?: throw GeocodingInvalidResponseException("City name is null"),
                region = city.admin1,
                country = city.country ?: throw GeocodingInvalidResponseException("Country country is null"),
                latitude = city.latitude ?: throw GeocodingInvalidResponseException("Latitude is null"),
                longitude = city.longitude ?: throw GeocodingInvalidResponseException("Longitude is null"))
        }
    }
}