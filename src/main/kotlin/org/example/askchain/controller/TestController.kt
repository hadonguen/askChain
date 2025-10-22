package org.example.askchain.controller

import org.example.askchain.dto.QaRequest
import org.example.askchain.dto.QaResponse
import org.example.askchain.service.TestService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

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
    fun answerYesNoForWord(@RequestBody req: QaRequest): ResponseEntity<QaResponse> {

        if (req.word?.isBlank() == true || req.question.isBlank()) {
            return ResponseEntity.badRequest().body(
                QaResponse(false, 0,req.word, req.question, "모르겠어요", "word/question 빈값")
            )
        }
        return ResponseEntity.ok(testService.testAnswerYesNoForWord(req))
    }


}