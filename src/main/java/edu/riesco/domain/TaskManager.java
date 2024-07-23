package edu.riesco.domain;

import com.google.gson.Gson;
import edu.riesco.exception.TaskNotFound;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private final List<Task> tasks = new ArrayList<>();
    private int nextId = 1;

    public boolean hasTasks() {
        return !tasks.isEmpty();
    }

    public boolean hasTask(int id) {
        try {
            taskById(id); // Task exist
            return true;
        } catch (TaskNotFound e) {
            return false;
        }
    }

    public int addTask(String title, String description, LocalDate dueDate) {
        int taskId = nextId++;
        Task newTask = Task.create(taskId, title, description, dueDate);
        tasks.add(newTask);
        return taskId;
    }

    public boolean isPending(int id) {
        return taskById(id).isPending();
    }

    public void markAsComplete(int id) {
        taskById(id).markAsComplete();
    }

    public void markAsPending(int id) {
        taskById(id).markAsPending();
    }

    public List<Task> tasks() {
        return new ArrayList<>(tasks);
    }

    public String tasksAsJson() {
        return new Gson().toJson(tasks);
    }

    public String getTaskTitle(int id) {
        return taskById(id).getTitle();
    }

    public String getTaskDescription(int id) {
        return taskById(id).getDescription();
    }

    public String getTaskDueDate(int id) {
        LocalDate dueDate = taskById(id).getDueDate();
        return dueDate == null ? "" : dueDate.toString();
    }

    private Task taskById(int id) {
        return tasks.stream()
                .filter(task -> task.getId() == id)
                .findFirst()
                .orElseThrow(() -> new TaskNotFound("Task with id \" + id + \" not found."));
    }

    public void updateTask(int id, String title, String description, LocalDate dueDate) {
        taskById(id).update(title, description, dueDate);
    }

    public void deleteTask(int id) {
        tasks.remove(taskById(id));
    }
}
