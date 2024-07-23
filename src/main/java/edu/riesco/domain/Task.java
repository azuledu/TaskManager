package edu.riesco.domain;

import com.google.gson.Gson;
import edu.riesco.exception.ModelException;

import java.time.LocalDate;

public class Task {
    public static final String TITLE_CAN_NOT_BE_BLANK = "Title can not be blank";

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

    public String toJson() {
        Gson gson = new Gson();
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

    void markAsComplete() {
        isPending = false;
    }

    void markAsPending() {
        isPending = true;
    }

    public void update(String title, String description, LocalDate dueDate) {
        if (title != null) this.title = title;
        if (description != null) this.description = description;
        if (dueDate != null) this.dueDate = dueDate;
    }
}