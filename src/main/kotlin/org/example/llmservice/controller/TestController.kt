package org.example.llmservice.controller

import io.swagger.v3.oas.annotations.Hidden
import org.example.llmservice.dto.askchain.QaResponse
import org.example.llmservice.dto.askchain.QaTestRequest
import org.example.llmservice.service.TestService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Hidden
@RestController
@RequestMapping("/test")
class TestController(private val testService: TestService) {

    private val log = LoggerFactory.getLogger(TestController::class.java)

    @GetMapping("/test")
    fun test(): String {
        log.info("test_api")  // ← 콘솔에 찍힘
        return "✅ Test API OK"
    }
    @GetMapping("/askTest")
    fun askTest(@RequestParam prompt: String): String {
        return testService.askTest(prompt)
    }

    @GetMapping("/generateWord")
    fun generateWord(): ResponseEntity<Map<String, Any?>> {
        return ResponseEntity.ok(testService.testGenerateWord())
    }

    @PostMapping("/answerYesNoForWord")
    fun answerYesNoForWord(@RequestBody req: QaTestRequest): ResponseEntity<QaResponse> {

        if (req.word?.isBlank() == true || req.question.isBlank()) {
            return ResponseEntity.badRequest().body(
                QaResponse(false, 0,req.word, req.question, "모르겠어요", "word/question 빈값")
            )
        }
        return ResponseEntity.ok(testService.testAnswerYesNoForWord(req))
    }


}