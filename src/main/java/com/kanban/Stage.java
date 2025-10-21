package com.kanban;

public class Stage{
    private String name;
    private Task[] tasks;
    private int count;

    public Stage(String stageName){
        name = stageName;
        tasks = new Task[100]; //fixed max for simplicity
        count=0;
    }

    public String getName(){
        return name;
    }

    public boolean addTask(Task t){
        if(count < tasks.length && t != null){
            tasks[count] = t;
            count++;
            return true;
        }
        return false;
    }


    public boolean removeTaskById(int id){
        for(int i = 0; i<count; i++){
            if(tasks[i].getId() == id){
                for(int j = i; j < count-1; j++){
                    tasks[j] = tasks[j+1];
                }
                count--;
                return true;
            }
        }
        return false;
    }

    public Task getTaskById(int id){
        for(int i = 0; i < count; i++){
            if(tasks[i].getId() == id){
                return tasks[i];
            }
        }
        return null;
    }


    public void showAllTasks() {
        System.out.println("=== "+ name+ " ===");
        if(count == 0){
            System.out.println("No tasks here yet.\n");
            return;
        }
        for(int i=0; i<count; i++){
            tasks[i].displayTask();
        }
    }

    public int getTaskCount(){
        return count;
    }
}
