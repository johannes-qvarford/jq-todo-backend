package io.github.johannesqvarford.jqtodobackend.models;

import java.net.URI;
import java.util.Objects;

public class CreatedTodo extends Todo {
    private final URI url;

    public CreatedTodo(Todo todo, URI url) {
        super(todo);
        this.url = Objects.requireNonNull(url);
    }

    public URI getUrl() {
        return url;
    }
}
