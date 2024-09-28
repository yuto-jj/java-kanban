package server;

import com.google.gson.*;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtaskHandlerTest {
    String partOfURL = "http://localhost:8080/subtasks/";
    Gson gson = HttpTaskServer.getGson();
    TaskManager manager = Managers.getDefault();
    HttpTaskServer server = new HttpTaskServer(manager);
    Epic epic = new Epic("Эпик - 1", "Описание");
    int eId = manager.addEpic(epic);

    public SubtaskHandlerTest() throws IOException {
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
    void postSubtask() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Задача", "Описание", eId,
                LocalDateTime.of(2024, 9, 10, 11, 30), 90);
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(partOfURL);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getSubtasks();
        assertEquals(1, subtasksFromManager.size());
        Subtask subtaskFromManager = subtasksFromManager.getFirst();

        assertEquals(2, subtaskFromManager.getId());
        assertEquals(subtask.getName(), subtaskFromManager.getName());
        assertEquals(subtask.getDescription(), subtaskFromManager.getDescription());
        assertEquals(Status.NEW, subtaskFromManager.getStatus());
        assertEquals(subtask.getStartTime(), subtaskFromManager.getStartTime());
        assertEquals(subtask.getDuration(), subtaskFromManager.getDuration());
        assertEquals(subtask.getEpicId(), subtaskFromManager.getEpicId());

        Subtask newSubtask = new Subtask(subtaskFromManager.getId(), "Обновленная подзадача",
                "Описание", Status.DONE, eId,
                LocalDateTime.of(2024, 9, 10, 11, 30), 90);
        String newSubtaskJson = gson.toJson(newSubtask);

        URI newUri = URI.create(partOfURL + subtaskFromManager.getId());
        HttpRequest newRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(newSubtaskJson))
                .uri(newUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> newResponse = client.send(newRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, newResponse.statusCode());

        List<Subtask> newSubtasksFromManager = manager.getSubtasks();
        Subtask newSubtaskFromManager = newSubtasksFromManager.getFirst();

        assertEquals(2, newSubtaskFromManager.getId());
        assertEquals(newSubtask.getName(), newSubtaskFromManager.getName());
        assertEquals(newSubtask.getDescription(), newSubtaskFromManager.getDescription());
        assertEquals(newSubtask.getStatus(), newSubtaskFromManager.getStatus());
        assertEquals(newSubtask.getStartTime(),
                newSubtaskFromManager.getStartTime());
        assertEquals(newSubtask.getDuration(), newSubtaskFromManager.getDuration());
        assertEquals(newSubtask.getEpicId(), newSubtaskFromManager.getEpicId());
    }

    @Test
    void getSubtask() throws IOException, InterruptedException {
        Subtask subtask1 = new Subtask("Задача", "Описание", eId,
                LocalDateTime.of(2024, 9, 10, 11, 30), 90);
        Subtask subtask2 = new Subtask("Задача", "Описание", eId,
                LocalDateTime.of(2024, 9, 11, 11, 30), 90);
        Subtask subtask3 = new Subtask("Задача", "Описание", eId,
                LocalDateTime.of(2024, 9, 12, 11, 30), 90);

        manager.addSubtask(subtask1);
        int id2 = manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

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

        List<Subtask> subtasks = new ArrayList<>();
        for (JsonElement jsonElement : jsonArray) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Subtask subtask = gson.fromJson(jsonObject, Subtask.class);
            subtasks.add(subtask);
        }

        List<Subtask> subtasksFromManager = manager.getSubtasks();

        assertEquals(subtasks.size(), subtasksFromManager.size());
        assertEquals(subtasks, subtasksFromManager);
        assertEquals(subtask2.getName(), subtasksFromManager.get(1).getName());
        assertEquals(subtask2.getDescription(), subtasksFromManager.get(1).getDescription());
        assertEquals(subtask2.getStatus(), subtasksFromManager.get(1).getStatus());
        assertEquals(subtask2.getStartTime(), subtasksFromManager.get(1).getStartTime());
        assertEquals(subtask2.getDuration(), subtasksFromManager.get(1).getDuration());
        assertEquals(subtask2.getEpicId(), subtasksFromManager.get(1).getEpicId());

        URI newUri = URI.create(partOfURL + id2);
        HttpRequest newRequest = HttpRequest.newBuilder()
                .GET()
                .uri(newUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> newResponse = client.send(newRequest, HttpResponse.BodyHandlers.ofString());

        JsonElement newJsonElem = JsonParser.parseString(newResponse.body());
        assertTrue(newJsonElem.isJsonObject());
        JsonObject jsonObject = newJsonElem.getAsJsonObject();
        Subtask newSubtask = gson.fromJson(jsonObject, Subtask.class);

        assertEquals(subtask2.getName(), newSubtask.getName());
        assertEquals(subtask2.getDescription(), newSubtask.getDescription());
        assertEquals(subtask2.getStatus(), newSubtask.getStatus());
        assertEquals(subtask2.getStartTime(), newSubtask.getStartTime());
        assertEquals(subtask2.getDuration(), newSubtask.getDuration());
        assertEquals(subtask2.getEpicId(), newSubtask.getEpicId());
    }

    @Test
    void deleteTask() throws IOException, InterruptedException {
        Subtask subtask1 = new Subtask("Задача", "Описание",  eId,
                LocalDateTime.of(2024, 9, 10, 11, 30), 90);
        Subtask subtask2 = new Subtask("Задача", "Описание", eId,
                LocalDateTime.of(2024, 9, 11, 11, 30), 90);

        int id1 = manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

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

        List<Subtask> subtasksFromManager = manager.getSubtasks();
        Subtask subtask = subtasksFromManager.getFirst();

        assertEquals(1, subtasksFromManager.size());
        assertEquals(subtask2, subtask);
        assertEquals(subtask2.getName(), subtask.getName());
        assertEquals(subtask2.getDescription(), subtask.getDescription());
        assertEquals(subtask2.getStatus(), subtask.getStatus());
        assertEquals(subtask2.getStartTime(), subtask.getStartTime());
        assertEquals(subtask2.getDuration(), subtask.getDuration());
        assertEquals(subtask2.getEpicId(), subtask.getEpicId());
    }
}
