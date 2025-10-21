package com.kanban;

import java.util.Arrays;

public class Task {
    private String title;
    private String description;
    private String assignee;
    private String priority;  // New field: "High", "Medium", "Low"

    // Existing no-arg constructor
    public Task() {}

    // Existing constructor
    public Task(String title, String description, String assignee) {
        this.title = title;
        this.description = description;
        this.assignee = assignee;
        this.priority = "Medium";  // Default
    }

    // New constructor with priority
    public Task(String title, String description, String assignee, String priority) {
        this.title = title;
        this.description = description;
        this.assignee = assignee;
        setPriority(priority);  // Use setter for validation
    }

    // Getters
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getAssignee() { return assignee; }
    public String getPriority() { return priority; }

    // Setter for priority with validation
    public void setPriority(String priority) {
        if (!Arrays.asList("High", "Medium", "Low").contains(priority)) {
            throw new IllegalArgumentException("Priority must be High, Medium, or Low.");
        }
        this.priority = priority;
    }

    // Assume toString or other methods exist; update if needed for display
    @Override
    public String toString() {
        return title + " - " + description + " - " + assignee + " (Priority: " + priority + ")";
    }
}