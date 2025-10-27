package com.kanban;

import java.util.*;
import java.util.stream.Collectors;

public class Stage {
    private String name;
    public Queue<Task> tasks = new LinkedList<>();  // Public for Board access if needed, but use methods

    public Stage(String name) {
        this.name = name;
    }

    public void addTask(Task t) {
        tasks.offer(t);
    }

    public void showAllTasks() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks.");
            return;
        }
        for (Task t : tasks) {
            System.out.println(t);
        }
    }

    public int getTaskCount() {
        return tasks.size();
    }

    public void listTasksWithIndex() {
        Iterator<Task> it = tasks.iterator();
        int index = 1;
        while (it.hasNext()) {
            System.out.println(index + ". " + it.next());
            index++;
        }
    }

    public Task getTaskAtIndex(int index) {
        if (index < 0 || index >= tasks.size()) {
            return null;
        }
        Iterator<Task> it = tasks.iterator();
        Task current = null;
        for (int i = 0; i <= index; i++) {
            current = it.next();
        }
        return current;
    }

    public String getName() {
        return name;
    }

    public Task removeTaskAtIndex(int index) {
        if (index < 0 || index >= tasks.size()) {
            return null;
        }

        Iterator<Task> it = tasks.iterator();
        Task removed = null;
        int currentIndex = 0;

        while (it.hasNext()) {
            Task t = it.next();
            if (currentIndex == index) {
                removed = t;
                it.remove();
                break;
            }
            currentIndex++;
        }

        return removed;
    }

    public int findTaskIndex(String id) {
        int index = 0;
        for (Task t : tasks) {
            if (t.getId().equals(id)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public boolean deleteTaskAtIndex(int index) {
        Task removed = removeTaskAtIndex(index);
        if (removed != null) {
            System.out.println("Task '" + removed.getTitle() + "' deleted successfully.");
            return true;
        }
        System.out.println("Invalid task number. Nothing deleted.");
        return false;
    }

    // Search by keyword
    public void searchByKeyword(String keyword) {
        keyword = keyword.toLowerCase();
        List<Task> results = new ArrayList<>();

        for (Task t : tasks) {
            if (t.getTitle().toLowerCase().contains(keyword) ||
                t.getDescription().toLowerCase().contains(keyword)) {
                results.add(t);
            }
        }

        if (results.isEmpty()) {
            System.out.println("No matching tasks in " + name + ".");
        } else {
            System.out.println("\nMatches found in " + name + " stage:");
            for (int i = 0; i < results.size(); i++) {
                Task t = results.get(i);
                System.out.println((i + 1) + ". " + t.getTitle() + " (Priority: " + t.getPriority() + ")");
            }
        }
    }

    // Filter by priority (High / Medium / Low)
    public void filterByPriority(String priority) {
        priority = priority.toLowerCase(); // for case-insensitive match
        List<Task> results = new ArrayList<>();

        for (Task t : tasks) {
            if (t.getPriority().toLowerCase().equals(priority)) {
                results.add(t);
            }
        }

        if (results.isEmpty()) {
            System.out.println("No tasks with priority '" + priority + "' in " + name + ".");
        } else {
            System.out.println("\nTasks with priority '" + priority + "' in " + name + ":");
            for (int i = 0; i < results.size(); i++) {
                Task t = results.get(i);
                System.out.println((i + 1) + ". " + t.getTitle() + " (Assignee: " + t.getAssignee() + ")");
            }
        }
    }

    // Filter by assignee
    public void filterByAssignee(String assignee) {
        assignee = assignee.toLowerCase();
        List<Task> results = new ArrayList<>();

        for (Task t : tasks) {
            if (t.getAssignee().toLowerCase().contains(assignee)) {
                results.add(t);
            }
        }

        if (results.isEmpty()) {
            System.out.println("No tasks assigned to '" + assignee + "' in " + name + ".");
        } else {
            System.out.println("\nTasks assigned to '" + assignee + "' in " + name + ":");
            for (int i = 0; i < results.size(); i++) {
                Task t = results.get(i);
                System.out.println((i + 1) + ". " + t.getTitle() + " (Priority: " + t.getPriority() + ")");
            }
        }
    }

    // Save all tasks in this stage to a file
    public void saveToFile(String filename) {
        try {
            java.io.FileWriter writer = new java.io.FileWriter(filename, true); // append mode
            for (Task t : tasks) {
                String depsStr = t.getDependencies().stream().collect(Collectors.joining(","));
                writer.write(name + "|" + t.getId() + "|" + t.getTitle() + "|" + t.getDescription() + "|" +
                            t.getPriority() + "|" + t.getAssignee() + "|" + t.getStatus().name() + "|" + depsStr + "\n");
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("Error saving tasks for stage " + name + ": " + e.getMessage());
        }
    }

    // Load tasks for this stage from file
    public void loadFromFile(String filename) {
        try {
            java.io.File file = new java.io.File(filename);
            if (!file.exists()) return; // no data yet

            tasks.clear(); // Clear existing
            java.util.Scanner reader = new java.util.Scanner(file);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                String[] parts = line.split("\\|");
                if (parts.length >= 7 && parts[0].equals(name)) { // >=7 for optional deps
                    Task t = new Task(); // no-arg
                    t.setId(parts[1]);
                    t.setTitle(parts[2]);
                    t.setDescription(parts[3]);
                    t.setPriority(parts[4]);
                    t.setAssignee(parts[5]);
                    t.setStatus(parts[6]);
                    if (parts.length > 7 && !parts[7].isEmpty()) {
                        String[] depParts = parts[7].split(",");
                        List<String> deps = Arrays.asList(depParts);
                        t.setDependencies(deps);
                    }
                    tasks.offer(t);
                }
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Error loading tasks for stage " + name + ": " + e.getMessage());
        }
    }
}