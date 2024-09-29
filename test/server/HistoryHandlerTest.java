package server;

import com.google.gson.*;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HistoryHandlerTest {

    String url = "http://localhost:8080/history/";
    Gson gson = HttpTaskServer.getGson();
    TaskManager manager = Managers.getDefault();
    HttpTaskServer server = new HttpTaskServer(manager);

    public HistoryHandlerTest() throws IOException {
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
    void getPrioritizedTasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик - 1", "Описание");
        int eId = manager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", eId,
                LocalDateTime.of(2024, 9, 10, 11, 30), 90);
        int sId = manager.addSubtask(subtask);
        Task task = new Task("Задача", "Описание", LocalDateTime.of(2024, 9,
                11, 11, 30), 90);
        int tId = manager.addTask(task);

        manager.getEpic(eId);
        manager.getTask(tId);
        manager.getSubtask(sId);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(url);
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
        Task aTask = null;
        Subtask aSub = null;
        Epic aEpic = null;
        for (JsonElement jsonElement : jsonArray) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            switch (jsonObject.get("type").getAsString()) {
                case "TASK":
                    aTask = gson.fromJson(jsonObject, Task.class);
                    tasks.add(aTask);
                    break;
                case "EPIC":
                    aEpic = gson.fromJson(jsonObject, Epic.class);
                    tasks.add(aEpic);
                    break;
                case "SUBTASK":
                    aSub = gson.fromJson(jsonObject, Subtask.class);
                    tasks.add(aSub);
                    break;
            }
        }

        List<Task> tasksFromManager = manager.getHistory();

        assertEquals(tasks.size(), tasksFromManager.size());
        assertEquals(tasks, tasksFromManager);

        Epic epicFromManager = manager.getEpic(eId);

        assertEquals(task, aTask);
        assertEquals(task.getName(), aTask.getName());
        assertEquals(task.getDescription(), aTask.getDescription());
        assertEquals(task.getStatus(), aTask.getStatus());
        assertEquals(task.getStartTime(), aTask.getStartTime());
        assertEquals(task.getDuration(), aTask.getDuration());

        assertEquals(subtask, aSub);
        assertEquals(subtask.getName(), aSub.getName());
        assertEquals(subtask.getDescription(), aSub.getDescription());
        assertEquals(subtask.getStatus(), aSub.getStatus());
        assertEquals(subtask.getStartTime(), aSub.getStartTime());
        assertEquals(subtask.getDuration(), aSub.getDuration());
        assertEquals(subtask.getEpicId(), aSub.getEpicId());

        assertEquals(epicFromManager, aEpic);
        assertEquals(epicFromManager.getName(), aEpic.getName());
        assertEquals(epicFromManager.getDescription(), aEpic.getDescription());
        assertEquals(epicFromManager.getStatus(), aEpic.getStatus());
        assertEquals(epicFromManager.getStartTime(), aEpic.getStartTime());
        assertEquals(epicFromManager.getDuration(), aEpic.getDuration());
        assertEquals(epicFromManager.getEndTime(), aEpic.getEndTime());
        assertEquals(epicFromManager.getSubsId(), aEpic.getSubsId());
    }
}
