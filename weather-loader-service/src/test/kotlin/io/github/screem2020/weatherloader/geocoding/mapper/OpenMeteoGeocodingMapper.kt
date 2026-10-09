package io.github.screem2020.weatherloader.geocoding.mapper

import io.github.screem2020.weatherloader.geocoding.dto.OpenMeteoGeocodingResult
import io.github.screem2020.weatherloader.geocoding.exception.GeocodingInvalidResponseException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertNull

class OpenMeteoGeocodingMapperTest {

    private val mapper = OpenMeteoGeocodingMapper()

    private fun cities(
        name: String? = "Kaliningrad",
        latitude: Double? = 54.70639,
        longitude: Double? = 20.51102,
        timezone: String? = "Europe/Kaliningrad",
        country: String? = "Russia",
        admin1: String? = "Kaliningrad Oblast"
    ): List<OpenMeteoGeocodingResult> {
        return listOf(OpenMeteoGeocodingResult(name, latitude, longitude, timezone, country, admin1))
    }

    @Test
    fun `search city test`() {
        val openMeteoGeocodingResultList = cities()

        val toCityLocationDto = mapper.toCityLocationDto(openMeteoGeocodingResultList)

        assertEquals(1, toCityLocationDto.size)
        assertEquals("Kaliningrad", toCityLocationDto[0].name)
        assertEquals(54.70639, toCityLocationDto[0].latitude)
        assertEquals(20.51102, toCityLocationDto[0].longitude)
        assertEquals("Russia", toCityLocationDto[0].country)
        assertEquals("Kaliningrad Oblast", toCityLocationDto[0].region)
    }

    @Test
    fun `search map city with null region`() {
        val createTestGeocodingResult = cities(admin1 = null)

        val toCityLocationDtoList = mapper.toCityLocationDto(createTestGeocodingResult)

        assertEquals(1, toCityLocationDtoList.size)
        assertEquals("Kaliningrad", toCityLocationDtoList[0].name)
        assertEquals(54.70639, toCityLocationDtoList[0].latitude)
        assertEquals(20.51102, toCityLocationDtoList[0].longitude)
        assertEquals("Russia", toCityLocationDtoList[0].country)
        assertNull(toCityLocationDtoList[0].region)
    }

    @Test
    fun `should throw exception when city name is null`() {
        val cities = cities(name = null)

        assertThrows<GeocodingInvalidResponseException> {
            mapper.toCityLocationDto(cities)

        }
    }

    companion object {
        private val validCity = OpenMeteoGeocodingResult(
            name = "Kaliningrad",
            latitude = 54.70639,
            longitude = 20.51102,
            timezone = "Europe/Kaliningrad",
            country = "Russia",
            admin1 = "Kaliningrad Oblast"
        )

        @JvmStatic
        fun invalidCities(): List<OpenMeteoGeocodingResult> {
            return listOf(
                validCity.copy(name = null),
                validCity.copy(longitude = null),
                validCity.copy(latitude = null),
                validCity.copy(country = null)
            )
        }
    }

    @ParameterizedTest
    @MethodSource("invalidCities")
    fun `should throw exception when field is null`(city: OpenMeteoGeocodingResult) {
        assertThrows<GeocodingInvalidResponseException> {
            mapper.toCityLocationDto(listOf(city))
        }
    }
}