package edu.riesco.domain;

import edu.riesco.exception.TaskNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class TaskManager {
    private final TaskRepository taskRepository;

    public TaskManager(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public boolean hasTasks() {
        return !taskRepository.getAll().isEmpty();
    }

    public boolean hasTask(int id) {
        try {
            taskRepository.getById(id); // Task exist
            return true;
        } catch (TaskNotFoundException e) {
            return false;
        }
    }

    public int addTask(String title, String description, LocalDate dueDate) {
        Task newTask = Task.from(title, description, dueDate);
        return taskRepository.create(newTask);
    }

    Task taskById(int id) {
        return taskRepository.getById(id);
    }

    public List<Task> tasks() {
        return taskRepository.getAll();
    }

    public List<String> tasksAsJson() {
        return taskRepository.getAll().stream().map(Task::toJson).collect(Collectors.toList());
    }

    // Tasks are read-only
    public String getTaskTitle(int id) {
        return taskRepository.getById(id).getTitle();
    }

    public String getTaskDescription(int id) {
        return taskRepository.getById(id).getDescription();
    }

    public String getTaskDueDate(int id) {
        LocalDate dueDate = taskRepository.getById(id).getDueDate();
        return dueDate == null ? "" : dueDate.toString();
    }

    public TaskStatus getTaskStatus(int id) {
        return taskRepository.getById(id).getStatus();
    }


    // As Task object is immutable, all this "update" commands create a new Task with the new data.

    public void markAsComplete(int id) {
        Task task = taskRepository.getById(id);
        Task newTask = task.withStatus(TaskStatus.COMPLETED);
        taskRepository.update(id, newTask);
    }

    public void markAsPending(int id) {
        Task task = taskRepository.getById(id);
        Task newTask = task.withStatus(TaskStatus.PENDING);
        taskRepository.update(id, newTask);
    }

    public void updateTask(int id, String title, String description, LocalDate dueDate) {
        Task task = taskRepository.getById(id);
        String newTitle = (title != null) ? title : task.getTitle();
        String newDescription = (description != null) ? description : task.getDescription();
        LocalDate newDueDate = (dueDate != null) ? dueDate : task.getDueDate();
        Task newTask = task.withTitle(newTitle).withDescription(newDescription).withDueDate(newDueDate);
        taskRepository.update(id, newTask);
    }

    public void deleteTask(int id) {
        taskRepository.delete(id);
    }
}
