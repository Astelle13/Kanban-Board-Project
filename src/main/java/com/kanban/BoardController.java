package com.kanban;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/boards")
public class BoardController {
    @GetMapping
    public ResponseEntity<String> listBoards() {
        return ResponseEntity.ok("Available boards: Default (add more via POST)");
    }

    @PostMapping
    public ResponseEntity<String> createBoard(@RequestBody String name) {
        BoardManager.createBoard(name);
        return ResponseEntity.ok("Created: " + name);
    }
}