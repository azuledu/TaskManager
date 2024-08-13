package edu.riesco.domain;

import edu.riesco.exception.TaskNotFoundException;

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

    public int addTask(String title, String description, TaskDueDate dueDate) {
        Task newTask = Task.from(title, description, dueDate);
        return taskRepository.create(newTask);
    }

    public int addTask(String title, String description) {
        Task newTask = Task.from(title, description, new NoDueDate());
        return taskRepository.create(newTask);
    }

    public Task taskById(int id) {
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

    public String getPrintableTaskDueDate(int id) {
        return taskRepository.getById(id).getPrintableDueDate();
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

    public void updateTaskTitle(int id, String newTitle) {
        Task task = taskRepository.getById(id);
        Task newTask = task.withTitle(newTitle);
        taskRepository.update(id, newTask);
    }

    public void updateTaskDescription(int id, String newDescription) {
        Task task = taskRepository.getById(id);
        Task newTask = task.withDescription(newDescription);
        taskRepository.update(id, newTask);
    }

    public void updateTaskDueDate(int id, TaskDueDate newDueDate) {
        Task task = taskRepository.getById(id);
        Task newTask = task.withDueDate(newDueDate);
        taskRepository.update(id, newTask);
    }

    public void deleteTask(int id) {
        taskRepository.delete(id);
    }
}
