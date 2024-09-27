package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import managers.TaskManager;
import tasks.Epic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler {

    private final TaskManager manager;

    public EpicHandler(TaskManager manager) {
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
                    String jsonStringOfEpics = gson.toJson(manager.getEpics());
                    sendText(httpExchange, jsonStringOfEpics);
                    break;
                } else if (splitPath.length > 2) {
                    Optional<Integer> optId = getId(httpExchange);
                    if (optId.isPresent()) {
                        int id = optId.get();
                        try {
                            if (splitPath.length == 3) {
                                String jsonStringOfEpic = gson.toJson(manager.getEpic(id));
                                sendText(httpExchange, jsonStringOfEpic);
                                break;
                            } else {
                                String jsonStringOfSubtasks = gson.toJson(manager.getSubtasksByEpicId(id));
                                sendText(httpExchange, jsonStringOfSubtasks);
                                break;
                            }
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
                String epicJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                Epic epic = gson.fromJson(epicJson, Epic.class);
                    try {
                        if (epic.getId() == 0) {
                            manager.addEpic(epic);
                        } else {
                            manager.epicUpdate(epic);
                        }
                        httpExchange.sendResponseHeaders(201, -1);
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
                            manager.removeEpic(id);
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
