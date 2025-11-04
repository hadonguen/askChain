package org.example.llmservice.llm

interface LlmClient {
    fun chat(systemPrompt: String, userPrompt: String): String
}