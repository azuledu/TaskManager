package edu.riesco.domain;

import edu.riesco.exception.ModelException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TaskTest {

    private static final String A_TITLE = "aTitle";
    private static final String A_DESCRIPTION = "aDescription";
    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate TOMORROW = LocalDate.now().plusDays(1);
    private static final String ANOTHER_TITLE = "Another title";
    private static final String ANOTHER_DESCRIPTION = "Another description";

    private Task task;


    @BeforeEach
    void setup() {
        task = Task.from(A_TITLE, A_DESCRIPTION, TOMORROW);
    }

    @Test
    @DisplayName("Can create a new task by providing a task title, description, and a due date")
    void fromTask() {
        assertEquals(A_TITLE, task.getTitle());
        assertEquals(A_DESCRIPTION, task.getDescription());
        assertEquals(TOMORROW, task.getDueDate());
    }

    @Test
    @DisplayName("New task can not have a blank title")
    void taskWithNoBlankTitle() {
        assertThrows(ModelException.class, () -> {
            Task.from("", A_DESCRIPTION, TOMORROW);
        });
    }

    // Status
    @Test
    @DisplayName("The status for a new task is 'pending'")
    void newTaskStatus() {
        assertEquals(TaskStatus.PENDING, task.getStatus());
    }

    @Test
    @DisplayName("Tasks can be set as 'completed'")
    void markAsComplete() {
        Task newTask = task.withStatus(TaskStatus.COMPLETED); // Tasks are immutable.

        assertEquals(TaskStatus.COMPLETED, newTask.getStatus());
    }

    @Test
    @DisplayName("Tasks can be set as 'pending'")
    void markTaskAsPending() {
        task = Task.from(A_TITLE, A_DESCRIPTION, TOMORROW, TaskStatus.COMPLETED);
        Task newTask = task.withStatus(TaskStatus.PENDING); // Tasks are immutable.

        assertEquals(TaskStatus.PENDING, newTask.getStatus());
    }

    // Update
    @Test
    @DisplayName("Tasks can be updated")
    void updateTask() {
        Task newTask = task.withTitle(ANOTHER_TITLE).withDescription(ANOTHER_DESCRIPTION).withDueDate(TODAY);

        assertEquals(ANOTHER_TITLE, newTask.getTitle());
        assertEquals(ANOTHER_DESCRIPTION, newTask.getDescription());
        assertEquals(TODAY, newTask.getDueDate());
    }
}
