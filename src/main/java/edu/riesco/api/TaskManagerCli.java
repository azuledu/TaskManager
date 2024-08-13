package edu.riesco.api;

import com.google.gson.Gson;
import edu.riesco.domain.DueDate;
import edu.riesco.domain.TaskManager;
import edu.riesco.persistence.JsonFileTaskRepository;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

class TaskManagerCli {

    static final String BOLD = "\033[1m";
    static final String NORMAL = "\033[0m";
    static final String GREEN = "\u001B[32m";
    static final String YELLOW = "\u001B[33m";
    static final String PURPLE = "\u001B[35m";
    public static String tasksFile = System.getProperty("user.home") + File.separator + ".tm";
    public static TaskManager taskManager = new TaskManager(new JsonFileTaskRepository(tasksFile));
    static Gson gson = new Gson();

    public static void main(String[] args) {

        CommandLine cmd = new CommandLine(new ParentCommand(taskManager));

        if (args.length == 0) {
            cmd.usage(System.out);
        } else {
            int exitCode = cmd.execute(args);
            System.exit(exitCode);
        }
    }

    static String printTasks(List<String> tasks) {
        String out = BOLD + "  ID   Due Date    Status     Title and Description" + NORMAL + "\n" + " ---------------------------------------------------";
        int taskId = 1;
        for (String task : tasks) {
            Map printableTask = gson.fromJson(task, Map.class);
            String status = (String) printableTask.get("status");
            String printableStatus = status.equalsIgnoreCase("COMPLETED") ? GREEN + "COMPLETED" + NORMAL : status + " ";
            String printableDueDate;
            if (printableTask.get("dueDate") == null) {
                printableDueDate = "";
            } else if (LocalDate.parse(printableTask.get("dueDate").toString()).isAfter(LocalDate.now())) {
                printableDueDate = printableTask.get("dueDate").toString();
            } else {
                printableDueDate = YELLOW + printableTask.get("dueDate").toString() + NORMAL;
            }

            out = String.join("\n", out, String.format(PURPLE + "%4s  " + NORMAL, taskId));
            out = String.join(" ", out, String.format("%10s  ", printableDueDate));
            out = String.join(" ", out, String.format("%9s   ", printableStatus));
            out = String.join(" ", out, BOLD + printableTask.get("title") + NORMAL);
            if (!printableTask.get("description").toString().isBlank()) {
                out = String.join(" - ", out, (String) printableTask.get("description"));
            }
            taskId++;
        }
        return out;
    }

    @Command(name = "tm", subcommands = {AddCommand.class, UpdateCommand.class, CompleteCommand.class, PendingCommand.class,
            DeleteCommand.class, ListCommand.class, CommandLine.HelpCommand.class}, description = "Task manager")
    public static class ParentCommand implements Runnable {

        ParentCommand(TaskManager taskManager) {
            TaskManagerCli.taskManager = taskManager;
        }

        @Override
        public void run() {
        }
    }

    @Command(name = "add", description = "Add Task")
    static class AddCommand implements Runnable {

        @Option(names = {"-d", "--description"}, description = "Task description")
        private String description;
        @Option(names = {"--due"}, description = "Due date")
        private String dueDate;
        @Parameters(paramLabel = "title", description = "Task title")
        private String title;

        @Override
        public void run() {
            try {
                DueDate parsedDueDate;
                if (dueDate != null) {
                    parsedDueDate = DueDate.of(dueDate);
                } else {
                    parsedDueDate = null;
                }
                int id = taskManager.addTask(title, description, parsedDueDate);
                System.out.println("Task " + id + " created");
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date: " + dueDate);
                System.out.println("Date format should be: YYYY-MM-DD");
            }
        }
    }

    //@formatter:off
    @Command(name = "update", description = "Update Task", sortOptions = false, sortSynopsis = false)
    static class UpdateCommand implements Runnable {

        @Option(names = {"-d", "--description"}, description = "Task description")
        String description;
        @Option(names = {"-t", "--title"}, description = "Task title")
        private String title;
        @Option(names = {"--due"}, description = "Due date")
        private String dueDate;
        @Parameters(paramLabel = "id", description = "Task ID")
        private int id;

        @Override
        public void run() {
            if (title != null) taskManager.updateTaskTitle(id, title);
            if (description != null) taskManager.updateTaskDescription(id, description);
            if (dueDate != null) {
                if (dueDate.isEmpty()) {  // Remove DueDate from Task
                    taskManager.updateTaskDueDate(id, null);
                } else {
                    taskManager.updateTaskDueDate(id, DueDate.of(dueDate));
                }
            }
            System.out.println("Task " + id + " updated");
        }
    }

    @Command(name = "complete", description = "Mark task as 'Completed'")
    static class CompleteCommand implements Runnable {

        @Parameters(paramLabel = "id", description = "Task ID")
        private int id;

        @Override
        public void run() {
            taskManager.markAsComplete(id);
            System.out.println("Task " + id + " completed");
        }
    }

    @Command(name = "pending", description = "Mark task as 'Pending'")
    static class PendingCommand implements Runnable {

        @Parameters(paramLabel = "id", description = "Task ID")
        private int id;

        @Override
        public void run() {
            taskManager.markAsPending(id);
            System.out.println("Task " + id + " pending");
        }
    }

    @Command(name = "delete", description = "Delete Task")
    static class DeleteCommand implements Runnable {

        @Parameters(paramLabel = "id", description = "Task ID")
        private int id;

        @Override
        public void run() {
            taskManager.deleteTask(id);
            System.out.println("Task " + id + " deleted");
        }
    }

    @Command(name = "list", description = "List Tasks")
    static class ListCommand implements Runnable {

        @Override
        public void run() {
            System.out.println(TaskManagerCli.printTasks(taskManager.tasksAsJson()));
        }
    }
}