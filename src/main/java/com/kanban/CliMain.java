// Updated Main.java (add import for Scanner)
package com.kanban;

import java.util.Scanner;

public class CliMain {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Initialize default board
        BoardManager.createBoard("Default");

        String currentBoardName = "Default";
        Board currentBoard = BoardManager.selectBoard(currentBoardName);
        currentBoard.load(currentBoardName + "_data.txt");

        boolean running = true;
        while (running) {
            showBoardManagerMenu();
            System.out.print("Enter choice: ");
            int boardChoice = sc.nextInt();
            sc.nextLine();

            switch (boardChoice) {
                case 1: // Create Board
                    System.out.print("Enter board name: ");
                    String newBoardName = sc.nextLine().trim();
                    if (!newBoardName.isEmpty()) {
                        BoardManager.createBoard(newBoardName);
                    }
                    break;
                case 2: // List Boards
                    BoardManager.listBoards();
                    break;
                case 3: // Select Board
                    System.out.print("Enter board name: ");
                    String selectName = sc.nextLine().trim();
                    Board selBoard = BoardManager.selectBoard(selectName);
                    if (selBoard != null) {
                        if (currentBoard != null) {
                            currentBoard.save(currentBoardName + "_data.txt");
                        }
                        currentBoardName = selectName;
                        currentBoard = selBoard;
                        currentBoard.load(selectName + "_data.txt");
                        System.out.println("Switched to board '" + selectName + "'.");
                        enterBoardMenu(currentBoard, sc, currentBoardName);
                    } else {
                        System.out.println("Board not found.");
                    }
                    break;
                case 4: // Delete Board
                    System.out.print("Enter board to delete: ");
                    String delName = sc.nextLine().trim();
                    if (!delName.equals(currentBoardName)) {
                        System.out.print("Confirm delete (y/n): ");
                        String confirm = sc.nextLine().trim().toLowerCase();
                        if (confirm.equals("y")) {
                            BoardManager.deleteBoard(delName);
                        }
                    } else {
                        System.out.println("Cannot delete current board. Switch first.");
                    }
                    break;
                case 5: // Rename Board
                    System.out.print("Enter board to rename: ");
                    String oldName = sc.nextLine().trim();
                    if (!oldName.equals(currentBoardName)) {
                        System.out.print("Enter new name: ");
                        String newName = sc.nextLine().trim();
                        BoardManager.renameBoard(oldName, newName);
                    } else {
                        System.out.println("Cannot rename current board. Switch first.");
                    }
                    break;
                case 0: // Exit
                    if (currentBoard != null) {
                        currentBoard.save(currentBoardName + "_data.txt");
                    }
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }

        sc.close();
    }

    private static void showBoardManagerMenu() {
        System.out.println("\n===== BOARD MANAGER =====");
        System.out.println("1. Create New Board");
        System.out.println("2. List Boards");
        System.out.println("3. Select Board (Enter Tasks)");
        System.out.println("4. Delete Board");
        System.out.println("5. Rename Board");
        System.out.println("0. Exit");
    }

