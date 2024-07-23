package edu.riesco.domain;

import edu.riesco.exception.ModelException;
import edu.riesco.exception.TaskNotFound;
import edu.riesco.persistence.MemoryTaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {

    private static final String A_TITLE = "aTitle";
    private static final String A_DESCRIPTION = "aDescription";
    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate TOMORROW = LocalDate.now().plusDays(1);
    private static final String ANOTHER_TITLE = "Another title";
    private static final String ANOTHER_DESCRIPTION = "Another description";

    private TaskManager taskManager;


    @BeforeEach
    void setup() {
        taskManager = new TaskManager(new MemoryTaskRepository());
    }

    @Test
    @DisplayName("New TaskManager has no tasks.")
    void hasTasks() {
        assertFalse(taskManager.hasTasks());
    }

    @Test
    @DisplayName("Users should be able to create a new task by providing a task title, description, and a due date.")
    void createTask() {
        int id = taskManager.addTask(A_TITLE, A_DESCRIPTION, TOMORROW);

        assertTrue(taskManager.hasTask(id));
        assertTrue(taskManager.hasTasks());
    }

    @Test
    @DisplayName("Tasks can not have a blank title")
    void taskWithNoBlankTitle() {
        assertThrows(ModelException.class, () -> {
            taskManager.addTask("", A_DESCRIPTION, TOMORROW);
        });
        assertFalse(taskManager.hasTasks());
    }

    @Test
    @DisplayName("Tasks can be created with a null description")
    void taskWithNullDescription() {
        int id = taskManager.addTask(A_TITLE, null, TOMORROW);

        assertEquals(A_TITLE, taskManager.getTaskTitle(id));
        assertEquals("", taskManager.getTaskDescription(id));
    }

    @Test
    @DisplayName("Tasks can be created with a null due date")
    void taskWithNullDueDate() {
        int id = taskManager.addTask(A_TITLE, A_DESCRIPTION, null);

        assertEquals(A_TITLE, taskManager.getTaskTitle(id));
        assertEquals("", taskManager.getTaskDueDate(id));
    }

    // Status
    @Test
    @DisplayName("New task status is 'pending'")
    void newTaskStatus() {
        int id = taskManager.addTask(A_TITLE, A_DESCRIPTION, TOMORROW);

        assertTrue(taskManager.isPending(id));
    }

    @Test
    @DisplayName("Tasks can be set as 'completed'")
    void markAsCompleteTask() {
        int id = taskManager.addTask(A_TITLE, A_DESCRIPTION, TOMORROW);
        taskManager.markAsComplete(id);

        assertFalse(taskManager.isPending(id));
    }

    @Test
    @DisplayName("A task searched by ID can not be set as 'completed' if is not found")
    void NotFoundSetTaskAsCompleted() {
        assertFalse(taskManager.hasTask(1));
        assertThrows(TaskNotFound.class, () -> {
            taskManager.markAsComplete(1);
        });
    }

    @Test
    @DisplayName("Tasks can be set as 'pending'")
    void setTaskAsPending() {
        int id = taskManager.addTask(A_TITLE, A_DESCRIPTION, TOMORROW);
        taskManager.markAsComplete(id);
        taskManager.markAsPending(id);

        assertTrue(taskManager.isPending(id));
    }

    @Test
    @DisplayName("A task searched by ID can not be set as 'pending' if is not found")
    void NotFoundSetTaskAsPending() {
        assertFalse(taskManager.hasTask(1));
        assertThrows(TaskNotFound.class, () -> {
            taskManager.markAsPending(1);
        });
    }

    // List
    @Test
    @DisplayName("List tasks")
    void listTasks() {
        taskManager.addTask(A_TITLE, A_DESCRIPTION, TODAY);
        taskManager.addTask(ANOTHER_TITLE, ANOTHER_DESCRIPTION, TOMORROW);
        List<Task> tasks = taskManager.tasks();

        assertEquals(2, tasks.size());
    }

    // ID
    @Test
    @DisplayName("Tasks have consecutive integers as IDs starting in 1")
    void tasksIds() {
        taskManager.addTask(A_TITLE, A_DESCRIPTION, TODAY);
        taskManager.addTask(ANOTHER_TITLE, ANOTHER_DESCRIPTION, TOMORROW);
        List<Task> tasks = taskManager.tasks();

        assertEquals(1, tasks.getFirst().getId());
        assertEquals(2, tasks.getLast().getId());
    }

    // Update
    @Test
    @DisplayName("A task searched by ID can be updated")
    void updateTask() {
        int id = taskManager.addTask(A_TITLE, A_DESCRIPTION, TODAY);
        taskManager.updateTask(id, ANOTHER_TITLE, ANOTHER_DESCRIPTION, TOMORROW);

        assertEquals(ANOTHER_TITLE, taskManager.getTaskTitle(id));
        assertEquals(ANOTHER_DESCRIPTION, taskManager.getTaskDescription(id));
        assertEquals(TOMORROW.toString(), taskManager.getTaskDueDate(id));
    }

    @Test
    @DisplayName("A task searched by ID can not be updated if is not found")
    void NotFoundUpdateTask() {
        assertFalse(taskManager.hasTask(1));
        assertThrows(TaskNotFound.class, () -> {
            taskManager.updateTask(1, ANOTHER_TITLE, ANOTHER_DESCRIPTION, TOMORROW);
        });
    }

    // Delete
    @Test
    @DisplayName("A task searched by ID can be deleted")
    void DeleteTask() {
        int id = taskManager.addTask(A_TITLE, A_DESCRIPTION, TODAY);
        taskManager.deleteTask(id);

        assertFalse(taskManager.hasTask(id));
    }

    @Test
    @DisplayName("A task searched by ID can not be deleted if is not found")
    void NotFoundDeleteTask() {
        assertFalse(taskManager.hasTask(1));
        assertThrows(TaskNotFound.class, () -> {
            taskManager.deleteTask(1);
        });
    }

}