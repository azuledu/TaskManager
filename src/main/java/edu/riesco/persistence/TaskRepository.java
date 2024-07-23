package edu.riesco.persistence;

import edu.riesco.domain.Task;

import java.util.List;


public interface TaskRepository {

    boolean hasTasks();

    void addTask(Task task);

    Task taskById(int id);

    void deleteTask(int id);

    List<Task> tasks();
}
