package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }
    @Test
    void shouldAddTasksOfDifferentTypes() {
        Task task = new Task("Задача - 1", "Тест задачи - 1");
        final int taskId = taskManager.addTask(task);
        Epic epic = new Epic("Эпик - 1", "Тест эпика - 1");
        final int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача - 1", "Тест позадачи - 1", epicId);
        final int subtaskId = taskManager.addSubtask(subtask);

        assertNotNull(taskManager.getTasks());
        assertNotNull(taskManager.getEpics());
        assertNotNull(taskManager.getSubtasks());

        assertEquals(task, taskManager.getTask(taskId));
        assertEquals(epic, taskManager.getEpic(epicId));
        assertEquals(subtask, taskManager.getSubtask(subtaskId));
    }

    @Test
    void theGivenIdShouldNotConflictWithTheGeneratedId() {
        int givenTaskId = 5;
        int givenEpicId = 10;
        int givenSubtaskId = 15;
        Task task = new Task(givenTaskId, "Задача - 1", "Тест задачи - 1", Status.NEW);
        final int taskId = taskManager.addTask(task);
        Epic epic = new Epic(givenEpicId, "Эпик - 1", "Тест эпика - 1");
        final int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask(givenSubtaskId, "Подзадача - 1", "Тест подзадачи - 1",
                Status.NEW, epicId);
        final int subId = taskManager.addSubtask(subtask);

        assertNotEquals(givenTaskId, taskId);
        assertNotEquals(givenEpicId, epicId);
        assertNotEquals(givenSubtaskId, subId);
    }

    @Test
    void fieldsTasksShouldNotChangeWhenAddedToManager() {
        String name = "Задача - 1";
        String description = "Тест задачи - 1";
        Status status = Status.DONE;
        Task task = new Task(1, name, description, status);
        final int taskId = taskManager.addTask(task);

        assertEquals(name, taskManager.getTask(taskId).getName());
        assertEquals(description, taskManager.getTask(taskId).getDescription());
        assertEquals(status, taskManager.getTask(taskId).getStatus());
    }
}