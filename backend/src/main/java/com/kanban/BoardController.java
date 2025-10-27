package com.kanban;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator; // For status
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus; // For List compatibility
import org.springframework.http.ResponseEntity; // Import for Iterator
import org.springframework.web.bind.annotation.CrossOrigin; // For Map responses
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*") // Enable CORS for frontend
@RequestMapping("/api/boards")
public class BoardController {

    @GetMapping
    public ResponseEntity<List<String>> listBoards() {
        CustomArrayList<String> boardsList = new CustomArrayList<>();
        Iterator<String> it = BoardManager.getBoardKeysIterator(); // Add this static method to BoardManager if not present
        while (it.hasNext()) {
            boardsList.add(it.next());
        }
        // Convert to List for ResponseEntity
        List<String> boardNames = new ArrayList<>();
        for (int i = 0; i < boardsList.size(); i++) {
            boardNames.add(boardsList.get(i));
        }
        return ResponseEntity.ok(boardNames);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createBoard(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Name required"));
        }
        BoardManager.createBoard(name.trim());
        return ResponseEntity.ok(Map.of("message", "Created: " + name.trim()));
    }

    @GetMapping("/{boardName}")
    public ResponseEntity<Map<String, Object>> getBoard(@PathVariable String boardName) {
        Board board = BoardManager.selectBoard(boardName);
        if (board == null) return ResponseEntity.notFound().build();
        // Return as Map for JSON (title, stages summary, etc.)
        Map<String, Object> boardInfo = new HashMap<>();
        boardInfo.put("name", board.getName());
        // Add more like task count if needed
        return ResponseEntity.ok(boardInfo);
    }

    @PutMapping("/{boardName}")
    public ResponseEntity<Map<String, String>> updateBoard(@PathVariable String boardName, @RequestBody Map<String, String> request) {
        String newName = request.get("name");
        if (newName == null || newName.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "New name required"));
        }
        BoardManager.renameBoard(boardName, newName.trim());
        return ResponseEntity.ok(Map.of("message", "Updated to: " + newName.trim()));
    }

    @DeleteMapping("/{boardName}")
    public ResponseEntity<Map<String, String>> deleteBoard(@PathVariable String boardName) {
        BoardManager.deleteBoard(boardName);
        return ResponseEntity.ok(Map.of("message", "Deleted: " + boardName));
    }

    @GetMapping("/{boardName}/stages/{stage}/tasks")
    public ResponseEntity<List<Task>> getTasks(@PathVariable String boardName, @PathVariable String stage) {
        Board board = BoardManager.selectBoard(boardName);
        if (board == null) return ResponseEntity.notFound().build();
        Stage s = board.getStage(stage);
        if (s == null) return ResponseEntity.notFound().build();
        CustomArrayList<Task> tasksList = new CustomArrayList<>();
        Iterator<Task> it = s.taskIterator();
        while (it.hasNext()) {
            tasksList.add(it.next());
        }
        // Convert to List
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < tasksList.size(); i++) {
            tasks.add(tasksList.get(i));
        }
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/{boardName}/tasks")
    public ResponseEntity<Map<String, String>> addTask(@PathVariable String boardName, @RequestBody Map<String, Object> request, @RequestParam String stage) {
        Board board = BoardManager.selectBoard(boardName);
        if (board == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Board not found"));
        String title = (String) request.get("title");
        String desc = (String) request.get("description");
        String assignee = (String) request.get("assignee");
        String priority = (String) request.get("priority");
        if (title == null || title.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Title required"));
        }
        List<String> depsList = (List<String>) request.get("dependencies");
        CustomLinkedList<String> deps = new CustomLinkedList<>();
        if (depsList != null) {
            for (String d : depsList) {
                deps.add(d);
            }
        }
        board.createAndAddTask(title, desc, assignee, priority, stage, deps);
        return ResponseEntity.ok(Map.of("message", "Task added to " + stage));
    }

    @GetMapping("/{boardName}/tasks/{taskId}")
    public ResponseEntity<Task> getTask(@PathVariable String boardName, @PathVariable String taskId) {
        Board board = BoardManager.selectBoard(boardName);
        if (board == null) return ResponseEntity.notFound().build();
        Task task = board.getTask(taskId); // Ensure getTask is in Board
        if (task == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{boardName}/tasks/{taskId}")
    public ResponseEntity<Map<String, String>> updateTask(@PathVariable String boardName, @PathVariable String taskId, @RequestBody Map<String, Object> request) {
        Board board = BoardManager.selectBoard(boardName);
        if (board == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Board not found"));
        Task task = board.getTask(taskId);
        if (task == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Task not found"));
        // Update fields
        String title = (String) request.get("title");
        if (title != null) task.setTitle(title);
        String desc = (String) request.get("description");
        if (desc != null) task.setDescription(desc);
        String assignee = (String) request.get("assignee");
        if (assignee != null) task.setAssignee(assignee);
        String priority = (String) request.get("priority");
        if (priority != null) task.setPriority(priority);
        // Update deps
        List<String> depsList = (List<String>) request.get("dependencies");
        if (depsList != null) {
            CustomLinkedList<String> deps = new CustomLinkedList<>();
            for (String d : depsList) {
                deps.add(d);
            }
            task.setDependencies(deps);
        }
        board.addToPriorityQueueIfHigh(task); // If priority changed
        return ResponseEntity.ok(Map.of("message", "Task updated"));
    }

    @DeleteMapping("/{boardName}/tasks/{taskId}")
    public ResponseEntity<Map<String, String>> deleteTask(@PathVariable String boardName, @PathVariable String taskId) {
        Board board = BoardManager.selectBoard(boardName);
        if (board == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Board not found"));
        board.removeTask(taskId);
        return ResponseEntity.ok(Map.of("message", "Task deleted"));
    }

    @PutMapping("/{boardName}/tasks/{taskId}/move")
    public ResponseEntity<Map<String, String>> moveTask(@PathVariable String boardName, @PathVariable String taskId, @RequestParam String toStage) {
        Board board = BoardManager.selectBoard(boardName);
        if (board == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Board not found"));
        String fromStage = getCurrentStageForTask(board, taskId);
        if (fromStage.equals(toStage)) return ResponseEntity.badRequest().body(Map.of("error", "Same stage"));
        boolean success = board.moveTask(taskId, fromStage, toStage);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Task moved to " + toStage));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Move failed: Dependencies not completed or task not found"));
        }
    }

    // Helper method (simplified - find stage by scanning)
    private String getCurrentStageForTask(Board board, String taskId) {
        CustomArrayList<String> keys = board.getStageKeys();
        for (int i = 0; i < keys.size(); i++) {
            String stageName = keys.get(i);
            Stage s = board.getStage(stageName);
            if (s.findTaskIndex(taskId) != -1) {
                return stageName;
            }
        }
        return "TO-DO"; // Default
    }
}