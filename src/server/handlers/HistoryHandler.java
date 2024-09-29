package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        if (!method.equals("GET")) {
            sendBadRequest(httpExchange);
        } else {
            String jsonHistory = gson.toJson(manager.getHistory());
            sendText(httpExchange, jsonHistory);
        }
    }
}
