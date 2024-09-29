package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import managers.Managers;
import managers.TaskManager;
import server.adapters.DurationAdapter;
import server.adapters.LocalDateTimeAdapter;
import server.handlers.*;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {

    private final HttpServer httpServer;
    private final TaskManager manager;
    protected static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;
        this.httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
    }

    public static Gson getGson() {
        return gson;
    }

    public static void main(String[] args) throws IOException {
        TaskManager manager = Managers.getDefault();
        Task task1 = new Task("Задача", "Описание", LocalDateTime.of(2024, 9,
                10, 11, 30), 90);
        int t1Id = manager.addTask(task1);
        Task task2 = new Task("Задача", "Описание", LocalDateTime.of(2024, 9,
                11, 11, 30), 90);
        manager.addTask(task2);
        Task task3 = new Task("Задача", "Описание", LocalDateTime.of(2024, 9,
                12, 11, 30), 90);
        int t3Id = manager.addTask(task3);
        Epic epic = new Epic("Эпик - 1", "Описание");
        int eId = manager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", eId,
                LocalDateTime.of(2024, 9, 13, 11, 30), 90);
        int sId = manager.addSubtask(subtask);
        manager.getTask(t1Id);
        manager.getTask(t3Id);
        manager.getSubtask(sId);
        manager.getEpic(eId);

        HttpTaskServer httpTaskServer = new HttpTaskServer(manager);
        httpTaskServer.start();
    }

    public void start() {
        httpServer.createContext("/tasks", new TaskHandler(manager));
        httpServer.createContext("/subtasks", new SubtaskHandler(manager));
        httpServer.createContext("/epics", new EpicHandler(manager));
        httpServer.createContext("/history", new HistoryHandler(manager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(manager));

        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }
}
