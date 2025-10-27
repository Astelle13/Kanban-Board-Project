package com.kanban;

import java.util.*;

public class Board {
    private String name;
    private HashMap<String, Stage> stages = new HashMap<>();
    private HashMap<String, Task> allTasks = new HashMap<>();
    private PriorityQueue<Task> priQ = new PriorityQueue<>(new TaskComparator());

    public Board(String name) {
        this.name = name;
        stages.put("TO-DO", new Stage("TO-DO"));
        stages.put("IN-PROGRESS", new Stage("IN-PROGRESS"));
        stages.put("DONE", new Stage("DONE"));
    }

    // Create and add task to stage
    public void createAndAddTask(String title, String desc, String assignee, String priority, String stageName, List<String> deps) {
        if (!stages.containsKey(stageName)) {
            System.out.println("Invalid stage.");
            return;
        }
        Task t = new Task(title, desc, assignee, priority, deps);
        Stage s = stages.get(stageName);
        s.addTask(t);
        allTasks.put(t.getId(), t);
        if ("High".equals(priority)) {
            priQ.offer(t);
        }
        System.out.println("Task added to " + stageName);
    }

    // Move task with dep check
    public boolean moveTask(String taskId, String fromStage, String toStage) {
        if (!stages.containsKey(fromStage) || !stages.containsKey(toStage)) {
            System.out.println("Invalid stages.");
            return false;
        }
        Task t = allTasks.get(taskId);
        if (t == null) {
            System.out.println("Task not found.");
            return false;
        }
        if (!canMoveTask(taskId)) {
            System.out.println("Cannot move: dependencies not met (all must be COMPLETED).");
            return false;
        }
        Stage from = stages.get(fromStage);
        int idx = from.findTaskIndex(taskId);
        if (idx == -1) {
            System.out.println("Task not in source stage.");
            return false;
        }
        from.removeTaskAtIndex(idx);
        t.setStatus(getStatusForStage(toStage));
        Stage to = stages.get(toStage);
        to.addTask(t);
        System.out.println("Task '" + t.getTitle() + "' moved to " + toStage + ".");
        return true;
    }

    // Simple dep check (loop, no transitive/BFS for now; extend if needed)
    public boolean canMoveTask(String taskId) {
        Task t = allTasks.get(taskId);
        if (t == null) return false;
        for (String depId : t.getDependencies()) {
            Task dep = allTasks.get(depId);
            if (dep == null || dep.getStatus() != TaskStatus.COMPLETED) {
                return false;
            }
        }
        return true;
    }

    private TaskStatus getStatusForStage(String stage) {
        switch (stage) {
            case "TO-DO": return TaskStatus.PENDING;
            case "IN-PROGRESS": return TaskStatus.IN_PROGRESS;
            case "DONE": return TaskStatus.COMPLETED;
            default: return TaskStatus.PENDING;
        }
    }

    // Get high priority snapshot (restore queue after)
    public List<Task> getHighPriorityTasks() {
        List<Task> high = new ArrayList<>();
        PriorityQueue<Task> temp = new PriorityQueue<>(priQ);
        while (!temp.isEmpty()) {
            high.add(temp.poll());
        }
        // Restore
        for (Task h : high) {
            priQ.offer(h);
        }
        return high;
    }

    // Add to priQ if high (for updates)
    public void addToPriorityQueueIfHigh(Task t) {
        if ("High".equals(t.getPriority())) {
            priQ.offer(t);
        }
    }

    // Remove from allTasks (for delete)
    public void removeTask(String taskId) {
        allTasks.remove(taskId);
        // priQ cleanup skipped (simple impl)
    }

    public Stage getStage(String stageName) {
        return stages.get(stageName);
    }

    public List<Stage> getAllStages() {
        return new ArrayList<>(stages.values());
    }

    public List<String> getStageKeys() {
        return new ArrayList<>(stages.keySet());
    }

    public void save(String filename) {
        try {
            new java.io.FileWriter(filename).close(); // Clear file
        } catch (Exception e) {}
        for (Stage s : stages.values()) {
            s.saveToFile(filename);
        }
        System.out.println("Board saved.");
    }

    public void load(String filename) {
        allTasks.clear();
        priQ.clear();
        for (Stage s : stages.values()) {
            s.loadFromFile(filename);
        }
        // Rebuild allTasks and priQ
        for (Stage s : stages.values()) {
            for (Task t : s.tasks) {
                allTasks.put(t.getId(), t);
                if ("High".equals(t.getPriority())) {
                    priQ.offer(t);
                }
            }
        }
        System.out.println("Board loaded.");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Comparator for PriorityQueue (min-heap, low num = high pri first)
    static class TaskComparator implements Comparator<Task> {
        @Override
        public int compare(Task a, Task b) {
            return Integer.compare(a.getPriorityNum(), b.getPriorityNum());
        }
    }
}