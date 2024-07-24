package edu.riesco.persistence;

import edu.riesco.domain.Task;
import edu.riesco.domain.TaskRepository;
import edu.riesco.exception.TaskNotFoundException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MemoryTaskRepository implements TaskRepository {
    private final List<Task> tasks = new ArrayList<>();

    @Override
    public boolean hasTasks() {
        return !tasks.isEmpty();
    }

    @Override
    public int addTask(Task task) {
        tasks.add(task);
        return tasks.size();  // Last Task ID. IDs start in 1
    }

    @Override
    public Task taskById(int id) {
        try {
            return tasks.get(id - 1);  // IDs start in 1, List index starts in 0
        } catch (IndexOutOfBoundsException e) {
            throw new TaskNotFoundException("Task with id " + id + " not found.");
        }
    }

    @Override
    public List<Task> tasks() {
        return new ArrayList<>(tasks);
    }

    @Override
    public List<String> tasksAsJson() {
        return tasks.stream().map(Task::toJson).collect(Collectors.toList());
    }

    public void markAsComplete(int id) {
        taskById(id).markAsComplete();
    }

    public void markAsPending(int id) {
        taskById(id).markAsPending();
    }

    @Override
    public void updateTask(int id, String title, String description, LocalDate dueDate) {
        taskById(id).update(title, description, dueDate);
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(taskById(id));
    }
}
