package org.example.llmservice.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.example.llmservice.dto.askchain.QaRequest
import org.example.llmservice.dto.askchain.StartRequest
import org.example.llmservice.service.AskChainGameService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/game")
@Tag(name = "Game API", description = "스무고개 게임 진행 엔드포인트")
class AskChainGameController(
    private val askChainGameService: AskChainGameService
) {
    @Operation(
        summary = "게임 시작",
        description = "새 게임을 시작한다. 스무고개 문제 단어 생성"
    )
    @GetMapping("/start")
    fun gameStart(@ModelAttribute req: StartRequest) = ResponseEntity.ok(askChainGameService.gameStart(req))

    @Operation(
        summary = "질문/정답 처리",
        description = "사용자의 질문을 받고 예/아니오/모르겠어요 중 하나로 응답한다. 정답입력시에선 정오답 판단"
    )
    @PostMapping("/question")
    fun ask(@RequestBody req: QaRequest) = ResponseEntity.ok(askChainGameService.ask(req.gameId, req.question))
}