package managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    Task task;

    @BeforeEach
    void beforeEach() {
        task = new Task("Задача - 1", "Тест задачи - 1");
    }

    @Test
    void add() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void shouldSavePreviousTaskVersion() {
        TaskManager taskManager = Managers.getDefault();
        final int taskId = taskManager.addTask(task);
        taskManager.getTask(taskId);
        Task task2 = new Task(taskId, "Новая задача - 1", "Обновление задачи - 1", Status.DONE);
        taskManager.taskUpdate(task2);
        taskManager.getTask(taskId);

        List<Task> history = taskManager.getHistory();
        assertEquals(task, history.get(0));
        assertEquals(task2, history.get(1));
    }
}