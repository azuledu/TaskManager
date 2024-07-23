package edu.riesco.persistence;

import edu.riesco.domain.Task;
import edu.riesco.exception.TaskNotFound;

import java.util.ArrayList;
import java.util.List;

public class MemoryTaskRepository implements TaskRepository {
    private final List<Task> tasks = new ArrayList<>();

    @Override
    public boolean hasTasks() {
        return !tasks.isEmpty();
    }

    @Override
    public void addTask(Task task) {
        tasks.add(task);
    }

    @Override
    public Task taskById(int id) {
        return tasks.stream()
                .filter(task -> task.getId() == id)
                .findFirst()
                .orElseThrow(() -> new TaskNotFound("Task with id " + id + " not found."));
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(taskById(id));
    }

    @Override
    public List<Task> tasks() {
        return new ArrayList<>(tasks);
    }
}
