package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler {

    public PrioritizedHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        if (!method.equals("GET")) {
            sendBadRequest(httpExchange);
        } else {
            String jsonTasks = gson.toJson(manager.getPrioritizedTasks());
            sendText(httpExchange, jsonTasks);
        }
    }
}
