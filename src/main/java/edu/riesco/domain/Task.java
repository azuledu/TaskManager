package edu.riesco.domain;

import com.google.gson.*;
import edu.riesco.exception.ModelException;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public final class Task {
    public static final String TITLE_CAN_NOT_BE_BLANK = "Title can not be blank";
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter()).create();
    // Read-only object.
    private final String title;
    private final String description;
    private final LocalDate dueDate;
    private final TaskStatus status;

    private Task(String title, String description, LocalDate dueDate, TaskStatus status) {
        this.title = title;
        this.description = description == null ? "" : description;
        this.dueDate = dueDate;  //TODO: Maybe, improve with a Null Object Pattern.
        this.status = status;
    }

    private static void assertTitleIsNotBlank(String title) {
        if (title == null || title.isBlank()) throw new ModelException(TITLE_CAN_NOT_BE_BLANK);
    }

    public static Task from(String title, String description, LocalDate dueDate) {
        assertTitleIsNotBlank(title);
        return new Task(title, description, dueDate, TaskStatus.PENDING);
    }

    public static Task from(String title, String description, LocalDate dueDate, TaskStatus status) {
        assertTitleIsNotBlank(title);
        return new Task(title, description, dueDate, status);
    }

    public static Task fromJson(String json) {
        try {
            Task task = gson.fromJson(json, Task.class);
            assertTitleIsNotBlank(task.title);
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

    public LocalDate getDueDate() {
        return dueDate;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public Task withStatus(TaskStatus newStatus) {
        return Task.from(title, description, dueDate, newStatus);
    }

    public Task withTitle(String newTitle) {
        return Task.from(newTitle, description, dueDate, status);
    }

    public Task withDescription(String newDescription) {
        return Task.from(title, newDescription, dueDate, status);
    }

    public Task withDueDate(LocalDate newDueDate) {
        return Task.from(title, description, newDueDate, status);
    }
}

class LocalDateTypeAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public JsonElement serialize(final LocalDate date, final Type typeOfSrc,
                                 final JsonSerializationContext context) {
        return new JsonPrimitive(date.format(formatter));
    }

    @Override
    public LocalDate deserialize(final JsonElement json, final Type typeOfT,
                                 final JsonDeserializationContext context) throws JsonParseException {
        return LocalDate.parse(json.getAsString(), formatter);
    }
}