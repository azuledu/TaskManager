package edu.riesco.domain;

import edu.riesco.exception.ModelException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

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
        task = Task.create(A_TITLE, A_DESCRIPTION, TOMORROW);
    }

    @Test
    @DisplayName("Can create a new task by providing a task title, description, and a due date")
    void createTask() {
        assertEquals(A_TITLE, task.getTitle());
        assertEquals(A_DESCRIPTION, task.getDescription());
        assertEquals(TOMORROW, task.getDueDate());
    }

    @Test
    @DisplayName("New task can not have a blank title")
    void taskWithNoBlankTitle() {
        assertThrows(ModelException.class, () -> {
            Task.create("", A_DESCRIPTION, TOMORROW);
        });
    }

    // Status
    @Test
    @DisplayName("The status for a new task is 'pending'")
    void newTaskStatus() {
        assertTrue(task.isPending());
    }

    @Test
    @DisplayName("Tasks can be set as 'completed'")
    void markAsComplete() {
        task.markAsComplete();

        assertFalse(task.isPending());
    }

    @Test
    @DisplayName("Tasks can be set as 'pending'")
    void markTaskAsPending() {
        task.markAsComplete();
        task.markAsPending();

        assertTrue(task.isPending());
    }

    // Update
    @Test
    @DisplayName("Tasks can be updated")
    void updateTask() {
        task.update(ANOTHER_TITLE, ANOTHER_DESCRIPTION, TODAY);

        assertEquals(ANOTHER_TITLE, task.getTitle());
        assertEquals(ANOTHER_DESCRIPTION, task.getDescription());
        assertEquals(TODAY, task.getDueDate());
    }
}
