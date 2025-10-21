package com.kanban;

public class Task{
    private static int nextId = 1; // simple auto-ID generator
    private int id;
    private String Title;
    private String description;
    private String priority; // High, Medium, Low
    private String assignee;
    private String status; // TO-DO, PROGRESS, DONE

    public Task(){
        id = nextId++;
        Title = "";
        description = "";
        priority = "Low";
        assignee = "Unassigned";
        status = "TO-DO";
    }

    public Task(String tit, String desc, String assign){
        id = nextId++;
        if(tit == null || desc.trim().equals("")){
            Title = "Untitled Task";
        } else {
            Title = tit;
        }

        if(desc == null || desc.trim().equals("")){
            description = "Enter description";
        } else{
            description = desc;
        }

        if(assign == null || assign.trim().equals("")){
            assignee = "Unassigned";
        } else{
            assignee = assign;
        }

        priority = "Low";
        status = "TO-DO";
    }

    public int getId(){
        return id;
    }

    public String getTitle(){
        return Title;
    }

    public String getDescription(){
        return description;
    }

    public String getPriority(){
        return priority;
    }

    public String getAssignee(){
        return assignee;
    }

    public String getStatus(){
        return status;
    }

    public void setPriority(String newPriority){
        priority = newPriority;
    }

    public void setStatus(String newStatus){
        status = newStatus;
    }

    public void displayTask() {
        System.out.println("Task ID: "+ id);
        System.out.println("Title: "+ Title);
        System.out.println("Description: "+ description);
        System.out.println("Priority: "+ priority);
        System.out.println("Assignee: "+ assignee);
        System.out.println("Status: "+ status);
    }
}