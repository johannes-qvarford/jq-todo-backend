package io.github.johannesqvarford.jqtodobackend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.github.johannesqvarford.jqtodobackend.models.Todo;
import io.github.johannesqvarford.jqtodobackend.models.TodoChanges;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.net.URI;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

/**
 * Software development is all about trade-offs, so let me explain a few that I've made for this task.
 * Most applications are never truly done, so I've added a few extension points where I expect that
 * a real product would change with time.
 *
 * An in-memory collection is enough to fulfill the requirements that I've been given,
 * but I've abstracted the data storage so it can be replaced with a persistent option in the future since that seems
 * very likely.
 *
 * I believe that the code structure evolves to fit the application, so I don't like needless sub packaging.
 * That has severely reduced readability in a lot of projects I've worked on, so I prefer to start with little structure
 * and then divide by feature as abstractions reveal themselves.
 * By having a robust set of tests, and utilising automatic refactorings from the IDE, you shouldn't be afraid to
 * change the structure to fit the problem as the application evolves.
 *
 * The tests are loosely based on the spec: https://github.com/TodoBackend/todo-backend-js-spec/blob/master/js/specs.js
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class JqTodoBackendApplicationTests {
    @LocalServerPort
    private int port;

    static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new Jdk8Module());
    }

    @BeforeEach
    void setupRestAssured() {
        RestAssured.port = port;
    }

    @AfterEach
    void clearTodos() {
        deleteAll();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void noTodosToStartWith() {
        getAll()
                .then()
                .statusCode(200)
                .body("$.size()", equalTo(0));
    }

    @Test
    void aPostedTodoIsReturned() {
        post(new Todo("a title"))
                .then()
                .statusCode(200)
                .body("title", equalTo("a title"));
    }

    @Test
    void postedTodosAreAddedToTheList() {
        post(new Todo("a title"));
        post(new Todo("a different title"));

        getAll()
                .then()
                .body("title", hasItems("a title", "a different title"));
    }

    @Test
    void listIsEmptyAfterDeletion() {
        post(new Todo("a title"));
        post(new Todo("a different title"));

        deleteAll()
                .then()
                .statusCode(200);

        getAll()
                .then()
                .body("$.size()", equalTo(0));
    }

    @Test
    void aTodoIsInitiallyNotCompleted() {
        post(new Todo("a title"));

        getAll()
                .then()
                .body("completed", hasItems(false));
    }

    @Test
    void aCreatedTodoHasAUrlToFetchItself() {
        URI url = postAndExtractUrl(new Todo("a title"));

        get(url)
                .then()
                .statusCode(200)
                .body("title", equalTo("a title"));
    }

    @Test
    void aCreatedTodosUrlIsStoredInTheList() {
        post(new Todo("a title"));
        post(new Todo("a different title"));

        String url = getAll()
                .then()
                .body("$.size()", equalTo(2))
                .extract()
                .body()
                .path("[0].url");

        get(url)
                .then()
                .statusCode(200)
                .body("title", equalTo("a title"));
    }

    @Test
    void aTodoCanBePatchedToChangeItsTitle() {
        URI url = postAndExtractUrl(new Todo("a title"));

        patch(url, TodoChanges.builder().title("a different title").build())
                .then()
                .statusCode(200);

        get(url)
                .then()
                .body("title", equalTo("a different title"));
    }

    @Test
    void aTodoCanBePatchedToChangeItsCompleteness() {
        URI url = postAndExtractUrl(new Todo("a title"));

        patch(url, TodoChanges.builder().completed(true).build())
                .then()
                .statusCode(200);

        get(url)
                .then()
                .body("completed", equalTo(true));
    }

    @Test
    void changesToTodosArePropagatedToTheList() {
        URI url = postAndExtractUrl(new Todo("a title", 1234));

        patch(url, TodoChanges.builder().title("a different title").completed(true).order(2345).build())
                .then()
                .statusCode(200);

        getAll()
                .then()
                .body("[0].title", equalTo("a different title"))
                .body("[0].completed", equalTo(true))
                .body("[0].order", equalTo(2345));
    }

    @Test
    void aTodoCanBeRemovedByDeletingIt() {
        URI url = postAndExtractUrl(new Todo("a title"));

        delete(url)
                .then()
                .statusCode(200);

        get(url)
                .then()
                .statusCode(404);
        getAll()
                .then()
                .body("$.size()", equalTo(0));
    }

    @Test
    void aTodoCanHaveAnInitialOrder() {
        URI url = postAndExtractUrl(new Todo("a title", 1234));

        get(url)
                .then()
                .body("order", equalTo(1234));
    }

    @Test
    void aTodoCanBePatchedToChangeItsOrder() {
        URI url = postAndExtractUrl(new Todo("a title", 1234));

        patch(url, TodoChanges.builder().order(2345).build())
                .then()
                .statusCode(200);

        get(url)
                .then()
                .body("order", equalTo(2345));
    }

    private Response getAll() {
        return get("/");
    }

    private static Response post(Todo todo) {
        try {
            return given()
                    .contentType(ContentType.JSON)
                    .body(objectMapper.writeValueAsString(todo))
                    .when()
                    .post("/");
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize todo item", e);
        }
    }

    private static Response patch(URI url, TodoChanges changes) {
        try {
            return given()
                    .contentType(ContentType.JSON)
                    .body(objectMapper.writeValueAsString(changes))
                    .when()
                    .patch(url);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize todo item", e);
        }
    }

    private static URI postAndExtractUrl(Todo todo) {
        String string = post(todo)
                .then()
                .statusCode(200)
                .extract()
                .body()
                .path("url");
        return URI.create(string);
    }

    private static Response deleteAll() {
        return delete("/");
    }
}
