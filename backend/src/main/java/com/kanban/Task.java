// Updated Task.java (add import for Iterator)
package com.kanban;

import java.util.Iterator;

public class Task {
    private String id;
    private String title;
    private String description;
    private String assignee;
    private String priority;  // "High", "Medium", "Low"
    private TaskStatus status; // PENDING, IN_PROGRESS, COMPLETED
    private CustomLinkedList<String> dependencies = new CustomLinkedList<>(); // For dependency tracking

    // No-arg constructor (for serialization/loading)
    public Task() {}

    // Constructor (default priority + pending status + empty deps)
    public Task(String title, String description, String assignee) {
        this.id = java.util.UUID.randomUUID().toString(); // Keep UUID, as it's utility
        this.title = title;
        this.description = description;
        this.assignee = assignee;
        this.priority = "Medium"; // default
        this.status = TaskStatus.PENDING;
    }

    // Constructor with priority and deps
    public Task(String title, String description, String assignee, String priority, CustomLinkedList<String> deps) {
        this.id = java.util.UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.assignee = assignee;
        setPriority(priority);
        this.status = TaskStatus.PENDING;
        if (deps != null) {
            Iterator<String> it = deps.iterator();
            while (it.hasNext()) {
                this.dependencies.add(it.next());
            }
        }
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getAssignee() { return assignee; }
    public String getPriority() { return priority; }
    public TaskStatus getStatus() { return status; }
    public CustomLinkedList<String> getDependencies() {
        CustomLinkedList<String> copy = new CustomLinkedList<>();
        Iterator<String> it = dependencies.iterator();
        while (it.hasNext()) {
            copy.add(it.next());
        }
        return copy;
    }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setAssignee(String assignee) { this.assignee = assignee; }
    public void setPriority(String priority) {
        String[] priorities = {"High", "Medium", "Low"};
        for (String p : priorities) {
            if (p.equals(priority)) {
                this.priority = priority;
                return;
            }
        }
        throw new IllegalArgumentException("Priority must be High, Medium, or Low.");
    }
    public void setStatus(String statusStr) {
        if ("PENDING".equals(statusStr)) this.status = TaskStatus.PENDING;
        else if ("IN_PROGRESS".equals(statusStr)) this.status = TaskStatus.IN_PROGRESS;
        else if ("COMPLETED".equals(statusStr)) this.status = TaskStatus.COMPLETED;
        else this.status = TaskStatus.PENDING;
    }
    public void setStatus(TaskStatus status) { this.status = status; }
    public void setDependencies(CustomLinkedList<String> deps) {
        dependencies.clear();
        if (deps != null) {
            Iterator<String> it = deps.iterator();
            while (it.hasNext()) {
                dependencies.add(it.next());
            }
        }
    }

    // Helper for priority number (High=1 highest, for PriorityQueue min-heap)
    public int getPriorityNum() {
        if ("High".equals(priority)) return 1;
        if ("Medium".equals(priority)) return 2;
        return 3;
    }

    // Display
    @Override
    public String toString() {
        String depsStr = "";
        Iterator<String> it = dependencies.iterator();
        if (it.hasNext()) {
            depsStr = ", Deps: [";
            boolean first = true;
            while (it.hasNext()) {
                if (!first) depsStr += ", ";
                depsStr += it.next();
                first = false;
            }
            depsStr += "]";
        }
        return "[" + priority + "] " + title + " - " + description +
               " (Assignee: " + assignee + ", Status: " + status + depsStr + ")";
    }
}

// Enum for Task Status
enum TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED
}