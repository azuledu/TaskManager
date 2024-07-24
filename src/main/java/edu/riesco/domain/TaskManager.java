package edu.riesco.domain;

import com.google.gson.Gson;
import edu.riesco.exception.TaskNotFoundException;

import java.time.LocalDate;
import java.util.List;

public class TaskManager {
    private final TaskRepository taskRepository;

    public TaskManager(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public boolean hasTasks() {
        return taskRepository.hasTasks();
    }

    public boolean hasTask(int id) {
        try {
            taskRepository.taskById(id); // Task exist
            return true;
        } catch (TaskNotFoundException e) {
            return false;
        }
    }

    public int addTask(String title, String description, LocalDate dueDate) {
        Task newTask = Task.create(title, description, dueDate);
        return taskRepository.addTask(newTask);
    }

    public boolean isPending(int id) {
        return taskRepository.taskById(id).isPending();
    }

    public void markAsComplete(int id) {
        taskRepository.markAsComplete(id);
    }

    public void markAsPending(int id) {
        taskRepository.markAsPending(id);
    }

    Task taskById(int id) {
        return taskRepository.taskById(id);
    }

    public List<Task> tasks() {
        return taskRepository.tasks();
    }

    public String tasksAsJson() {
        return new Gson().toJson(taskRepository.tasks());
    }

    public String getTaskTitle(int id) {
        return taskRepository.taskById(id).getTitle();
    }

    public String getTaskDescription(int id) {
        return taskRepository.taskById(id).getDescription();
    }

    public String getTaskDueDate(int id) {
        LocalDate dueDate = taskRepository.taskById(id).getDueDate();
        return dueDate == null ? "" : dueDate.toString();
    }

    public void updateTask(int id, String title, String description, LocalDate dueDate) {
        taskRepository.updateTask(id, title, description, dueDate);
    }

    public void deleteTask(int id) {
        taskRepository.deleteTask(id);
    }
}
