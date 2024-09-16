package managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    Task task;
    HistoryManager historyManager;
    TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault();
        task = new Task("Задача - 1", "Тест задачи - 1",
                LocalDateTime.of(2024, 9, 11, 11, 30), 90);
    }

    @Test
    void add() {
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
        assertEquals(task, history.getFirst());
    }

    @Test
    void remove() {
        int t1 = taskManager.addTask(task);
        Epic epic1 = new Epic("1. Эпик", "Тест эпика - 1");
        int e1 = taskManager.addEpic(epic1);
        Subtask subtask1 = new Subtask("1. Подзадача", "Тест подзадачи - 1", e1,
                LocalDateTime.of(2024, 9, 12, 11, 30), 90);
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
        final int taskId = taskManager.addTask(task);
        taskManager.getTask(taskId);
        Task task2 = new Task(taskId, "Новая задача - 1", "Обновление задачи - 1", Status.DONE,
                LocalDateTime.of(2024, 9, 13, 11, 30), 90);
        taskManager.taskUpdate(task2);
        taskManager.getTask(taskId);

        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task2, history.getFirst());
        assertNotEquals(task.getName(), history.getFirst().getName());
        assertNotEquals(task.getDescription(), history.getFirst().getDescription());
    }

    @Test
    void mustNotStoreDuplicateTasks() {
        historyManager.add(task);
        historyManager.add(task);
        assertEquals(1, historyManager.getHistory().size());
        assertEquals(task, historyManager.getHistory().getFirst());

        Task task1 = new Task(0, "2. Задача", "Задача с одинаковым id", Status.DONE,
                LocalDateTime.of(2024, 9, 14, 11, 30), 90);
        assertEquals(task1.getId(), task.getId());
        historyManager.add(task);
        historyManager.add(task1);
        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void theHistoryMustNotChangeAfterRemoveNonExistingTask() {
        historyManager.add(task);
        historyManager.remove(5);
        assertEquals(1, historyManager.getHistory().size());
        assertEquals(task, historyManager.getHistory().getFirst());
    }

    @Test
    void mustDeleteTheTaskAtTheBeginningInTheMiddleAndAtTheEndOfTheStory() {
        historyManager.add(task);
        Task task2 = new Task(1, "Задача - 1", "Описание - 1", Status.DONE,
                LocalDateTime.of(2024, 9, 12, 11, 30), 90);
        historyManager.add(task2);
        Task task3 = new Task(2, "Задача - 1", "Описание - 1", Status.NEW,
                LocalDateTime.of(2024, 9, 13, 11, 30), 90);
        historyManager.add(task3);
        Task task4 = new Task(3, "Задача - 1", "Описание - 1", Status.DONE,
                LocalDateTime.of(2024, 9, 14, 11, 30), 90);
        historyManager.add(task4);
        Task task5 = new Task(4, "Задача - 1", "Описание - 1", Status.IN_PROGRESS,
                LocalDateTime.of(2024, 9, 15, 11, 30), 90);
        historyManager.add(task5);
        Task task6 = new Task(5, "Задача - 1", "Описание - 1", Status.NEW,
                LocalDateTime.of(2024, 9, 16, 11, 30), 90);
        historyManager.add(task6);

        assertEquals(6, historyManager.getHistory().size());
        historyManager.remove(0);
        assertEquals(5, historyManager.getHistory().size());
        historyManager.remove(3);
        assertEquals(4, historyManager.getHistory().size());
        historyManager.remove(5);
        assertEquals(3, historyManager.getHistory().size());
    }
}