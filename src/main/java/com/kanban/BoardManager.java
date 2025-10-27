package com.kanban;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class BoardManager {
    private static Map<String, Board> boards = new HashMap<>();

    public static void createBoard(String name) {
        if (boards.containsKey(name)) {
            System.out.println("Board '" + name + "' already exists.");
            return;
        }
        boards.put(name, new Board(name));
        System.out.println("Board '" + name + "' created.");
    }

    public static void listBoards() {
        if (boards.isEmpty()) {
            System.out.println("No boards available.");
            return;
        }
        System.out.println("Available Boards:");
        for (String boardName : boards.keySet()) {
            System.out.println("- " + boardName);
        }
    }

    public static Board selectBoard(String name) {
        return boards.get(name);
    }

    public static void deleteBoard(String name) {
        Board b = boards.remove(name);
        if (b == null) {
            System.out.println("Board '" + name + "' not found.");
            return;
        }
        String fileName = name + "_data.txt";
        new File(fileName).delete();
        System.out.println("Board '" + name + "' deleted.");
    }

    public static void renameBoard(String oldName, String newName) {
        if (!boards.containsKey(oldName)) {
            System.out.println("Board '" + oldName + "' not found.");
            return;
        }
        if (boards.containsKey(newName)) {
            System.out.println("Board '" + newName + "' already exists.");
            return;
        }
        Board b = boards.get(oldName);
        String oldFile = oldName + "_data.txt";
        String newFile = newName + "_data.txt";
        b.save(oldFile); // Ensure saved
        File oldF = new File(oldFile);
        File newF = new File(newFile);
        if (oldF.renameTo(newF)) {
            boards.remove(oldName);
            b.setName(newName);
            boards.put(newName, b);
            System.out.println("Board renamed to '" + newName + "'.");
        } else {
            System.out.println("Failed to rename file.");
        }
    }
}