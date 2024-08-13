package edu.riesco.domain;

import java.time.LocalDate;

public class DueDate implements TaskDueDate {
    private final LocalDate dueDate;

    public DueDate(int year, int month, int day) {
        this.dueDate = LocalDate.of(year, month, day);
    }

    static public DueDate of(int year, int month, int day) {
        return new DueDate(year, month, day);
    }

    static public DueDate of(String date) {
        LocalDate parsedDueDate = LocalDate.parse(date);
        return new DueDate(parsedDueDate.getYear(), parsedDueDate.getMonthValue(), parsedDueDate.getDayOfMonth());
    }

    @Override
    public String printableDueDate() {
        return dueDate.toString();
    }

    @Override
    public boolean isOverdue() {
        return dueDate.isBefore(LocalDate.now());
    }
}
