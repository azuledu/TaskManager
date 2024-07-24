package edu.riesco.api;

import edu.riesco.domain.Task;
import edu.riesco.domain.TaskManager;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.time.LocalDate;
import java.util.List;

class TaskManagerCli {

    public static TaskManager taskManager;

    public static void main(String[] args) {

        CommandLine cmd = new CommandLine(new ParentCommand(taskManager));

        if (args.length == 0) {
            cmd.usage(System.out);
        } else {
            int exitCode = cmd.execute(args);
            System.exit(exitCode);
        }
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
        @Option(names = {"-t", "--due"}, description = "Due date")
        private String dueDate;
        @Parameters(paramLabel = "title", description = "Task title")
        private String title;

        @Override
        public void run() {
            int id = taskManager.addTask(title, description, LocalDate.now());
            System.out.println("Task " + id + " created");
        }
    }

    @Command(name = "update", description = "Update Task")
    static class UpdateCommand implements Runnable {

        @Option(names = {"-d", "--description"}, description = "Task description")
        String description = "";
        @Option(names = {"-t", "--due"}, description = "Due date")
        private String dueDate;
        @Parameters(paramLabel = "id", description = "Task ID")
        private int id;
        @Parameters(paramLabel = "title", description = "Task title")
        private String title;

        @Override
        public void run() {
            taskManager.updateTask(id, title, description, LocalDate.now());
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
            List<Task> tasks = taskManager.tasks();
            System.out.println(tasks);
        }
    }
}