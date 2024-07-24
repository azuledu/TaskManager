package edu.riesco.persistence;

import edu.riesco.domain.Task;
import edu.riesco.domain.TaskRepository;
import edu.riesco.exception.TaskNotFoundException;
import edu.riesco.exception.TaskRepositoryException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JsonFileTaskRepository implements TaskRepository {
    private final Path filePath;

    public JsonFileTaskRepository(String filePath) {
        this.filePath = Path.of(filePath);
    }

    @Override
    public boolean hasTasks() {
        if (!Files.exists(filePath)) {
            return false;
        } else {
            try (Stream<String> fileStream = Files.lines(filePath)) {
                int numTasks = (int) fileStream.count();
                return numTasks != 0;
            } catch (IOException e) {
                throw new TaskRepositoryException(e.getMessage());
            }
        }
    }

    @Override
    public int addTask(Task task) {
        try {
            if (Files.exists(filePath)) {
                Files.write(filePath, (System.lineSeparator() + task.toJson()).getBytes(), StandardOpenOption.APPEND);
                return lastTaskId();
            } else {
                Files.write(filePath, task.toJson().getBytes());
                return 1;
            }
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
    public Task taskById(int id) {
        try (Stream<String> lines = Files.lines(filePath)) {
            String taskAsJson = lines.skip(id - 1).findFirst()
                    .orElseThrow(() -> new TaskNotFoundException("Task with id " + id + " not found."));
            return Task.fromJson(taskAsJson);
        } catch (IOException e) {
            throw new TaskNotFoundException("Task with id " + id + " not found.");
        }
    }

    @Override
    public void deleteTask(int id) {
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

    public void markAsComplete(int id) {
        Task task = taskById(id);
        task.markAsComplete();
        updateTask(id, task);
    }

    public void markAsPending(int id) {
        Task task = taskById(id);
        task.markAsPending();
        updateTask(id, task);
    }

    public void updateTask(int id, Task task) {
        try {
            // Read all lines from the file
            Path file = filePath;
            List<String> lines = Files.readAllLines(file);

            // Update the specific line
            lines.set(id - 1, task.toJson());

            // Write the updated lines back to the file
            Files.write(file, lines);
        } catch (IOException e) {
            throw new TaskRepositoryException(e.getMessage());
        }
    }

    @Override
    public void updateTask(int id, String title, String description, LocalDate dueDate) {
        Task newTask = Task.create(title, description, dueDate);
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


    @Override
    public List<Task> tasks() {
        try {
            List<String> lines = Files.readAllLines(filePath);
            return lines.stream().map(Task::fromJson).collect(Collectors.toList());
        } catch (IOException e) {
            throw new TaskRepositoryException(e.getMessage());
        }
    }
}

