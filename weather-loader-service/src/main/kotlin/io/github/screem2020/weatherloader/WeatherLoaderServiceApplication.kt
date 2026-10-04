package io.github.screem2020.weatherloader

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WeatherLoadServiceApplication

fun main(args: Array<String>) {
    runApplication<WeatherLoadServiceApplication>(*args)
}