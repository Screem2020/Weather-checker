package io.github.screem2020.weatherloader.geocoding.exception

import org.springframework.web.client.RestClientException
import java.lang.RuntimeException


class GeocodingProviderException(message : String, ex : RestClientException) : RuntimeException(message, ex)