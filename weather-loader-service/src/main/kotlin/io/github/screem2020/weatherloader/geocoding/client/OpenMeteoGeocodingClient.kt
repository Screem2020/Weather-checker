package io.github.screem2020.weatherloader.geocoding.client

import io.github.screem2020.weatherloader.geocoding.dto.CityLocationDto
import io.github.screem2020.weatherloader.geocoding.dto.OpenMeteoGeocodingResponse
import io.github.screem2020.weatherloader.geocoding.exception.GeocodingInvalidResponseException
import io.github.screem2020.weatherloader.geocoding.exception.GeocodingProviderException
import io.github.screem2020.weatherloader.geocoding.mapper.OpenMeteoGeocodingMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException

@Component
class OpenMeteoGeocodingClient(
    private val mapper: OpenMeteoGeocodingMapper,
    restClientBuilder: RestClient.Builder
) {
    private val log = LoggerFactory.getLogger(OpenMeteoGeocodingClient::class.java)
    private val restClient = restClientBuilder
        .baseUrl("https://geocoding-api.open-meteo.com")
        .build()

    /**
     * Выполняет поиск города через Geocoding API Open-Meteo
     *
     * @param  query название города
     * @return список найденных городов
     * @throws GeocodingProviderException при ошибке взаимодействия с внешним API
     * @throws GeocodingInvalidResponseException при некорректоном ответе внешного API
     */
    fun searchCity(query: String): List<CityLocationDto> {
        log.debug("Searching geocoding search, query{}:", query)
        val body = try {
            restClient.get()
                .uri { builder ->
                    builder.path("/v1/search")
                        .queryParam("name", query)
                        .build()
                }
                .retrieve()
                .body(OpenMeteoGeocodingResponse::class.java)
        } catch (ex: RestClientException) {
            throw GeocodingProviderException(
                "Error while searching geocoding search",
                ex
            )
        }
        if (body == null) throw GeocodingInvalidResponseException("Open Meteo Geocoding API call failed")
        val results = body.results
        log.debug("Found {} results", results.size)
        return mapper.toCityLocationDto(results)
    }
}
