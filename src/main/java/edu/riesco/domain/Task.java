package edu.riesco.domain;

import com.google.gson.*;
import edu.riesco.exception.ModelException;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Task {
    public static final String TITLE_CAN_NOT_BE_BLANK = "Title can not be blank";
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
            .create();
    private String title;
    private String description;
    private LocalDate dueDate;
    private boolean isPending = true;

    private Task(String title, String description, LocalDate dueDate) {
        this.title = title;
        this.description = description == null ? "" : description;
        this.dueDate = dueDate;  //TODO: Improve with a Null Object Pattern.
    }

    public static Task create(String title, String description, LocalDate dueDate) {
        assertTitleIsNotBlank(title);
        return new Task(title, description, dueDate);
    }

    private static void assertTitleIsNotBlank(String title) {
        if (title == null || title.isBlank()) throw new ModelException(TITLE_CAN_NOT_BE_BLANK);
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

    public boolean isPending() {
        return isPending;
    }

    public void markAsComplete() {
        isPending = false;
    }

    public void markAsPending() {
        isPending = true;
    }

    public void update(String title, String description, LocalDate dueDate) {
        if (title != null) this.title = title;
        if (description != null) this.description = description;
        if (dueDate != null) this.dueDate = dueDate;
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