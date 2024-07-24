package edu.riesco.domain;

import java.time.LocalDate;
import java.util.List;


public interface TaskRepository {

    boolean hasTasks();

    int addTask(Task task);

    List<Task> tasks();

    List<String> tasksAsJson();

    Task taskById(int id);
    
    void markAsComplete(int id);

    void markAsPending(int id);


    void updateTask(int id, String title, String description, LocalDate dueDate);

    void deleteTask(int id);
}