    private static void enterBoardMenu(Board board, Scanner sc, String currentBoardName) {
        int choice = -1;
        while (choice != 0) {
            System.out.println("\n===== KANBAN BOARD: " + board.getName() + " =====");
            System.out.println("1. Add Task");
            System.out.println("2. View All Tasks");
            System.out.println("3. Update Task Priority");
            System.out.println("4. Move Task Between Stages");
            System.out.println("5. Delete Task");
            System.out.println("6. Search / Filter Tasks");
            System.out.println("7. View Urgent (High Priority) Tasks");
            System.out.println("0. Exit Board (Save & Back)");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("\n--- Add Task ---");
                    System.out.print("Enter Title: ");
                    String title = sc.nextLine();
                    System.out.print("Enter Description: ");
                    String desc = sc.nextLine();
                    System.out.print("Enter Assignee: ");
                    String assign = sc.nextLine();
                    System.out.println("Select Priority: 1. High 2. Medium 3. Low");
                    int priChoice = sc.nextInt();
                    sc.nextLine();
                    String priority = getPriorityFromChoice(priChoice);
                    System.out.print("Dependencies (comma-sep IDs or none): ");
                    String depsInput = sc.nextLine();
                    CustomLinkedList<String> deps = new CustomLinkedList<>();
                    if (!depsInput.isEmpty()) {
                        String[] depParts = depsInput.split(",");
                        for (String d : depParts) {
                            deps.add(d.trim());
                        }
                    }
                    System.out.println("Select Stage: 1. TO-DO 2. IN-PROGRESS 3. DONE");
                    int stageChoice = sc.nextInt();
                    sc.nextLine();
                    String stageName = getStageName(stageChoice);
                    board.createAndAddTask(title, desc, assign, priority, stageName, deps);
                    break;
                case 2:
                    System.out.println("\n--- All Tasks ---");
                    CustomArrayList<String> keys = board.getStageKeys();
                    for (int i = 0; i < keys.size(); i++) {
                        String key = keys.get(i);
                        Stage s = board.getStage(key);
                        System.out.println("\n" + key + ":");
                        s.showAllTasks();
                    }
                    break;
                case 3:
                    System.out.println("\n--- Update Task Priority ---");
                    System.out.println("Select Stage: 1. TO-DO 2. IN-PROGRESS 3. DONE");
                    int updateChoice = sc.nextInt();
                    sc.nextLine();
                    String updateStage = getStageName(updateChoice);
                    Stage selected = board.getStage(updateStage);
                    if (selected == null || selected.getTaskCount() == 0) {
                        System.out.println("No tasks or invalid stage.");
                        break;
                    }
                    System.out.println("\nTasks in " + selected.getName() + ":");
                    selected.listTasksWithIndex();
                    System.out.print("Enter task number: ");
                    int taskIndex = sc.nextInt() - 1;
                    sc.nextLine();
                    Task taskToUpdate = selected.getTaskAtIndex(taskIndex);
                    if (taskToUpdate == null) {
                        System.out.println("Invalid task.");
                        break;
                    }
                    System.out.println("Select New Priority: 1. High 2. Medium 3. Low");
                    int newPriChoice = sc.nextInt();
                    sc.nextLine();
                    String newPriority = getPriorityFromChoice(newPriChoice);
                    String oldPri = taskToUpdate.getPriority();
                    taskToUpdate.setPriority(newPriority);
                    if (!oldPri.equals("High") && newPriority.equals("High")) {
                        board.addToPriorityQueueIfHigh(taskToUpdate);
                    }
                    System.out.println("Priority updated to " + newPriority + " for '" + taskToUpdate.getTitle() + "'.");
                    break;
                case 4:
                    System.out.println("\n--- Move Task ---");
                    System.out.println("Source Stage: 1. TO-DO 2. IN-PROGRESS 3. DONE");
                    int srcChoice = sc.nextInt();
                    sc.nextLine();
                    String srcName = getStageName(srcChoice);
                    Stage src = board.getStage(srcName);
                    if (src == null || src.getTaskCount() == 0) {
                        System.out.println("No tasks.");
                        break;
                    }
                    System.out.println("\nTasks in " + src.getName() + ":");
                    src.listTasksWithIndex();
                    System.out.print("Enter task number: ");
                    int moveIndex = sc.nextInt() - 1;
                    sc.nextLine();
                    Task taskToMove = src.getTaskAtIndex(moveIndex);
                    if (taskToMove == null) {
                        System.out.println("Invalid task.");
                        break;
                    }
                    System.out.println("Destination: 1. TO-DO 2. IN-PROGRESS 3. DONE");
                    int destChoice = sc.nextInt();
                    sc.nextLine();
                    String destName = getStageName(destChoice);
                    if (srcName.equals(destName)) {
                        System.out.println("Same stage.");
                        break;
                    }
                    board.moveTask(taskToMove.getId(), srcName, destName);
                    break;
                case 5:
                    System.out.println("\n--- Delete Task ---");
                    System.out.println("Select Stage: 1. TO-DO 2. IN-PROGRESS 3. DONE");
                    int delChoice = sc.nextInt();
                    sc.nextLine();
                    String delStageName = getStageName(delChoice);
                    Stage delStage = board.getStage(delStageName);
                    if (delStage == null || delStage.getTaskCount() == 0) {
                        System.out.println("No tasks.");
                        break;
                    }
                    System.out.println("\nTasks in " + delStage.getName() + ":");
                    delStage.listTasksWithIndex();
                    System.out.print("Enter task number: ");
                    int delIndex = sc.nextInt() - 1;
                    sc.nextLine();
                    System.out.print("Confirm delete (y/n): ");
                    String confirm = sc.nextLine().trim().toLowerCase();
                    if (confirm.equals("y")) {
                        Task removed = delStage.removeTaskAtIndex(delIndex);
                        if (removed != null) {
                            board.removeTask(removed.getId());
                            System.out.println("Deleted '" + removed.getTitle() + "'.");
                        }
                    } else {
                        System.out.println("Cancelled.");
                    }
                    break;
                case 6:
                    System.out.println("\n--- Search & Filter ---");
                    System.out.println("1. Keyword 2. Priority 3. Assignee");
                    int searchChoice = sc.nextInt();
                    sc.nextLine();
                    CustomArrayList<Stage> allStages = board.getAllStages();
                    if (searchChoice == 1) {
                        System.out.print("Keyword: ");
                        String keyword = sc.nextLine();
                        for (int i = 0; i < allStages.size(); i++) {
                            allStages.get(i).searchByKeyword(keyword);
                        }
                    } else if (searchChoice == 2) {
                        System.out.print("Priority (High/Medium/Low): ");
                        String pr = sc.nextLine();
                        for (int i = 0; i < allStages.size(); i++) {
                            allStages.get(i).filterByPriority(pr);
                        }
                    } else if (searchChoice == 3) {
                        System.out.print("Assignee: ");
                        String assigneeFilter = sc.nextLine();
                        for (int i = 0; i < allStages.size(); i++) {
                            allStages.get(i).filterByAssignee(assigneeFilter);
                        }
                    } else {
                        System.out.println("Invalid.");
                    }
                    break;
                case 7:
                    System.out.println("\n--- Urgent Tasks ---");
                    CustomArrayList<Task> urgents = board.getHighPriorityTasks();
                    if (urgents.isEmpty()) {
                        System.out.println("No high priority tasks.");
                    } else {
                        for (int i = 0; i < urgents.size(); i++) {
                            System.out.println((i + 1) + ". " + urgents.get(i));
                        }
                    }
                    break;
                case 0:
                    board.save(currentBoardName + "_data.txt");
                    System.out.println("Board saved. Back to manager.");
                    break;
                default:
                    System.out.println("Invalid.");
            }
        }
    }

    private static String getPriorityFromChoice(int choice) {
        switch (choice) {
            case 1: return "High";
            case 2: return "Medium";
            case 3: return "Low";
            default: return "Medium";
        }
    }

    private static String getStageName(int choice) {
        switch (choice) {
            case 1: return "TO-DO";
            case 2: return "IN-PROGRESS";
            case 3: return "DONE";
            default: return "TO-DO";
        }
    }
}