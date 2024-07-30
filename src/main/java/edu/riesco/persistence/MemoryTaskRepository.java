package edu.riesco.persistence;

import edu.riesco.domain.Task;
import edu.riesco.domain.TaskRepository;
import edu.riesco.exception.EmptyRepositoryException;
import edu.riesco.exception.TaskNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryTaskRepository implements TaskRepository {
    private final List<Task> tasks = new ArrayList<>();

    @Override
    public int create(Task task) {
        tasks.add(task);
        return tasks.size();  // Last Task ID. IDs start in 1
    }

    @Override
    public Task getById(int id) {
        if (tasks.isEmpty()) {
            throw new EmptyRepositoryException("Operation not allowed in an empty repository.");
        }
        try {
            return tasks.get(id - 1);  // IDs start in 1, List index starts in 0
        } catch (IndexOutOfBoundsException e) {
            throw new TaskNotFoundException("Task with id " + id + " not found.");
        }
    }

    @Override
    public List<Task> getAll() {
        return Collections.unmodifiableList(tasks);
    }

    // "Update" means "put a new task in the same location" to preserve the old Task ID.
    @Override
    public void update(int id, Task newTask) {
        getById(id);  // To check if the repository is empty or the ID does not exist.
        tasks.set(id - 1, newTask);
    }

    @Override
    public void delete(int id) {
        Task task = getById(id);  // To check if the repository is empty or the ID does not exist.
        tasks.remove(task);
    }
}
