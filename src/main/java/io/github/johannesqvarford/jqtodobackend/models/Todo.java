package io.github.johannesqvarford.jqtodobackend.models;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Objects;
import java.util.Optional;

public class Todo {
    private String title;

    private Integer order;

    private boolean completed;

    @JsonCreator
    public Todo(String title, Integer order) {
        this(title, order, false);
    }

    public Todo(String title, Integer order, boolean completed) {
        this.title = Objects.requireNonNull(title);
        this.order = order;
        this.completed = completed;
    }

    public Todo(Todo toCreateCopyFrom) {
        this(toCreateCopyFrom.title, toCreateCopyFrom.order, toCreateCopyFrom.completed);
    }

    public Todo(String title) {
        this(title, null);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Optional<Integer> getOrder() {
        return Optional.ofNullable(order);
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}
