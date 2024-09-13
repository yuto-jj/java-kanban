package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {


    @BeforeEach
    void beforeEach() {
        manager = new InMemoryTaskManager();
        task = new Task("Задача - 1", "Описание - 1",
                "11:30 12.09.2024", 90);
        tId = manager.addTask(task);
        epic = new Epic("Эпик - 1", "Описание - 1");
        eId = manager.addEpic(epic);
        sub = new Subtask("Подзадача - 1", "Описание - 1", eId,
                "11:30 13.09.2024", 90);
        sId = manager.addSubtask(sub);
        savedTask = manager.getTask(tId);
        savedEpic = manager.getEpic(eId);
        savedSub = manager.getSubtask(sId);
    }
    @Test
    void shouldAddTasksOfDifferentTypes() {
        Task task = new Task("Задача - 1", "Тест задачи - 1",
                "11:30 01.09.2024", 90);
        final int taskId = manager.addTask(task);
        Epic epic = new Epic("Эпик - 1", "Тест эпика - 1");
        final int epicId = manager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача - 1", "Тест позадачи - 1", epicId,
                "11:30 03.09.2024", 90);
        final int subtaskId = manager.addSubtask(subtask);

        assertNotNull(manager.getTasks());
        assertNotNull(manager.getEpics());
        assertNotNull(manager.getSubtasks());

        assertEquals(task, manager.getTask(taskId));
        assertEquals(epic, manager.getEpic(epicId));
        assertEquals(subtask, manager.getSubtask(subtaskId));
    }

    @Test
    void theGivenIdShouldNotConflictWithTheGeneratedId() {
        int givenTaskId = 5;
        int givenEpicId = 10;
        int givenSubtaskId = 15;
        Task task = new Task(givenTaskId, "Задача - 1", "Тест задачи - 1", Status.NEW,
                "11:30 13.09.2024", 90);
        final int taskId = manager.addTask(task);
        Epic epic = new Epic(givenEpicId, "Эпик - 1", "Тест эпика - 1");
        final int epicId = manager.addEpic(epic);
        Subtask subtask = new Subtask(givenSubtaskId, "Подзадача - 1", "Тест подзадачи - 1",
                Status.NEW, epicId, "11:30 14.09.2024", 90);
        final int subId = manager.addSubtask(subtask);

        assertNotEquals(givenTaskId, taskId);
        assertNotEquals(givenEpicId, epicId);
        assertNotEquals(givenSubtaskId, subId);
    }

    @Test
    void fieldsTasksShouldNotChangeWhenAddedToManager() {
        String name = "Задача - 1";
        String description = "Тест задачи - 1";
        Status status = Status.DONE;
        Task task = new Task(1, name, description, status, "11:30 15.09.2024", 90);
        final int taskId = manager.addTask(task);

        assertEquals(name, manager.getTask(taskId).getName());
        assertEquals(description, manager.getTask(taskId).getDescription());
        assertEquals(status, manager.getTask(taskId).getStatus());
    }

    @Test
    void epicsMustNotHaveIdsOfIrrelevantSubtasks() {
        Epic epic = new Epic("1. Эпик","Тест эпика - 1");
        int e1 = manager.addEpic(epic);
        Subtask sub1 = new Subtask("1. Подзадача", "Тест подзадачи - 1", e1,
                "11:30 16.09.2024", 90);
        int s1 = manager.addSubtask(sub1);
        Subtask sub2 = new Subtask("2. Подзадача", "Тест подзадачи - 2", e1,
                "11:30 17.09.2024", 90);
        int s2 = manager.addSubtask(sub2);

        assertEquals(2, epic.getSubsId().size());
        assertEquals(s1, epic.getSubsId().get(0));
        assertEquals(s2, epic.getSubsId().get(1));

        manager.removeSubtask(s1);
        assertEquals(1, epic.getSubsId().size());
        assertFalse(epic.getSubsId().contains(s1));
        assertEquals(s2, epic.getSubsId().get(0));
    }

    @Test
    void settersShouldInfluenceTheDataInsideTheTaskManager() {
        Task task = new Task("1. Задача", "Тест задачи - 1",
                "11:30 18.09.2024", 90);
        Task task2 = new Task("1. Задача", "Тест задачи - 1",
                "11:30 19.09.2024", 90);
        final int taskId = manager.addTask(task);
        task2.setId(5);
        task.setId(5);
        assertEquals(task2, manager.getTask(taskId));
        task.setId(6);
        assertNotEquals(task2, manager.getTask(taskId));
        task.setId(5);
        task.setStatus(Status.DONE);
        assertNotEquals(task2.getStatus(), manager.getTask(taskId).getStatus());

    }
}