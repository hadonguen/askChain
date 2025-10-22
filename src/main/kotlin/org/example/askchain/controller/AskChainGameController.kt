package org.example.askchain.controller

import org.example.askchain.dto.QaRequest
import org.example.askchain.dto.StartRequest
import org.example.askchain.dto.StartResponse
import org.example.askchain.service.AskChainGameService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/game")
class AskChainGameController(
    private val askChainGameService: AskChainGameService
) {
    @GetMapping("/start")
    fun gameStart(@ModelAttribute req: StartRequest) = ResponseEntity.ok(askChainGameService.gameStart(req))

    @PostMapping("/question")
    fun ask(@RequestBody req: QaRequest) = ResponseEntity.ok(askChainGameService.ask(req.gameId, req.question))
}