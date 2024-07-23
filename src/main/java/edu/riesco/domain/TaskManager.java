package edu.riesco.domain;

import com.google.gson.Gson;
import edu.riesco.exception.TaskNotFound;
import edu.riesco.persistence.TaskRepository;

import java.time.LocalDate;
import java.util.List;

public class TaskManager {
    private final TaskRepository taskRepository;

    private int nextId = 1;

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
        } catch (TaskNotFound e) {
            return false;
        }
    }

    public int addTask(String title, String description, LocalDate dueDate) {
        int taskId = nextId++;
        Task newTask = Task.create(taskId, title, description, dueDate);
        taskRepository.addTask(newTask);
        return taskId;
    }

    public boolean isPending(int id) {
        return taskRepository.taskById(id).isPending();
    }

    public void markAsComplete(int id) {
        taskRepository.taskById(id).markAsComplete();
    }

    public void markAsPending(int id) {
        taskRepository.taskById(id).markAsPending();
    }

    public List<Task> tasks() {
        return taskRepository.tasks();
        //return new ArrayList<>(tasks);
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
        taskRepository.taskById(id).update(title, description, dueDate);
    }

    public void deleteTask(int id) {
        taskRepository.deleteTask(id);
    }
}
