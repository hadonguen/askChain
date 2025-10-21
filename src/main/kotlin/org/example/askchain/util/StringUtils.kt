package org.example.askchain.util

object StringUtils {

    /**
     * 유효한 단어인지 검사한다.
     * - 공백/기호 없이 한글 또는 영문만 허용
     * - 길이 2~8자
     */
    fun isValidWord(word: String): Boolean {
        val okToken = word.matches(Regex("^[가-힣A-Za-z]+$"))
        val okLen = word.length in 2..8
        return okToken && okLen
    }

}