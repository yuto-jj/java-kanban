package server;

import com.google.gson.Gson;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TaskHandlerTest {

    String partOfURL = "http://localhost:8080/tasks/";
    Gson gson = HttpTaskServer.getGson();
    TaskManager manager = Managers.getDefault();
    HttpTaskServer server = new HttpTaskServer(manager);

    public TaskHandlerTest() throws IOException {
    }

    @BeforeEach
    void beforeEach() {
        server.start();
    }

    @AfterEach
    void afterEach() {
        server.stop();
    }
    @Test
    void postTask() throws IOException, InterruptedException {
        Task task = new Task("Задача", "Описание", LocalDateTime.of(2024, 9,
                10, 11, 30), 90);
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(partOfURL);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager);
        assertEquals(1, tasksFromManager.size());

        Task taskFromManager = tasksFromManager.getFirst();

        assertEquals(1, taskFromManager.getId());
        assertEquals(task.getName(), taskFromManager.getName());
        assertEquals(task.getDescription(), taskFromManager.getDescription());
        assertEquals(Status.NEW, taskFromManager.getStatus());
        assertEquals(task.getStartTime(), taskFromManager.getStartTime());
        assertEquals(task.getDuration(), taskFromManager.getDuration());

        Task newTask = new Task(taskFromManager.getId(), "Обновленная задача","Описание", Status.DONE,
                LocalDateTime.of(2024, 9, 10, 11, 30), 90);
        String newTaskJson = gson.toJson(newTask);

        URI newUri = URI.create(partOfURL + taskFromManager.getId());
        HttpRequest newRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(newTaskJson))
                .uri(newUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> newResponse = client.send(newRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, newResponse.statusCode());

        List<Task> newTasksFromManager = manager.getTasks();
        Task newTaskFromManager = newTasksFromManager.getFirst();

        assertEquals(1, newTaskFromManager.getId());
        assertEquals(newTask.getName(), newTaskFromManager.getName());
        assertEquals(newTask.getDescription(), newTaskFromManager.getDescription());
        assertEquals(newTask.getStatus(), newTaskFromManager.getStatus());
        assertEquals(newTask.getStartTime(),
                newTaskFromManager.getStartTime());
        assertEquals(newTask.getDuration(), newTaskFromManager.getDuration());
    }
}
