package com.kanban;

import java.util.Scanner;

public class main {  // Fixed class name to follow Java conventions
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Stage toDo = new Stage("TO-DO");
        Stage inProgress = new Stage("IN-PROGRESS");
        Stage done = new Stage("DONE");

        int choice = -1;

        while (choice != 0) {
            System.out.println("\n===== KANBAN BOARD =====");
            System.out.println("1. Add Task");
            System.out.println("2. View All Tasks");
            System.out.println("3. Update Task Priority");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine(); // consume leftover newline

            switch (choice) {
                case 1:
                    System.out.println("\n--- Add Task ---");
                    System.out.print("Enter Title: ");
                    String title = sc.nextLine();

                    System.out.print("Enter Description: ");
                    String desc = sc.nextLine();

                    System.out.print("Enter Assignee: ");
                    String assign = sc.nextLine();

                    System.out.println("Select Priority:");
                    System.out.println("1. High");
                    System.out.println("2. Medium");
                    System.out.println("3. Low");
                    System.out.print("Enter priority number: ");
                    int priChoice = sc.nextInt();
                    sc.nextLine();  // consume newline after int
                    String priority = getPriorityFromChoice(priChoice);

                    System.out.println("Select Stage:");
                    System.out.println("1. TO-DO");
                    System.out.println("2. IN-PROGRESS");
                    System.out.println("3. DONE");
                    System.out.print("Enter stage number: ");
                    int stageChoice = sc.nextInt();
                    sc.nextLine();

                    Task t = new Task(title, desc, assign, priority);  // Updated constructor to include string priority

                    if (stageChoice == 1) {
                        toDo.addTask(t);
                        System.out.println("Task added to TO-DO");
                    } else if (stageChoice == 2) {
                        inProgress.addTask(t);
                        System.out.println("Task added to IN-PROGRESS");
                    } else if (stageChoice == 3) {
                        done.addTask(t);
                        System.out.println("Task added to DONE");
                    } else {
                        System.out.println("Invalid choice. Task not added.");
                    }
                    break;

                case 2:
                    System.out.println("\n--- All Tasks ---");
                    System.out.println("\nTO-DO:");
                    toDo.showAllTasks();  // Assume showAllTasks now prints string priority
                    System.out.println("\nIN-PROGRESS:");
                    inProgress.showAllTasks();
                    System.out.println("\nDONE:");
                    done.showAllTasks();
                    break;

                case 3:
                    System.out.println("\n--- Update Task Priority ---");
                    System.out.println("Select Stage to Update:");
                    System.out.println("1. TO-DO");
                    System.out.println("2. IN-PROGRESS");
                    System.out.println("3. DONE");
                    System.out.print("Enter stage number: ");
                    int updateStageChoice = sc.nextInt();
                    sc.nextLine();

                    Stage selectedStage = null;
                    if (updateStageChoice == 1) selectedStage = toDo;
                    else if (updateStageChoice == 2) selectedStage = inProgress;
                    else if (updateStageChoice == 3) selectedStage = done;
                    else {
                        System.out.println("Invalid stage. No update.");
                        break;
                    }

                    if (selectedStage.getTaskCount() == 0) {
                        System.out.println("No tasks in this stage.");
                        break;
                    }

                    // List tasks with indices
                    System.out.println("\nTasks in " + selectedStage.getName() + ":");
                    selectedStage.listTasksWithIndex();  // Assume new method to list with numbers

                    System.out.print("Enter task number to update: ");
                    int taskIndex = sc.nextInt() - 1;  // 0-based
                    sc.nextLine();

                    if (taskIndex < 0 || taskIndex >= selectedStage.getTaskCount()) {
                        System.out.println("Invalid task number.");
                        break;
                    }

                    System.out.println("Select New Priority:");
                    System.out.println("1. High");
                    System.out.println("2. Medium");
                    System.out.println("3. Low");
                    System.out.print("Enter priority number: ");
                    int newPriChoice = sc.nextInt();
                    sc.nextLine();

                    String newPriority = getPriorityFromChoice(newPriChoice);

                    Task taskToUpdate = selectedStage.getTaskAtIndex(taskIndex);  // Assume new method
                    taskToUpdate.setPriority(newPriority);  // Assume setter in Task
                    System.out.println("Priority updated to " + newPriority + " for task: " + taskToUpdate.getTitle());
                    break;

                case 0:
                    System.out.println("Exiting Kanban Board...");
                    break;

                default:
                    System.out.println("Invalid input. Try again.");
            }
        }

        sc.close();
    }

    // Helper method to map choice to priority string
    private static String getPriorityFromChoice(int choice) {
        switch (choice) {
            case 1: return "High";
            case 2: return "Medium";
            case 3: return "Low";
            default:
                System.out.println("Invalid priority choice. Defaulting to Medium.");
                return "Medium";
        }
    }
}