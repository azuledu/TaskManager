package edu.riesco.domain;

public class NoDueDate implements TaskDueDate {
    @Override
    public String printableDueDate() {
        return "";
    }

    @Override
    public boolean isOverdue() {
        return false;
    }
}
