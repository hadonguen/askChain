package org.example.askchain.exception

import org.example.askchain.dto.StartRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(org.springframework.validation.BindException::class)
    fun handleBind(ex: org.springframework.validation.BindException): ResponseEntity<Map<String, Any?>> {
        // enum 필드 에러 탐지
        val enumMismatch = ex.fieldErrors.firstOrNull {
            it.field == "difficulty" && it.code == "typeMismatch"
        } != null

        val requiredMissing = ex.fieldErrors.any {
            it.code in setOf("NotNull", "NotBlank", "required") // 검증/바인더가 남기는 코드들 커버
        }

        val message = when {
            enumMismatch ->
                "잘못된 난이도 값입니다. 허용값: " + _root_ide_package_.org.example.askchain.dto.StartRequest.Difficulty.entries.joinToString(", ") { it.name }
            requiredMissing ->
                "필수 값이 누락되었습니다."
            else ->
                "요청 값 바인딩에 실패했습니다."
        }

        val body = mapOf(
            "success" to false,
            "message" to message,
            "fieldErrors" to ex.fieldErrors.map { fe ->
                mapOf(
                    "field" to fe.field,
                    "rejectedValue" to fe.rejectedValue,
                    "code" to fe.code
                )
            }
        )
        return ResponseEntity.badRequest().body(body)
    }
}