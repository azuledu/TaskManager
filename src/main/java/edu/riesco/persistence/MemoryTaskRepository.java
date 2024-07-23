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
    public int addTask(Task task) {
        tasks.add(task);
        return tasks.size();  // Last Task ID. IDs start in 1
    }

    @Override
    public Task taskById(int id) {
        try {
            return tasks.get(id - 1);  // IDs start in 1, List index starts in 0
        } catch (IndexOutOfBoundsException e) {
            throw new TaskNotFound("Task with id " + id + " not found.");
        }
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
