package org.example.llmservice.service

import org.springframework.stereotype.Component

@Component
class RecentWords {
    private val words = ArrayDeque<String>()
    private val maxSize = 50

    @Synchronized
    fun add(word: String) {
        val w = word.lowercase()
        if (words.contains(w)) return
        words.addLast(w)
        if (words.size > maxSize) words.removeFirst()
    }

    @Synchronized
    fun contains(word: String): Boolean =
        words.contains(word.lowercase())

    @Synchronized
    fun snapshot(): List<String> =
        words.toList()
}