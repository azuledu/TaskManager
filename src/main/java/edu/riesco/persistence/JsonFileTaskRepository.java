package edu.riesco.persistence;

import edu.riesco.domain.Task;
import edu.riesco.domain.TaskRepository;
import edu.riesco.exception.EmptyRepositoryException;
import edu.riesco.exception.TaskNotFoundException;
import edu.riesco.exception.TaskRepositoryException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JsonFileTaskRepository implements TaskRepository {
    private final Path filePath;

    public JsonFileTaskRepository(String filePath) {
        this.filePath = Path.of(filePath);
    }

    @Override
    public int create(Task task) {
        try {
            Files.write(filePath, task.toJson().getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
            Files.write(filePath, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
            return lastTaskId();
        } catch (IOException e) {
            throw new TaskRepositoryException(e.getMessage());
        }
    }

    // The Task ID will be the line number in the file.
    private int lastTaskId() {
        try (Stream<String> fileStream = Files.lines(filePath)) {
            return (int) fileStream.count();
        } catch (IOException e) {
            throw new TaskRepositoryException(e.getMessage());
        }
    }

    @Override
    public List<Task> getAll() {
        if (!Files.exists(filePath)) return new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(filePath);
            return lines.stream().map(Task::fromJson).collect(Collectors.toList());
        } catch (IOException e) {
            throw new TaskRepositoryException(e.getMessage());
        }
    }

    @Override
    public Task getById(int id) {
        if (!Files.exists(filePath)) {
            throw new EmptyRepositoryException("Operation not allowed in an empty repository.");
        }
        try (Stream<String> lines = Files.lines(filePath)) {
            String taskAsJson = lines.skip(id - 1).findFirst()
                    .orElseThrow(() -> new TaskNotFoundException("Task with id " + id + " not found."));
            return Task.fromJson(taskAsJson);
        } catch (IOException e) {
            throw new TaskNotFoundException("Task with id " + id + " not found.");
        }
    }

    @Override
    public void delete(int id) {
        if (!Files.exists(filePath)) {
            throw new EmptyRepositoryException("Operation not allowed in an empty repository.");
        }
        try {
            // Read all tasks from the file
            List<String> lines = Files.readAllLines(filePath);

            // Check if id is within valid range
            if (id < 1 || id > lines.size()) {
                throw new TaskNotFoundException("Task with ID " + id + " not found.");
            }

            // Filter out the line to remove
            List<String> updatedLines = lines.stream()
                    .filter(line -> lines.indexOf(line) != id - 1)
                    .collect(Collectors.toList());

            // Write the remaining lines back to the file
            Files.write(filePath, updatedLines);
        } catch (IOException e) {
            throw new TaskRepositoryException(e.getMessage());
        }
    }

    // "Update" means "put a new task in the same location" to preserve the old Task ID.
    @Override
    public void update(int id, Task newTask) {
        if (!Files.exists(filePath)) {
            throw new EmptyRepositoryException("Operation not allowed in an empty repository.");
        }
        try {
            // Read all lines from the file
            List<String> lines = Files.readAllLines(filePath);

            // Update the specific line
            if (id > 0 && id <= lines.size()) {
                lines.set(id - 1, newTask.toJson());
            } else {
                throw new TaskNotFoundException("Task with ID " + id + " not found.");
            }

            // Write the updated lines back to the file
            Files.write(filePath, lines);
        } catch (IOException e) {
            throw new TaskRepositoryException(e.getMessage());
        }
    }
}

