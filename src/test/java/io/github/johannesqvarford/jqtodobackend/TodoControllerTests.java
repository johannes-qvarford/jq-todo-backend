package io.github.johannesqvarford.jqtodobackend;

import io.github.johannesqvarford.jqtodobackend.models.CreatedTodo;
import io.github.johannesqvarford.jqtodobackend.models.Todo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * The tests in JqTodoBackendApplicationTests cover the API, which is the most important part.
 * The tests here are mostly to demonstrate knowledge about Mockito.
 */
public class TodoControllerTests {
    private static final UUID ARBITRARY_ID = UUID.randomUUID();

    private static final URI ARBITRARY_URL = URI.create(String.format("http://localhost:8080/%s", ARBITRARY_ID));

    private static final CreatedTodo ARBITRARY_CREATED_TODO = new CreatedTodo(new Todo("a title"), ARBITRARY_URL);

    private TodoController controller;

    private TodoRepository repository;

    @BeforeEach
    public void setupMocks() {
        repository = mock(TodoRepository.class);
        controller = new TodoController(repository);
    }

    @Test
    public void findByIdReturns404IfTodoDoesntExistInTheRepository() {
        when(repository.findById(any())).thenReturn(Optional.empty());

        ResponseEntity<?> actual = controller.findById(ARBITRARY_ID);

        ResponseEntity<?> expected = ResponseEntity.notFound().build();
        Assertions.assertEquals(expected.getStatusCode(), actual.getStatusCode());
        verify(repository, times(1)).findById(ARBITRARY_ID);
    }

    @Test
    public void findByIdReturns200IfTodoExistsInTheRepository() {
        when(repository.findById(any())).thenReturn(Optional.of(ARBITRARY_CREATED_TODO));

        @SuppressWarnings("unchecked")
        ResponseEntity<CreatedTodo> actual = (ResponseEntity<CreatedTodo>) controller.findById(ARBITRARY_ID);

        ResponseEntity<?> expected = ResponseEntity.ok(ARBITRARY_CREATED_TODO);
        Assertions.assertEquals(expected.getStatusCode(), actual.getStatusCode());
        Assertions.assertEquals(expected.getBody(), actual.getBody());
    }
}
