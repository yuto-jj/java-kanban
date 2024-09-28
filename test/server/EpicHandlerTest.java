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
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EpicHandlerTest {

    String partOfURL = "http://localhost:8080/epics/";
    Gson gson = HttpTaskServer.getGson();
    TaskManager manager = Managers.getDefault();
    HttpTaskServer server = new HttpTaskServer(manager);

    public EpicHandlerTest() throws IOException {
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
    void postepic() throws IOException, InterruptedException {
        Epic epic = new Epic("Задача", "Описание");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(partOfURL);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = manager.getEpics();

        assertNotNull(epicsFromManager);
        assertEquals(1, epicsFromManager.size());

        Epic epicFromManager = epicsFromManager.getFirst();

        assertEquals(1, epicFromManager.getId());
        assertEquals(epic.getName(), epicFromManager.getName());
        assertEquals(epic.getDescription(), epicFromManager.getDescription());
        assertEquals(Status.NEW, epicFromManager.getStatus());
        assertNull(epicFromManager.getStartTime());
        assertEquals(epic.getDuration(), epicFromManager.getDuration());
        assertNull(epicFromManager.getEndTime());
        assertEquals(epic.getSubsId(), epicFromManager.getSubsId());

        Epic newEpic = new Epic(epicFromManager.getId(), "Обновленный эпик","Описание");
        String newEpicJson = gson.toJson(newEpic);

        URI newUri = URI.create(partOfURL + epicFromManager.getId());
        HttpRequest newRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(newEpicJson))
                .uri(newUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> newResponse = client.send(newRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, newResponse.statusCode());

        List<Epic> newEpicsFromManager = manager.getEpics();
        Epic newEpicFromManager = newEpicsFromManager.getFirst();

        assertEquals(1, newEpicFromManager.getId());
        assertEquals(newEpic.getName(), newEpicFromManager.getName());
        assertEquals(newEpic.getDescription(), newEpicFromManager.getDescription());
        assertEquals(newEpic.getStatus(), newEpicFromManager.getStatus());
        assertNull(newEpicFromManager.getStartTime());
        assertEquals(newEpic.getDuration(), newEpicFromManager.getDuration());
        assertNull(newEpicFromManager.getEndTime());
        assertEquals(newEpic.getSubsId(), newEpicFromManager.getSubsId());
    }

    @Test
    void getEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик - 1", "Описание");
        Epic epic2 = new Epic("Эпик - 2", "Описание");
        Epic epic3 = new Epic("Эпик - 3", "Описание");

        int id1 = manager.addEpic(epic1);
        int id2 = manager.addEpic(epic2);
        int id3 = manager.addEpic(epic3);

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

        List<Epic> epics = new ArrayList<>();
        for (JsonElement jsonElement : jsonArray) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Epic epic = gson.fromJson(jsonObject, Epic.class);
            epics.add(epic);
        }

        List<Epic> epicsFromManager = manager.getEpics();

        assertEquals(epics.size(), epicsFromManager.size());
        assertEquals(id1, epicsFromManager.get(0).getId());
        assertEquals(id2, epicsFromManager.get(1).getId());
        assertEquals(id3, epicsFromManager.get(2).getId());
        assertEquals(epic2.getName(), epicsFromManager.get(1).getName());
        assertEquals(epic2.getDescription(), epicsFromManager.get(1).getDescription());
        assertEquals(epic2.getStatus(), epicsFromManager.get(1).getStatus());
        assertNull(epicsFromManager.get(1).getStartTime());
        assertEquals(epic2.getDuration(), epicsFromManager.get(1).getDuration());
        assertNull(epicsFromManager.get(2).getEndTime());
        assertEquals(epic2.getSubsId(), epicsFromManager.get(2).getSubsId());

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
        Epic newEpic = gson.fromJson(jsonObject, Epic.class);

        assertEquals(epic2.getName(), newEpic.getName());
        assertEquals(epic2.getDescription(), newEpic.getDescription());
        assertEquals(epic2.getStatus(), newEpic.getStatus());
        assertNull(newEpic.getStartTime());
        assertEquals(epic2.getDuration(), newEpic.getDuration());
        assertNull(newEpic.getEndTime());
        assertEquals(epic2.getSubsId(), newEpic.getSubsId());

        Subtask subtask1 = new Subtask("1. Подзадача", "1. Эпик. 1. Подзадача", id2,
                LocalDateTime.of(2024, 9, 12, 11, 30), 90);
        manager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("2. Подзадача", "1. Эпик. 2. Подзадача", id2,
                LocalDateTime.of(2024, 9, 13, 11, 30), 90);
        manager.addSubtask(subtask2);

        URI subUri = URI.create(partOfURL + id2 + "/subtasks");
        HttpRequest subRequest = HttpRequest.newBuilder()
                .GET()
                .uri(subUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> subResponse = client.send(subRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, subResponse.statusCode());

        JsonElement subJsonElem = JsonParser.parseString(subResponse.body());
        assertTrue(subJsonElem.isJsonArray());
        JsonArray subJsonArray = subJsonElem.getAsJsonArray();

        List<Subtask> subs = new ArrayList<>();
        for (JsonElement jsonElement : subJsonArray) {
            JsonObject subJsonObject = jsonElement.getAsJsonObject();
            Subtask sub = gson.fromJson(subJsonObject, Subtask.class);
            subs.add(sub);
        }

        List<Subtask> epicSubsFromManager = manager.getSubtasksByEpicId(id2);
        assertEquals(epicSubsFromManager, subs);

        Subtask sub = subs.get(1);

        assertEquals(subtask2.getName(), sub.getName());
        assertEquals(subtask2.getDescription(), sub.getDescription());
        assertEquals(subtask2.getStatus(), sub.getStatus());
        assertEquals(subtask2.getStartTime(), sub.getStartTime());
        assertEquals(subtask2.getDuration(), sub.getDuration());
        assertEquals(subtask2.getEndTime(), sub.getEndTime());
        assertEquals(subtask2.getEpicId(), sub.getEpicId());
    }

    @Test
    void deleteEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Задача", "Описание");
        Epic epic2 = new Epic("Задача", "Описание");

        int id1 = manager.addEpic(epic1);
        manager.addEpic(epic2);

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

        List<Epic> epicsFromManager = manager.getEpics();
        Epic epic = epicsFromManager.getFirst();

        assertEquals(1, epicsFromManager.size());
        assertEquals(epic2, epic);
        assertEquals(epic2.getName(), epic.getName());
        assertEquals(epic2.getDescription(), epic.getDescription());
        assertEquals(epic2.getStatus(), epic.getStatus());
        assertNull(epic.getStartTime());
        assertEquals(epic2.getDuration(), epic.getDuration());
        assertNull(epic.getEndTime());
        assertEquals(epic2.getSubsId(), epic.getSubsId());
    }
}
