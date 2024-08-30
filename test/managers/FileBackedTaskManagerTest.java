package managers;

import exceptions.ManagerSaveException;
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
        assertThrows(ManagerSaveException.class, () -> {
                    FileBackedTaskManager.loadFromFile(file1);
        });
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
        assertEquals(epic, manager.getEpic(epicId));
        assertEquals(subtask, manager.getSubtask(subId));

        FileBackedTaskManager secondManager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(task, secondManager.getTask(taskId));
        assertEquals(epic, secondManager.getEpic(epicId));
        assertEquals(subtask, secondManager.getSubtask(subId));
    }
}
