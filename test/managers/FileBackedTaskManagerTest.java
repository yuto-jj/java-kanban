package managers;

import exceptions.ManagerLoadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    FileBackedTaskManager manager;
    File file;

    @BeforeEach
    void beforeEach() throws IOException {
        file = File.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(file);
    }

    @Test
    void mustLoadAnEmptyFile() throws IOException {
        File file1 = File.createTempFile("empty", ".csv");
        FileBackedTaskManager secondManager = FileBackedTaskManager.loadFromFile(file1);
        assertNotNull(secondManager);
    }

    @Test
    void mustSaveTasksToNotExistFile() {
        File file1 = new File("resources/123.csv");
        FileBackedTaskManager secondManager = new FileBackedTaskManager(file1);
        Task task = new Task("Первая задача", "Описание - 1");
        int taskId = secondManager.addTask(task);
        FileBackedTaskManager thirdManager = FileBackedTaskManager.loadFromFile(file1);
        assertEquals(secondManager.getTask(taskId), thirdManager.getTask(taskId));
    }

    @Test
    void mustNotLoadTasksFromNotExistFile() {
        File file1 = new File("resources/456.csv");
        assertThrows(ManagerLoadException.class, () -> FileBackedTaskManager.loadFromFile(file1));
    }

    @Test
    void mustSaveTasksInFileAndLoadFromFile() {
        Task task = new Task("Первая задача", "Описание - 1");
        int taskId = manager.addTask(task);
        Epic epic = new Epic("Первый эпик", "Опсание - 2");
        int epicId = manager.addEpic(epic);
        Subtask subtask = new Subtask("Первая подзадача", "Опсание - 3", epicId);
        int subId = manager.addSubtask(subtask);

        assertEquals(task, manager.getTask(taskId));
        assertEquals(task.getName(), manager.getTask(taskId).getName());
        assertEquals(task.getDescription(), manager.getTask(taskId).getDescription());
        assertEquals(task.getStatus(), manager.getTask(taskId).getStatus());

        assertEquals(epic, manager.getEpic(epicId));
        assertEquals(epic.getName(), manager.getEpic(epicId).getName());
        assertEquals(epic.getDescription(), manager.getEpic(epicId).getDescription());
        assertEquals(epic.getStatus(), manager.getEpic(epicId).getStatus());
        assertEquals(epic.getSubsId(), manager.getEpic(epicId).getSubsId());

        assertEquals(subtask, manager.getSubtask(subId));
        assertEquals(subtask.getName(), manager.getSubtask(subId).getName());
        assertEquals(subtask.getDescription(), manager.getSubtask(subId).getDescription());
        assertEquals(subtask.getStatus(), manager.getSubtask(subId).getStatus());
        assertEquals(subtask.getEpicId(), manager.getSubtask(subId).getEpicId());

        FileBackedTaskManager secondManager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(manager.getTask(taskId), secondManager.getTask(taskId));
        assertEquals(manager.getTask(taskId).getName(), secondManager.getTask(taskId).getName());
        assertEquals(manager.getTask(taskId).getDescription(), secondManager.getTask(taskId).getDescription());
        assertEquals(manager.getTask(taskId).getStatus(), secondManager.getTask(taskId).getStatus());

        assertEquals(manager.getEpic(epicId), secondManager.getEpic(epicId));
        assertEquals(manager.getEpic(epicId).getName(), secondManager.getEpic(epicId).getName());
        assertEquals(manager.getEpic(epicId).getDescription(), secondManager.getEpic(epicId).getDescription());
        assertEquals(manager.getEpic(epicId).getStatus(), secondManager.getEpic(epicId).getStatus());
        assertEquals(manager.getEpic(epicId).getSubsId(), secondManager.getEpic(epicId).getSubsId());

        assertEquals(manager.getSubtask(subId), secondManager.getSubtask(subId));
        assertEquals(manager.getSubtask(subId).getName(), secondManager.getSubtask(subId).getName());
        assertEquals(manager.getSubtask(subId).getDescription(), secondManager.getSubtask(subId).getDescription());
        assertEquals(manager.getSubtask(subId).getStatus(), secondManager.getSubtask(subId).getStatus());
        assertEquals(manager.getSubtask(subId).getEpicId(), secondManager.getSubtask(subId).getEpicId());
    }
}
