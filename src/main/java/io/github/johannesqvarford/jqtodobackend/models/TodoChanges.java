package io.github.johannesqvarford.jqtodobackend.models;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Optional;

public record TodoChanges(String title, Boolean completed, Integer order) {
    @JsonCreator
    public TodoChanges {
    }

    public Optional<String> getTitle() {
        return Optional.ofNullable(title);
    }

    public Optional<Boolean> getCompleted() {
        return Optional.ofNullable(completed);
    }

    public Optional<Integer> getOrder() {
        return Optional.ofNullable(order);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String title;

        private Boolean complete;

        private Integer order;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder completed(boolean complete) {
            this.complete = complete;
            return this;
        }

        public Builder order(int order) {
            this.order = order;
            return this;
        }

        public TodoChanges build() {
            return new TodoChanges(title, complete, order);
        }
    }
}
