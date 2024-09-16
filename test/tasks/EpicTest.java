package tasks;

import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void mustHaveASubtaskDependentStatus() {
        Epic epic = new Epic("Эпик", "Тест эпика");
        int e = taskManager.addEpic(epic);

        Subtask sub1 = new Subtask("Подзадача", "Тест с1", e,
                LocalDateTime.of(2024, 9, 11, 11, 30), 90);
        Subtask sub2 = new Subtask("Подзадача", "Тест с2", e,
                LocalDateTime.of(2024, 9, 12, 11, 30), 90);
        Subtask sub3 = new Subtask("Подзадача", "Тест с3", e,
                LocalDateTime.of(2024, 9, 13, 11, 30), 90);
        int s1 = taskManager.addSubtask(sub1);
        int s2 = taskManager.addSubtask(sub2);
        int s3 = taskManager.addSubtask(sub3);

        assertEquals(epic.getStatus(), Status.NEW);

        Subtask sub4 = new Subtask(s1, "Подзадача", "Тест с1", Status.IN_PROGRESS, e,
                LocalDateTime.of(2024, 9, 14, 11, 30), 90);
        Subtask sub5 = new Subtask(s2, "Подзадача", "Тест с2", Status.IN_PROGRESS, e,
                LocalDateTime.of(2024, 9, 15, 11, 30), 90);
        Subtask sub6 = new Subtask(s3, "Подзадача", "Тест с3", Status.IN_PROGRESS, e,
                LocalDateTime.of(2024, 9, 16, 11, 30), 90);
        taskManager.subtaskUpdate(sub4);
        taskManager.subtaskUpdate(sub5);
        taskManager.subtaskUpdate(sub6);

        assertEquals(epic.getStatus(), Status.IN_PROGRESS);

        Subtask sub7 = new Subtask(s1, "Подзадача", "Тест с3", Status.DONE, e,
                LocalDateTime.of(2024, 9, 17, 11, 30), 90);
        Subtask sub8 = new Subtask(s2, "Подзадача", "Тест с3", Status.DONE, e,
                LocalDateTime.of(2024, 9, 18, 11, 30), 90);
        Subtask sub9 = new Subtask(s3, "Подзадача", "Тест с3", Status.DONE, e,
                LocalDateTime.of(2024, 9, 19, 11, 30), 90);
        taskManager.subtaskUpdate(sub7);
        taskManager.subtaskUpdate(sub8);
        taskManager.subtaskUpdate(sub9);

        assertEquals(epic.getStatus(), Status.DONE);

        Subtask sub10 = new Subtask(s3, "Подзадача", "Тест с3", Status.NEW, e,
                LocalDateTime.of(2024, 9, 19, 11, 30), 90);
        taskManager.subtaskUpdate(sub10);

        assertEquals(epic.getStatus(), Status.IN_PROGRESS);
    }
}