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
        OpenAPI().info(
            Info()
                .title("AskChain 스무고개 API")
                .description("OpenAI를 활용한 스무고개 게임 백엔드 API")
                .version("v1.0.0")
        )

    @Bean
    fun publicApi(): GroupedOpenApi =
        GroupedOpenApi.builder()
            .group("public")
            .packagesToScan("org.example.askchain.controller") // 네 컨트롤러 패키지
            .build()
}