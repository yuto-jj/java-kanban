package managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
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
        assertEquals(task, history.get(0));
    }

    @Test
    void remove() {
        TaskManager taskManager = new InMemoryTaskManager();
        int t1 = taskManager.addTask(task);
        Epic epic1 = new Epic("1. Эпик", "Тест эпика - 1");
        int e1 = taskManager.addEpic(epic1);
        Subtask subtask1 = new Subtask("1. Подзадача", "Тест подзадачи - 1", e1);
        int s1 = taskManager.addSubtask(subtask1);

        taskManager.getTask(t1);
        taskManager.getEpic(e1);
        taskManager.getSubtask(s1);
        assertEquals(3, taskManager.getHistory().size());
        assertEquals(task, taskManager.getHistory().get(0));
        assertEquals(epic1, taskManager.getHistory().get(1));
        assertEquals(subtask1, taskManager.getHistory().get(2));

        taskManager.removeTask(t1);
        assertEquals(2, taskManager.getHistory().size());
        taskManager.removeSubtask(s1);
        assertEquals(1, taskManager.getHistory().size());
        taskManager.removeEpic(e1);
        assertEquals(0, taskManager.getHistory().size());

    }

    @Test
    void mustDeletePreviousViewSameTask() {
        TaskManager taskManager = Managers.getDefault();
        final int taskId = taskManager.addTask(task);
        taskManager.getTask(taskId);
        Task task2 = new Task(taskId, "Новая задача - 1", "Обновление задачи - 1", Status.DONE);
        taskManager.taskUpdate(task2);
        taskManager.getTask(taskId);

        List<Task> history = taskManager.getHistory();
        assertEquals(task, history.getFirst());
        assertEquals(task2, history.getFirst());
    }

    @Test
    void mustNotStoreDuplicateTasks() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        historyManager.add(task);
        historyManager.add(task);
        assertEquals(1, historyManager.getHistory().size());
        assertEquals(task, historyManager.getHistory().get(0));

        Task task1 = new Task(0, "2. Задача", "Задача с одинаковым id", Status.DONE);
        assertEquals(task1.getId(), task.getId());
        historyManager.add(task);
        historyManager.add(task1);
        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void theHistoryMustNotChangeAfterRemoveNonExistingTask() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        historyManager.add(task);
        historyManager.remove(5);
        assertEquals(1, historyManager.getHistory().size());
        assertEquals(task, historyManager.getHistory().get(0));
    }
}