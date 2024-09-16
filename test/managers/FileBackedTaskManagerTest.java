package managers;

import exceptions.ManagerLoadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    File file;

    @BeforeEach
    void beforeEach() throws IOException {
        file = File.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(file);
        task = new Task("Задача - 1", "Описание - 1",
                LocalDateTime.of(2024, 9, 12, 11, 30), 90);
        tId = manager.addTask(task);
        epic = new Epic("Эпик - 1", "Описание - 1");
        eId = manager.addEpic(epic);
        sub = new Subtask("Подзадача - 1", "Описание - 1", eId,
                LocalDateTime.of(2024, 9, 13, 11, 30), 90);
        sId = manager.addSubtask(sub);
        savedTask = manager.getTask(tId);
        savedEpic = manager.getEpic(eId);
        savedSub = manager.getSubtask(sId);
    }


    @Test
    void mustLoadAnEmptyFile() throws IOException {
        File file1 = File.createTempFile("empty", ".csv");
        assertDoesNotThrow(() -> FileBackedTaskManager.loadFromFile(file1));
        FileBackedTaskManager secondManager = FileBackedTaskManager.loadFromFile(file1);
        assertNotNull(secondManager);
    }

    @Test
    void mustSaveTasksToNotExistFile() {
        File file1 = new File("resources/123.csv");
        FileBackedTaskManager secondManager = new FileBackedTaskManager(file1);
        int t1Id = secondManager.addTask(task);
        assertDoesNotThrow(() -> FileBackedTaskManager.loadFromFile(file1));
        FileBackedTaskManager thirdManager = FileBackedTaskManager.loadFromFile(file1);
        assertEquals(secondManager.getTask(t1Id), thirdManager.getTask(t1Id));
    }

    @Test
    void mustNotLoadTasksFromNotExistFile() {
        File file1 = new File("resources/456.csv");
        assertThrows(ManagerLoadException.class, () -> FileBackedTaskManager.loadFromFile(file1));
    }

    @Test
    void mustSaveTasksInFileAndLoadFromFile() {
        assertEquals(task, manager.getTask(tId));
        assertEquals(task.getName(), manager.getTask(tId).getName());
        assertEquals(task.getDescription(), manager.getTask(tId).getDescription());
        assertEquals(task.getStatus(), manager.getTask(tId).getStatus());
        assertEquals(task.getStartTime(), manager.getTask(tId).getStartTime());
        assertEquals(task.getDuration(), manager.getTask(tId).getDuration());

        assertEquals(epic, manager.getEpic(eId));
        assertEquals(epic.getName(), manager.getEpic(eId).getName());
        assertEquals(epic.getDescription(), manager.getEpic(eId).getDescription());
        assertEquals(epic.getStatus(), manager.getEpic(eId).getStatus());
        assertEquals(epic.getSubsId(), manager.getEpic(eId).getSubsId());
        assertEquals(epic.getStartTime(), manager.getEpic(eId).getStartTime());
        assertEquals(epic.getDuration(), manager.getEpic(eId).getDuration());
        assertEquals(epic.getEndTime(), manager.getEpic(eId).getEndTime());

        assertEquals(sub, manager.getSubtask(sId));
        assertEquals(sub.getName(), manager.getSubtask(sId).getName());
        assertEquals(sub.getDescription(), manager.getSubtask(sId).getDescription());
        assertEquals(sub.getStatus(), manager.getSubtask(sId).getStatus());
        assertEquals(sub.getEpicId(), manager.getSubtask(sId).getEpicId());
        assertEquals(sub.getStartTime(), manager.getSubtask(sId).getStartTime());
        assertEquals(sub.getDuration(), manager.getSubtask(sId).getDuration());

        assertDoesNotThrow(() -> FileBackedTaskManager.loadFromFile(file));
        FileBackedTaskManager secondManager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(manager.getTask(tId), secondManager.getTask(tId));
        assertEquals(manager.getTask(tId).getName(), secondManager.getTask(tId).getName());
        assertEquals(manager.getTask(tId).getDescription(), secondManager.getTask(tId).getDescription());
        assertEquals(manager.getTask(tId).getStatus(), secondManager.getTask(tId).getStatus());
        assertEquals(manager.getTask(tId).getStartTime(), secondManager.getTask(tId).getStartTime());
        assertEquals(manager.getTask(tId).getDuration(), secondManager.getTask(tId).getDuration());

        assertEquals(manager.getEpic(eId), secondManager.getEpic(eId));
        assertEquals(manager.getEpic(eId).getName(), secondManager.getEpic(eId).getName());
        assertEquals(manager.getEpic(eId).getDescription(), secondManager.getEpic(eId).getDescription());
        assertEquals(manager.getEpic(eId).getStatus(), secondManager.getEpic(eId).getStatus());
        assertEquals(manager.getEpic(eId).getSubsId(), secondManager.getEpic(eId).getSubsId());
        assertEquals(manager.getEpic(eId).getStartTime(), secondManager.getEpic(eId).getStartTime());
        assertEquals(manager.getEpic(eId).getDuration(), secondManager.getEpic(eId).getDuration());
        assertEquals(manager.getEpic(eId).getEndTime(), secondManager.getEpic(eId).getEndTime());

        assertEquals(manager.getSubtask(sId), secondManager.getSubtask(sId));
        assertEquals(manager.getSubtask(sId).getName(), secondManager.getSubtask(sId).getName());
        assertEquals(manager.getSubtask(sId).getDescription(), secondManager.getSubtask(sId).getDescription());
        assertEquals(manager.getSubtask(sId).getStatus(), secondManager.getSubtask(sId).getStatus());
        assertEquals(manager.getSubtask(sId).getEpicId(), secondManager.getSubtask(sId).getEpicId());
        assertEquals(manager.getSubtask(sId).getStartTime(), secondManager.getSubtask(sId).getStartTime());
        assertEquals(manager.getSubtask(sId).getDuration(), secondManager.getSubtask(sId).getDuration());

        assertEquals(manager.getPrioritizedTasks(), secondManager.getPrioritizedTasks());
    }
}
