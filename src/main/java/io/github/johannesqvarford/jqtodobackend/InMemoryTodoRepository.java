package io.github.johannesqvarford.jqtodobackend;

import io.github.johannesqvarford.jqtodobackend.models.CreatedTodo;
import io.github.johannesqvarford.jqtodobackend.models.Todo;
import io.github.johannesqvarford.jqtodobackend.models.TodoChanges;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.net.URI;
import java.util.*;

@Repository
public class InMemoryTodoRepository implements TodoRepository {
    // In a real application, this class would be thread-safe
    // but a real application would also use a real database.
    private final List<CreatedTodo> todos = new ArrayList<>();

    private final Map<UUID, CreatedTodo> idsToTodos = new HashMap<>();

    private final String baseUrl;

    public InMemoryTodoRepository(@Value("${todo.baseUrl}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public List<CreatedTodo> readAll() {
        return todos;
    }

    @Override
    public CreatedTodo create(Todo todo) {
        UUID id = UUID.randomUUID();
        CreatedTodo todoWithId = new CreatedTodo(todo, URI.create(String.format("%s/%s", baseUrl, id)));
        todos.add(todoWithId);
        idsToTodos.put(id, todoWithId);
        return todoWithId;
    }

    @Override
    public void deleteAll() {
        todos.clear();
    }

    @Override
    public Optional<CreatedTodo> findById(UUID id) {
        return Optional.ofNullable(idsToTodos.get(id));
    }

    @Override
    public Optional<CreatedTodo> update(UUID id, TodoChanges changes) {
        Optional<CreatedTodo> toBeUpdated = findById(id);
        toBeUpdated.ifPresent(todo -> {
            changes.getTitle().ifPresent(todo::setTitle);
            changes.getCompleted().ifPresent(todo::setCompleted);
            changes.getOrder().ifPresent(todo::setOrder);
        });
        return toBeUpdated;
    }

    @Override
    public void delete(UUID id) {
        findById(id)
                .ifPresent(todo -> {
                    todos.remove(todo);
                    idsToTodos.remove(id);
                });
    }
}
