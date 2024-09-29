package server.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import exceptions.TimeValidationException;
import managers.TaskManager;
import tasks.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler {

    public SubtaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String path = httpExchange.getRequestURI().getPath();
        String[] splitPath = path.split("/");
        switch (method) {
            case "GET":
                if (splitPath.length == 2) {
                    String jsonStringOfSubtasks = gson.toJson(manager.getSubtasks());
                    sendText(httpExchange, jsonStringOfSubtasks);
                    break;
                } else if (splitPath.length > 2) {
                    Optional<Integer> optId = getId(httpExchange);
                    if (optId.isPresent()) {
                        int id = optId.get();
                        try {
                            String jsonStringOfSubtask = gson.toJson(manager.getSubtask(id));
                            sendText(httpExchange, jsonStringOfSubtask);
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
                String subtaskJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                if (subtaskJson.isEmpty()) {
                    sendBadRequest(httpExchange);
                    break;
                }
                Subtask subtask;
                try {
                    subtask = gson.fromJson(subtaskJson, Subtask.class);
                } catch (JsonSyntaxException e) {
                    sendBadRequest(httpExchange);
                    break;
                }
                try {
                    if (subtask.getId() == 0) {
                        manager.addSubtask(subtask);
                    } else {
                        manager.subtaskUpdate(subtask);
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
                            manager.removeSubtask(id);
                            httpExchange.sendResponseHeaders(201, -1);
                            break;
                        } catch (NotFoundException e) {
                            sendNotFound(httpExchange);
                            break;
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
