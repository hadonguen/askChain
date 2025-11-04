package org.example.llmservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.openai.OpenAiChatModel

@Configuration
class OpenAiConfig(
    private val openAiChatModel: OpenAiChatModel
) {

    @Bean
    fun chatClient(): ChatClient {
        return ChatClient.create(openAiChatModel)
    }
}