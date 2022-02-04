package io.github.johannesqvarford.jqtodobackend;

import io.github.johannesqvarford.jqtodobackend.models.CreatedTodo;
import io.github.johannesqvarford.jqtodobackend.models.Todo;
import io.github.johannesqvarford.jqtodobackend.models.TodoChanges;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TodoRepository {
    List<CreatedTodo> readAll();

    CreatedTodo create(Todo todo);

    void deleteAll();

    Optional<CreatedTodo> findById(UUID id);

    Optional<CreatedTodo> update(UUID id, TodoChanges changes);

    void delete(UUID id);
}
