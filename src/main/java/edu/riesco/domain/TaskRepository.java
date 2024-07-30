package edu.riesco.domain;

import java.util.List;


// The Tasks ID will be the Task position in the repository.
public interface TaskRepository {

    // Append at the end.
    int create(Task task);

    List<Task> getAll();

    Task getById(int id);

    // Create a new Task (Tasks are immutable objects) and save it in the same position to preserve the old ID.
    void update(int id, Task newTask);

    void delete(int id);
}
