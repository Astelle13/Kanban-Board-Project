package com.kanban;

import java.util.Comparator;
import java.util.Iterator;

public class Board {
    private String name;
    private CustomHashMap<String, Stage> stages = new CustomHashMap<>();
    private CustomHashMap<String, Task> allTasks = new CustomHashMap<>();
    private CustomPriorityQueue<Task> priQ = new CustomPriorityQueue<>(new TaskComparator());

    public Board(String name) {
        this.name = name;
        stages.put("TO-DO", new Stage("TO-DO"));
        stages.put("IN-PROGRESS", new Stage("IN-PROGRESS"));
        stages.put("DONE", new Stage("DONE"));
    }

    // Create and add task to stage
    public void createAndAddTask(String title, String desc, String assignee, String priority, String stageName, CustomLinkedList<String> deps) {
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
            System.out.println("Cannot move: dependencies not met for task " + taskId);
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

    // Relaxed dep check: Allow if no deps or to IN_PROGRESS/DONE
    public boolean canMoveTask(String taskId) {
        Task t = allTasks.get(taskId);
        if (t == null) return false;
        if (t.getDependencies().isEmpty()) {
            System.out.println("No deps - move allowed for task " + taskId);
            return true; // Always allow no deps
        }
        // Only check if moving to DONE (strict)
        // For IN_PROGRESS, allow if PENDING
        Iterator<String> it = t.getDependencies().iterator();
        while (it.hasNext()) {
            String depId = it.next();
            Task dep = allTasks.get(depId);
            if (dep == null || dep.getStatus() != TaskStatus.COMPLETED) {
                System.out.println("Dep not completed: " + depId + " for task " + taskId);
                return false;
            }
        }
        System.out.println("All deps completed for task " + taskId);
        return true;
    }

    private TaskStatus getStatusForStage(String stage) {
        if ("TO-DO".equals(stage)) return TaskStatus.PENDING;
        if ("IN-PROGRESS".equals(stage)) return TaskStatus.IN_PROGRESS;
        if ("DONE".equals(stage)) return TaskStatus.COMPLETED;
        return TaskStatus.PENDING;
    }

    // Get high priority snapshot (restore queue after)
    public CustomArrayList<Task> getHighPriorityTasks() {
        CustomArrayList<Task> high = new CustomArrayList<>();
        CustomPriorityQueue<Task> temp = new CustomPriorityQueue<>(priQ.comparator);
        while (!priQ.isEmpty()) {
            high.add(priQ.poll());
        }
        // Restore (simplified, in practice re-offer from allTasks high pri)
        Iterator<Task> it = getAllTasksIterator();
        while (it.hasNext()) {
            Task tt = it.next();
            if ("High".equals(tt.getPriority())) {
                priQ.offer(tt);
            }
        }
        return high;
    }

    // Helper for allTasks iterator (fixed type)
    private Iterator<Task> getAllTasksIterator() {
        return new Iterator<Task>() {
            Iterator<Task> it = allTasks.valuesIterator(); // Correct type for V=Task
            public boolean hasNext() {
                return it.hasNext();
            }
            public Task next() {
                return it.next();
            }
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
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

    public Task getTask(String taskId) {
        return allTasks.get(taskId);
    }

    public Stage getStage(String stageName) {
        return stages.get(stageName);
    }

    public CustomArrayList<Stage> getAllStages() {
        CustomArrayList<Stage> stageList = new CustomArrayList<>();
        Iterator<Stage> it = new Iterator<Stage>() {
            Iterator<Stage> rawIt = stages.valuesIterator();  // Correct type for V=Stage
            public boolean hasNext() {
                return rawIt.hasNext();
            }
            public Stage next() {
                return rawIt.next();
            }
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        while (it.hasNext()) {
            stageList.add(it.next());
        }
        return stageList;
    }

    public CustomArrayList<String> getStageKeys() {
        CustomArrayList<String> keys = new CustomArrayList<>();
        Iterator<String> it = new Iterator<String>() {
            Iterator<String> rawIt = stages.keySetIterator();  // Correct type for K=String
            public boolean hasNext() {
                return rawIt.hasNext();
            }
            public String next() {
                return rawIt.next();
            }
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        while (it.hasNext()) {
            keys.add(it.next());
        }
        return keys;
    }

    public void save(String filename) {
        try {
            new java.io.FileWriter(filename).close(); // Clear file
        } catch (Exception e) {}
        Iterator<Stage> it = getAllStagesIterator();
        while (it.hasNext()) {
            Stage s = it.next();
            s.saveToFile(filename);
        }
        System.out.println("Board saved.");
    }

    // Helper for stages iterator (fixed type)
    private Iterator<Stage> getAllStagesIterator() {
        return new Iterator<Stage>() {
            Iterator<Stage> rawIt = stages.valuesIterator();  // Correct type for V=Stage
            public boolean hasNext() {
                return rawIt.hasNext();
            }
            public Stage next() {
                return rawIt.next();
            }
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public void load(String filename) {
        allTasks.clear();
        priQ = new CustomPriorityQueue<>(new TaskComparator());
        Iterator<Stage> it = getAllStagesIterator();
        while (it.hasNext()) {
            Stage s = it.next();
            s.loadFromFile(filename);
        }
        // Rebuild allTasks and priQ
        it = getAllStagesIterator();
        while (it.hasNext()) {
            Stage s = it.next();
            Iterator<Task> taskIt = s.taskIterator();
            while (taskIt.hasNext()) {
                Task t = taskIt.next();
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