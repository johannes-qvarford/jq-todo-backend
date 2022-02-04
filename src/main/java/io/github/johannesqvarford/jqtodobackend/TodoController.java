package io.github.johannesqvarford.jqtodobackend;

import io.github.johannesqvarford.jqtodobackend.models.CreatedTodo;
import io.github.johannesqvarford.jqtodobackend.models.Todo;
import io.github.johannesqvarford.jqtodobackend.models.TodoChanges;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class TodoController {
    private final TodoRepository repository;

    public TodoController(TodoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<CreatedTodo> readAll() {
        return repository.readAll();
    }

    @PostMapping
    public CreatedTodo create(@RequestBody Todo todo) {
        return repository.create(todo);
    }

    @DeleteMapping
    public void deleteAll() {
        repository.deleteAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<?> findById(@PathVariable UUID id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("{id}")
    public ResponseEntity<CreatedTodo> update(@PathVariable UUID id, @RequestBody TodoChanges changes) {
        return repository.update(id, changes)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable UUID id) {
        repository.delete(id);
    }
}
