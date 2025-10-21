package com.kanban;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.ArrayList; // Add this import

public class Stage {
    private String name;
    private Queue<Task> tasks = new LinkedList<>();

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
}
