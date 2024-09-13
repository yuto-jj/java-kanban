package managers;

import org.junit.jupiter.api.Test;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void shouldReturnInitialisedTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager);
    }

    @Test
    void shouldReturnInitialisedHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager);
    }

    @Test
    void mustReturnEmployedTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Задача - 1", "Тест задачи - 1", "11:30 11.09.2024", 90);
        final int taskId = taskManager.addTask(task);
        ArrayList<Task> tasks = taskManager.getTasks();
        assertNotEquals(0, taskId);
        assertNotNull(tasks);
    }

    @Test
    void mustReturnEmployedHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("Задача - 1", "Тест задачи - 1", "11:30 11.09.2024", 90);
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertNotNull(history);
    }

}