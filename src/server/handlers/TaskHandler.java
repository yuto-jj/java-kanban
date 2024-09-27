package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import exceptions.TimeValidationException;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler {

    private final TaskManager manager;

    public TaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String path = httpExchange.getRequestURI().getPath();
        String[] splitPath = path.split("/");
        switch (method) {
            case "GET":
                if (splitPath.length == 2) {
                    String jsonStringOfTasks = gson.toJson(manager.getTasks());
                    sendText(httpExchange, jsonStringOfTasks);
                    break;
                } else if (splitPath.length > 2) {
                    Optional<Integer> optId = getId(httpExchange);
                    if (optId.isPresent()) {
                        int id = optId.get();
                        try {
                            String jsonStringOfTask = gson.toJson(manager.getTask(id));
                            sendText(httpExchange, jsonStringOfTask);
                            break;
                        } catch (NotFoundException e) {
                            sendNotFound(httpExchange);
                            break;
                        }
                    }
                }
                sendNotFound(httpExchange);
                break;
            case "POST":
                InputStream inputStream = httpExchange.getRequestBody();
                String taskJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                Task task = gson.fromJson(taskJson, Task.class);
                try {
                    if (task.getId() == 0) {
                        manager.addTask(task);
                    } else {
                        manager.taskUpdate(task);
                    }
                    httpExchange.sendResponseHeaders(201, -1);
                    break;
                } catch (TimeValidationException e) {
                    sendHasInteractions(httpExchange);
                    break;
                } catch (NotFoundException e) {
                    sendNotFound(httpExchange);
                    break;
                }
            case "DELETE":
                if (splitPath.length > 2) {
                    Optional<Integer> optId = getId(httpExchange);
                    if (optId.isPresent()) {
                        int id = optId.get();
                        try {
                            manager.removeTask(id);
                            httpExchange.sendResponseHeaders(201, -1);
                        } catch (NotFoundException e) {
                            sendNotFound(httpExchange);
                        }
                    }
                }
                sendNotFound(httpExchange);
                break;
            default:
                sendNotFound(httpExchange);
        }
    }
}
