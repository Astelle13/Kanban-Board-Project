package com.kanban;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class Task {
    private String id;
    private String title;
    private String description;
    private String assignee;
    private String priority;  // "High", "Medium", "Low"
    private TaskStatus status; // PENDING, IN_PROGRESS, COMPLETED
    private LinkedList<String> dependencies = new LinkedList<>(); // For dependency tracking

    // No-arg constructor (for serialization/loading)
    public Task() {}

    // Constructor (default priority + pending status + empty deps)
    public Task(String title, String description, String assignee) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.assignee = assignee;
        this.priority = "Medium"; // default
        this.status = TaskStatus.PENDING;
        this.dependencies = new LinkedList<>();
    }

    // Constructor with priority and deps
    public Task(String title, String description, String assignee, String priority, List<String> deps) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.assignee = assignee;
        setPriority(priority);
        this.status = TaskStatus.PENDING;
        this.dependencies = new LinkedList<>(deps != null ? deps : Collections.emptyList());
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getAssignee() { return assignee; }
    public String getPriority() { return priority; }
    public TaskStatus getStatus() { return status; }
    public List<String> getDependencies() { return new ArrayList<>(dependencies); }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setAssignee(String assignee) { this.assignee = assignee; }
    public void setPriority(String priority) {
        if (!Arrays.asList("High", "Medium", "Low").contains(priority)) {
            throw new IllegalArgumentException("Priority must be High, Medium, or Low.");
        }
        this.priority = priority;
    }
    public void setStatus(String statusStr) {
        try {
            this.status = TaskStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            this.status = TaskStatus.PENDING;
        }
    }
    public void setStatus(TaskStatus status) { this.status = status; }
    public void setDependencies(List<String> deps) {
        this.dependencies = new LinkedList<>(deps != null ? deps : Collections.emptyList());
    }

    // Helper for priority number (High=1 highest, for PriorityQueue min-heap)
    public int getPriorityNum() {
        switch (priority) {
            case "High": return 1;
            case "Medium": return 2;
            case "Low": return 3;
            default: return 3;
        }
    }

    // Display
    @Override
    public String toString() {
        return "[" + priority + "] " + title + " - " + description +
               " (Assignee: " + assignee + ", Status: " + status + 
               (dependencies.isEmpty() ? "" : ", Deps: " + dependencies) + ")";
    }
}

// Enum for Task Status
enum TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED
}