package edu.riesco.api;

import edu.riesco.domain.TaskManager;
import edu.riesco.persistence.MemoryTaskRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskManagerCliTest {

    private static final String TASK_ID = "1";
    private static final String TASK_TITLE = "taskTitle";
    private static final String TASK_DESCRIPTION = "taskDescription";
    private static final String TODAY = LocalDate.now().toString();
    private static final String TOMORROW = LocalDate.now().plusDays(1).toString();//.format(formatter);
    private static final String ANOTHER_TITLE = "Another title";
    private static final String ANOTHER_DESCRIPTION = "Another description";

    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    CommandLine cmd;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setup() {
        System.setOut(new PrintStream(outputStreamCaptor));
        cmd = new CommandLine(new TaskManagerCli.ParentCommand(new TaskManager(new MemoryTaskRepository())));

        //String filePath = tempDir.resolve("tmTestFile.json").toString();
        //cmd = new CommandLine(new TaskManagerCli.ParentCommand(new TaskManager(new JsonFileTaskRepository(filePath))));

    }

    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
    }

    @Test
    @DisplayName("CLI can add task with title.")
    void addTaskWithTitle() {
        final String[] args = {"add", TASK_TITLE};
        cmd.execute(args);

        assertEquals("Task " + TASK_ID + " created", outputStreamCaptor.toString().trim());
        Assertions.assertTrue(TaskManagerCli.taskManager.hasTasks());
        Assertions.assertEquals(TASK_TITLE, TaskManagerCli.taskManager.tasks().getFirst().getTitle());
        Assertions.assertEquals("", TaskManagerCli.taskManager.tasks().getFirst().getDescription());
    }

    @Test
    @DisplayName("CLI can add task with title, description an due date.")
    void addTaskWithTitleAndDescription() {
        final String[] args = {"add", TASK_TITLE, "-d", TASK_DESCRIPTION, "-t", TODAY};
        cmd.execute(args);

        assertEquals("Task " + TASK_ID + " created", outputStreamCaptor.toString().trim());
        Assertions.assertTrue(TaskManagerCli.taskManager.hasTasks());
        Assertions.assertEquals(TASK_TITLE, TaskManagerCli.taskManager.tasks().getFirst().getTitle());
        Assertions.assertEquals(TASK_DESCRIPTION, TaskManagerCli.taskManager.tasks().getFirst().getDescription());
        Assertions.assertEquals(TODAY, TaskManagerCli.taskManager.tasks().getFirst().getDueDate().toString());
    }

    @Test
    @DisplayName("CLI can update a task.")
    void updateTask() {
        final String[] args = {"add", TASK_TITLE, "-d", TASK_DESCRIPTION};
        cmd.execute(args);
        final String[] args2 = {"update", TASK_ID, ANOTHER_TITLE, "-d", ANOTHER_DESCRIPTION, "-t", TOMORROW};
        cmd.execute(args2);

        String consoleOutput = "Task " + TASK_ID + " created" + "\n" + "Task " + TASK_ID + " updated";
        assertEquals(consoleOutput, outputStreamCaptor.toString().trim());
        Assertions.assertTrue(TaskManagerCli.taskManager.hasTasks());
        Assertions.assertEquals(ANOTHER_TITLE, TaskManagerCli.taskManager.tasks().getFirst().getTitle());
        Assertions.assertEquals(ANOTHER_DESCRIPTION, TaskManagerCli.taskManager.tasks().getFirst().getDescription());
        Assertions.assertEquals(TOMORROW, TaskManagerCli.taskManager.tasks().getFirst().getDueDate().toString());
    }

    @Test
    @DisplayName("CLI can complete a task.")
    void markTaskAsCompleted() {
        final String[] args = {"add", TASK_TITLE, "-d", TASK_DESCRIPTION};
        cmd.execute(args);
        final String[] args2 = {"complete", TASK_ID};
        cmd.execute(args2);

        String consoleOutput = "Task " + TASK_ID + " created" + "\n" + "Task " + TASK_ID + " completed";
        assertEquals(consoleOutput, outputStreamCaptor.toString().trim());
        Assertions.assertFalse(TaskManagerCli.taskManager.tasks().getFirst().isPending());
    }

    @Test
    @DisplayName("CLI can mark a task as pending.")
    void markTaskAsPending() {
        final String[] args = {"add", TASK_TITLE, "-d", TASK_DESCRIPTION};
        cmd.execute(args);
        final String[] args2 = {"complete", TASK_ID};
        cmd.execute(args2);
        final String[] args3 = {"pending", TASK_ID};
        cmd.execute(args3);

        String consoleOutput = "Task " + TASK_ID + " created" + "\n"
                + "Task " + TASK_ID + " completed" + "\n"
                + "Task " + TASK_ID + " pending";
        assertEquals(consoleOutput, outputStreamCaptor.toString().trim());
        Assertions.assertTrue(TaskManagerCli.taskManager.tasks().getFirst().isPending());
    }

    @Test
    @DisplayName("CLI can delete tasks.")
    void deleteTasks() {
        final String[] args = {"add", TASK_TITLE, "-d", TASK_DESCRIPTION};
        cmd.execute(args);
        final String[] args2 = {"delete", TASK_ID};
        cmd.execute(args2);

        String consoleOutput = "Task " + TASK_ID + " created" + "\n" + "Task " + TASK_ID + " deleted";
        assertEquals(consoleOutput, outputStreamCaptor.toString().trim());
        Assertions.assertFalse(TaskManagerCli.taskManager.hasTasks());
    }
}
