package edu.riesco.domain;

import com.google.gson.*;
import edu.riesco.exception.ModelException;
import edu.riesco.exception.OverdueException;

import java.lang.reflect.Type;


public final class Task {
    public static final String TITLE_CAN_NOT_BE_BLANK = "Title can not be blank";
    public static final String DATE_IS_OVERDUE = "Date is overdue";
    private static final Gson gson = new GsonBuilder()
            .registerTypeHierarchyAdapter(TaskDueDate.class, new TaskDueDateTypeAdapter()).create();
    // Read-only object.
    private final String title;
    private final String description;
    private final TaskDueDate dueDate;
    private final TaskStatus status;

    private Task(String title, String description, TaskDueDate dueDate, TaskStatus status) {
        this.title = title;
        this.description = description == null ? "" : description;
        this.dueDate = dueDate;
        this.status = status == null ? TaskStatus.PENDING : status;
    }

    private static void assertTitleIsNotBlank(String title) {
        if (title == null || title.isBlank()) throw new ModelException(TITLE_CAN_NOT_BE_BLANK);
    }

    private static void assertDateIsNotOverDue(TaskDueDate dueDate) {
        if (dueDate.isOverdue()) throw new OverdueException(DATE_IS_OVERDUE);
    }

    public static Task from(String title, String description, TaskDueDate dueDate) {
        assertTitleIsNotBlank(title);
        assertDateIsNotOverDue(dueDate);
        return new Task(title, description, dueDate, TaskStatus.PENDING);
    }

    public static Task from(String title, String description, TaskDueDate dueDate, TaskStatus status) {
        assertTitleIsNotBlank(title);
        return new Task(title, description, dueDate, status);
    }

    public static Task fromJson(String json) {
        try {
            Task task = gson.fromJson(json, Task.class);
            assertTitleIsNotBlank(task.title);
            assertDateIsNotOverDue(task.dueDate);
            return task;
        } catch (JsonParseException | NullPointerException e) {
            throw new IllegalArgumentException("Invalid JSON string", e);
        }
    }

    public String toJson() {
        return gson.toJson(this);
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPrintableDueDate() {
        if (dueDate == null) {
            return "";
        } else {
            return dueDate.printableDueDate();
        }
    }

    public TaskStatus getStatus() {
        return status;
    }

    public Task withStatus(TaskStatus newStatus) {
        return Task.from(title, description, dueDate, newStatus);
    }

    public Task withTitle(String newTitle) {
        return Task.from(newTitle, description, dueDate);
    }

    public Task withDescription(String newDescription) {
        return Task.from(title, newDescription, dueDate);
    }

    public Task withDueDate(TaskDueDate newDueDate) {
        return Task.from(title, description, newDueDate);
    }
}

class TaskDueDateTypeAdapter implements JsonSerializer<TaskDueDate>, JsonDeserializer<TaskDueDate> {

    @Override
    public JsonElement serialize(final TaskDueDate date, final Type typeOfSrc,
                                 final JsonSerializationContext context) {
        return new JsonPrimitive(date.printableDueDate());
    }

    @Override
    public TaskDueDate deserialize(final JsonElement json, final Type typeOfT,
                                   final JsonDeserializationContext context) throws JsonParseException {
        String jsonElement = json.getAsString();
        return jsonElement.isEmpty() ? new NoDueDate() : DueDate.of(jsonElement);
    }
}