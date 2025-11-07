package org.example.llmservice.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.example.llmservice.dto.blinddate.CharacterGenerationRequest
import org.example.llmservice.dto.blinddate.ChatRequest
import org.example.llmservice.service.BlindDateService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/dating")
@Tag(name = "Blind Date API", description = "소개팅 시뮬레이션 엔드포인트")
class BlindDateController(
    private val blindDateService: BlindDateService
) {
    @Operation(
        summary = "소개팅 상대 생성",
        description = "소개팅을 시작한다. 소개팅 상대 캐릭터 생성"
    )
    @GetMapping("/generate")
    fun generate(@ModelAttribute req: CharacterGenerationRequest) = ResponseEntity.ok(blindDateService.generateCharacter(req))

    @Operation(
        summary = "소개팅 대화 진행",
        description = "소개팅 대화를 신행한다. 호감도 상대 대화 생성"
    )
    @PostMapping("/chat")
    fun chat(@RequestBody req: ChatRequest) = ResponseEntity.ok(blindDateService.chat(req))

}