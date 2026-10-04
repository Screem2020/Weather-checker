package io.github.screem2020.weather

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WeatherServiceApplication

fun main(args: Array<String>) {
    runApplication<WeatherServiceApplication>(*args)
}
