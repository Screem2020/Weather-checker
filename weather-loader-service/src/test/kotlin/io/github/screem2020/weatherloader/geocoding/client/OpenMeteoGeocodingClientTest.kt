package io.github.screem2020.weatherloader.geocoding.client

import io.github.screem2020.weatherloader.geocoding.exception.GeocodingInvalidResponseException
import io.github.screem2020.weatherloader.geocoding.exception.GeocodingProviderException
import io.github.screem2020.weatherloader.geocoding.mapper.OpenMeteoGeocodingMapper
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withServerError
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestClient
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OpenMeteoGeocodingClientTest {
    private val mapper = OpenMeteoGeocodingMapper()
    private lateinit var mockServer: MockRestServiceServer
    private lateinit var client: OpenMeteoGeocodingClient

    @BeforeEach
    fun setUp() {
        val builder = RestClient.builder()
        mockServer = MockRestServiceServer.bindTo(builder).build()
        client = OpenMeteoGeocodingClient(mapper, builder)
    }

    @Test
    fun `should return mapped city when provider responds with valid JSON HTTP 200`() {
        val responseJson: String = """
            {
              "results": [
                {
                  "name": "Kaliningrad",
                  "latitude": 54.70639,
                  "longitude": 20.51102,
                  "country": "Russia",
                  "admin1": "Kaliningrad Oblast"
                }
              ]
            }
        """.trimIndent()
        mockServer.expect(
            requestTo("https://geocoding-api.open-meteo.com/v1/search?name=Kaliningrad")
        ).andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON))
        val result = client.searchCity("Kaliningrad")
        assertEquals(1, result.size)
        assertEquals("Kaliningrad", result[0].name)
        assertEquals(54.70639, result[0].latitude)
        assertEquals(20.51102, result[0].longitude)
        assertEquals("Russia", result[0].country)
        assertEquals("Kaliningrad Oblast", result[0].region)
        mockServer.verify()
    }

    @Test
    fun `should return mapped city when provider responds with valid russian JSON HTTP 200`() {
        val responseJson: String = """
            {
              "results": []
            }
        """.trimIndent()
        mockServer.expect(
            requestTo("https://geocoding-api.open-meteo.com/v1/search?name=%D0%9D%D0%B8%D0%B6%D0%BD%D0%B8%D0%B9%20%D0%9D%D0%BE%D0%B2%D0%B3%D0%BE%D1%80%D0%BE%D0%B4")
        ).andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON))
        val result = client.searchCity("Нижний Новгород")
        assertTrue(result.isEmpty())
        mockServer.verify()
    }

    @Test
    fun `should return empty list when provider response has no results field HTTP 200`() {
        val responseJson: String = """
           {"generationtime_ms": 0.12}
        """.trimIndent()
        mockServer.expect(
            requestTo("https://geocoding-api.open-meteo.com/v1/search?name=Kaliningrad")
        ).andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON))
        val result = client.searchCity("Kaliningrad")
        assertTrue(result.isEmpty())
        mockServer.verify()
    }

    @Test
    fun `should encode Cyrillic characters and spaces in query HTTP 200`() {
        val responseJson: String = """
            {
              "results": []
            }
        """.trimIndent()
        mockServer.expect(
            requestTo("https://geocoding-api.open-meteo.com/v1/search?name=Kaliningrad")
        ).andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON))
        val result = client.searchCity("Kaliningrad")
        assertTrue(result.isEmpty())
        mockServer.verify()
    }

    @Test
    fun `should throw exception when provider responds with empty body HTTP 200`() {
        mockServer.expect(
            requestTo("https://geocoding-api.open-meteo.com/v1/search?name=Kaliningrad")
        ).andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess())
        assertThrows<GeocodingInvalidResponseException> {
            client.searchCity("Kaliningrad")
        }
        mockServer.verify()
    }

    @Test
    fun `should throw provider exception when provider responds with HTTP 500`() {
        mockServer.expect(
            requestTo("https://geocoding-api.open-meteo.com/v1/search?name=Kaliningrad")
        ).andExpect(method(HttpMethod.GET))
            .andRespond(withServerError())
        val exception = assertThrows<GeocodingProviderException> {
            client.searchCity("Kaliningrad")
        }
        assertNotNull(exception.cause)
        mockServer.verify()
    }

    @Test
    fun `should return all mapped cities in provider order HTTP 200`() {
        val responseJson: String = """
            {
              "results": [
                {
                  "name": "Kaliningrad",
                  "latitude": 54.70639,
                  "longitude": 20.51102,
                  "country": "Russia",
                  "admin1": "Kaliningrad Oblast"
                },
                {
                  "name": "Moscow",
                  "latitude": 55.70639,
                  "longitude": 24.51102,
                  "country": "Russia",
                  "admin1": "Moscow Oblast"
                }
              ]
            }
        """.trimIndent()
        mockServer.expect(
            requestTo("https://geocoding-api.open-meteo.com/v1/search?name=Kaliningrad")
        ).andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON))
        val result = client.searchCity("Kaliningrad")
        assertEquals(2, result.size)
        assertEquals("Kaliningrad", result[0].name)
        assertEquals(54.70639, result[0].latitude)
        assertEquals(20.51102, result[0].longitude)
        assertEquals("Russia", result[0].country)
        assertEquals("Kaliningrad Oblast", result[0].region)

        assertEquals("Moscow", result[1].name)
        assertEquals(55.70639, result[1].latitude)
        assertEquals(24.51102, result[1].longitude)
        assertEquals("Russia", result[1].country)
        assertEquals("Moscow Oblast", result[1].region)
        mockServer.verify()
    }
}