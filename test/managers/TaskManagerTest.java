package managers;

import exceptions.NotFoundException;
import exceptions.TimeValidationException;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class TaskManagerTest<T extends TaskManager> {

    T manager;
    Task task;
    Task savedTask;
    Epic epic;
    Epic savedEpic;
    Subtask sub;
    Subtask savedSub;
    int tId;
    int eId;
    int sId;

    @Test
    void addTaskTest() {
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final ArrayList<Task> tasks = manager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void addEpicTest() {
        assertNotNull(savedEpic, "Эпик не найдена.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final ArrayList<Epic> epics = manager.getEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.getFirst(), "Эпики не совпадают.");
    }

    @Test
    void addSubtaskTest() {
        assertNotNull(savedSub, "Подзадача не найдена.");
        assertEquals(sub, savedSub, "Подзадачи не совпадают.");

        final ArrayList<Subtask> subtasks = manager.getSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(sub, subtasks.getFirst(), "Подзадачи не совпадают.");
    }

    @Test
    void getTaskTest() {
        assertEquals(task.getId(), savedTask.getId());
        assertEquals(task.getName(), savedTask.getName());
        assertEquals(task.getDescription(), savedTask.getDescription());
        assertEquals(task.getStatus(), savedTask.getStatus());
        assertEquals(task.getStartTime(), savedTask.getStartTime());
        assertEquals(task.getDuration(), savedTask.getDuration());
    }

    @Test
    void getEpicTest() {
        assertEquals(epic.getId(), savedEpic.getId());
        assertEquals(epic.getName(), savedEpic.getName());
        assertEquals(epic.getDescription(), savedEpic.getDescription());
        assertEquals(epic.getStatus(), savedEpic.getStatus());
        assertEquals(epic.getStartTime(), savedEpic.getStartTime());
        assertEquals(epic.getDuration(), savedEpic.getDuration());
        assertEquals(epic.getSubsId(), savedEpic.getSubsId());
    }

    @Test
    void getSubtaskTest() {
        assertEquals(sub.getId(), savedSub.getId());
        assertEquals(sub.getName(), savedSub.getName());
        assertEquals(sub.getDescription(), savedSub.getDescription());
        assertEquals(sub.getStatus(), savedSub.getStatus());
        assertEquals(sub.getStartTime(), savedSub.getStartTime());
        assertEquals(sub.getDuration(), savedSub.getDuration());
        assertEquals(sub.getEpicId(), savedSub.getEpicId());
    }

    @Test
    void getTasksTest() {
        ArrayList<Task> tasks = manager.getTasks();
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertEquals(task, tasks.getFirst());
    }

    @Test
    void getEpicsTest() {
        ArrayList<Epic> epics = manager.getEpics();
        assertNotNull(epics);
        assertEquals(1, epics.size());
        assertEquals(epic, epics.getFirst());
    }

    @Test
    void getSubtasksTest() {
        ArrayList<Subtask> subs = manager.getSubtasks();
        assertNotNull(subs);
        assertEquals(1, subs.size());
        assertEquals(sub, subs.getFirst());
    }

    @Test
    void removeTaskTest() {
        Task task1 = new Task("Задача - 2", "Описание - 2",
                LocalDateTime.of(2024, 9, 14, 11, 30), 90);
        int t2Id = manager.addTask(task1);
        manager.removeTask(tId);
        assertEquals(1, manager.getTasks().size());
        assertEquals(task1, manager.getTasks().getFirst());
        assertEquals(manager.getTask(t2Id), manager.getTasks().getFirst());
        assertThrows(NotFoundException.class, () -> manager.getTask(tId));
    }

    @Test
    void removeEpicTest() {
        Epic epic1 = new Epic("Задача - 2", "Описание - 2");
        int e2Id = manager.addEpic(epic1);
        manager.removeEpic(eId);
        assertEquals(1, manager.getEpics().size());
        assertEquals(epic1, manager.getEpics().getFirst());
        assertEquals(manager.getEpic(e2Id), manager.getEpics().getFirst());
        assertThrows(NotFoundException.class, () -> manager.getEpic(eId));
    }

    @Test
    void removeSubtaskTest() {
        Subtask sub1 = new Subtask("Задача - 2", "Описание - 2", eId,
                LocalDateTime.of(2024, 9, 14, 11, 30), 90);
        int s1Id = manager.addSubtask(sub1);
        manager.removeSubtask(sId);
        assertEquals(1, manager.getSubtasks().size());
        assertEquals(sub1, manager.getSubtasks().getFirst());
        assertEquals(manager.getSubtask(s1Id), manager.getSubtasks().getFirst());
        assertThrows(NotFoundException.class, () -> manager.getSubtask(sId));
    }

    @Test
    void removeTasksTest() {
        Task task1 = new Task("Задача - 2", "Описание - 2",
                LocalDateTime.of(2024, 9, 14, 11, 30), 90);
        manager.addTask(task1);
        manager.removeTasks();
        assertEquals(0, manager.getTasks().size());
        assertThrows(NotFoundException.class, () -> manager.getTask(task.getId()));
        assertThrows(NotFoundException.class, () ->manager.getTask(task1.getId()));
    }

    @Test
    void removeEpicsTest() {
        Epic epic1 = new Epic("Задача - 2", "Описание - 2");
        manager.addEpic(epic1);
        manager.removeEpics();
        assertEquals(0, manager.getEpics().size());
        assertThrows(NotFoundException.class, () -> manager.getEpic(epic.getId()));
        assertThrows(NotFoundException.class, () -> manager.getEpic(epic1.getId()));
    }

    @Test
    void removeSubtasksTest() {
        Subtask sub1 = new Subtask("Задача - 2", "Описание - 2", eId,
                LocalDateTime.of(2024, 9, 14, 11, 30), 90);
        manager.addSubtask(sub1);
        manager.removeSubtasks();
        assertEquals(0, manager.getSubtasks().size());
        assertThrows(NotFoundException.class, () -> manager.getSubtask(sub.getId()));
        assertThrows(NotFoundException.class, () -> manager.getSubtask(sub1.getId()));
    }

    @Test
    void taskUpdateTest() {
        Task task1 = new Task(tId, "Задача - обновлена", "Описание - обновлено", Status.DONE,
                LocalDateTime.of(2024, 9, 13, 0, 0), 180);
        manager.taskUpdate(task1);
        Task updatedTask = manager.getTask(tId);
        assertEquals(task.getId(), updatedTask.getId());
        assertEquals(task1.getId(), updatedTask.getId());
        assertEquals(task1.getName(), updatedTask.getName());
        assertEquals(task1.getDescription(), updatedTask.getDescription());
        assertEquals(task1.getStatus(), updatedTask.getStatus());
        assertEquals(task1.getStartTime(), updatedTask.getStartTime());
        assertEquals(task1.getDuration(), updatedTask.getDuration());
    }

    @Test
    void epicUpdateTest() {
        Epic epic1 = new Epic(eId, "Задача - обновлена", "Описание - обновлено");
        manager.epicUpdate(epic1);
        Epic updatedEpic = manager.getEpic(eId);
        assertEquals(epic.getId(), updatedEpic.getId());
        assertEquals(epic1.getId(), updatedEpic.getId());
        assertEquals(epic1.getName(), updatedEpic.getName());
        assertEquals(epic1.getDescription(), updatedEpic.getDescription());
        assertEquals(epic.getStatus(), updatedEpic.getStatus());
        assertEquals(epic.getStartTime(), updatedEpic.getStartTime(), "sT");
        assertEquals(epic.getDuration(), updatedEpic.getDuration(), "dR");
        assertEquals(epic.getSubsId(), updatedEpic.getSubsId(), "sId");
    }

    @Test
    void subtaskUpdateTest() {
        Subtask sub1 = new Subtask(sId, "Задача - обновлена", "Описание - обновлено",
                Status.DONE, eId, LocalDateTime.of(2024, 9, 13, 0, 0), 180);
        manager.subtaskUpdate(sub1);
        Subtask updatedSub = manager.getSubtask(sId);
        assertEquals(sub.getId(), updatedSub.getId());
        assertEquals(sub1.getId(), updatedSub.getId());
        assertEquals(sub1.getName(), updatedSub.getName());
        assertEquals(sub1.getDescription(), updatedSub.getDescription());
        assertEquals(sub1.getStatus(), updatedSub.getStatus());
        assertEquals(sub1.getStartTime(), updatedSub.getStartTime());
        assertEquals(sub1.getDuration(), updatedSub.getDuration());
        assertEquals(sub1.getEpicId(), updatedSub.getEpicId());
    }

    @Test
    void getSubtasksByEpicIdTest() {
        Subtask sub1 = new Subtask("Задача - 2", "Описание - 2", eId,
                LocalDateTime.of(2024, 9, 14, 11, 30), 90);
        manager.addSubtask(sub1);
        ArrayList<Subtask> subtasks = manager.getSubtasksByEpicId(eId);
        assertNotNull(subtasks);
        assertEquals(2, subtasks.size());
        assertEquals(sub, subtasks.get(0));
        assertEquals(sub1, subtasks.get(1));
    }

    @Test
    void timeIntervalIntersectionTest() {
        Task task1 = new Task("Задача - 2", "Описание - 2",
                LocalDateTime.of(2024, 9, 12, 12, 0), 90);
        Subtask sub1 = new Subtask("Подзадача - 2", "Описание - 2", eId,
                LocalDateTime.of(2024, 9, 13, 12, 0), 90);

        assertThrows(TimeValidationException.class, () -> manager.addTask(task1));
        assertThrows(TimeValidationException.class, () -> manager.addSubtask(sub1));

        assertFalse(manager.getTasks().contains(task1));
        assertFalse(manager.getSubtasks().contains(sub1));
        assertTrue(manager.getTasks().contains(task));
        assertTrue(manager.getSubtasks().contains(sub));
        assertNotNull(manager.getTask(tId));
        assertNotNull(manager.getSubtask(sId));
    }
}