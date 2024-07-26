package tasks;

import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    TaskManager taskManager = new InMemoryTaskManager();

    @Test
    void addNewSubtask() {
        Epic epic = new Epic("Эпик - 1", "Тест эпика - 1");
        final int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача - 1", "Тест подзадачи - 1",
                epicId);
        final int subId = taskManager.addSubtask(subtask);

        final Subtask savedSubtask = taskManager.getSubtask(subId);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");

        final ArrayList<Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");
    }

}