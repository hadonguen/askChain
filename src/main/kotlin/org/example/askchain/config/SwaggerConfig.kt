package org.example.askchain.config

import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.OpenAPI
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    @Bean
    fun openAPI(): OpenAPI =
        OpenAPI().info(Info().title("AskChain API").version("v1"))

    @Bean
    fun publicApi(): GroupedOpenApi =
        GroupedOpenApi.builder()
            .group("public")
            .packagesToScan("org.example.askchain.controller") // 네 컨트롤러 패키지
            .build()
}