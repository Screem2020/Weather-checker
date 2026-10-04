package io.github.screem2020.weatherloader

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WeatherLoaderServiceApplication

fun main(args: Array<String>) {
    runApplication<WeatherLoaderServiceApplication>(*args)
}