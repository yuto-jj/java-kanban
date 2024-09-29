package server;

import com.google.gson.*;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void getTask() throws IOException, InterruptedException {
        Task task1 = new Task("Задача", "Описание", LocalDateTime.of(2024, 9,
                10, 11, 30), 90);
        Task task2 = new Task("Задача", "Описание", LocalDateTime.of(2024, 9,
                11, 11, 30), 90);
        Task task3 = new Task("Задача", "Описание", LocalDateTime.of(2024, 9,
                12, 11, 30), 90);

        manager.addTask(task1);
        int id2 = manager.addTask(task2);
        manager.addTask(task3);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(partOfURL);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        JsonElement jsonElem = JsonParser.parseString(response.body());
        assertTrue(jsonElem.isJsonArray());
        JsonArray jsonArray = jsonElem.getAsJsonArray();

        List<Task> tasks = new ArrayList<>();
        for (JsonElement jsonElement : jsonArray) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Task task = gson.fromJson(jsonObject, Task.class);
            tasks.add(task);
        }

        List<Task> tasksFromManager = manager.getTasks();

        assertEquals(tasks.size(), tasksFromManager.size());
        assertEquals(tasks, tasksFromManager);
        assertEquals(task2.getName(), tasksFromManager.get(1).getName());
        assertEquals(task2.getDescription(), tasksFromManager.get(1).getDescription());
        assertEquals(task2.getStatus(), tasksFromManager.get(1).getStatus());
        assertEquals(task2.getStartTime(), tasksFromManager.get(1).getStartTime());
        assertEquals(task2.getDuration(), tasksFromManager.get(1).getDuration());

        URI newUri = URI.create(partOfURL + id2);
        HttpRequest newRequest = HttpRequest.newBuilder()
                .GET()
                .uri(newUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> newResponse = client.send(newRequest, HttpResponse.BodyHandlers.ofString());

        JsonElement newjsonElem = JsonParser.parseString(newResponse.body());
        assertTrue(newjsonElem.isJsonObject());
        JsonObject jsonObject = newjsonElem.getAsJsonObject();
        Task newTask = gson.fromJson(jsonObject, Task.class);

        assertEquals(task2.getName(), newTask.getName());
        assertEquals(task2.getDescription(), newTask.getDescription());
        assertEquals(task2.getStatus(), newTask.getStatus());
        assertEquals(task2.getStartTime(), newTask.getStartTime());
        assertEquals(task2.getDuration(), newTask.getDuration());
    }

    @Test
    void deleteTask() throws IOException, InterruptedException {
        Task task1 = new Task("Задача", "Описание", LocalDateTime.of(2024, 9,
                10, 11, 30), 90);
        Task task2 = new Task("Задача", "Описание", LocalDateTime.of(2024, 9,
                11, 11, 30), 90);

        int id1 = manager.addTask(task1);
        manager.addTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(partOfURL + id1);
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();
        Task task = tasksFromManager.getFirst();

        assertEquals(1, tasksFromManager.size());
        assertEquals(task2, task);
        assertEquals(task2.getName(), task.getName());
        assertEquals(task2.getDescription(), task.getDescription());
        assertEquals(task2.getStatus(), task.getStatus());
        assertEquals(task2.getStartTime(), task.getStartTime());
        assertEquals(task2.getDuration(), task.getDuration());
    }
}
