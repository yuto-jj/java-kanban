package tasks;

import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

        Subtask sub1 = new Subtask("Подзадача", "Тест с1", e, "11:30 11.09.2024", 90);
        Subtask sub2 = new Subtask("Подзадача", "Тест с2", e, "11:30 12.09.2024", 90);
        Subtask sub3 = new Subtask("Подзадача", "Тест с3", e, "11:30 13.09.2024", 90);
        int s1 = taskManager.addSubtask(sub1);
        int s2 = taskManager.addSubtask(sub2);
        int s3 = taskManager.addSubtask(sub3);

        assertEquals(epic.getStatus(), Status.NEW);

        Subtask sub4 = new Subtask(s1,"Подзадача", "Тест с1", Status.IN_PROGRESS, e,
                "11:30 14.09.2024", 90);
        Subtask sub5 = new Subtask(s2,"Подзадача", "Тест с2", Status.IN_PROGRESS, e,
                "11:30 15.09.2024", 90);
        Subtask sub6 = new Subtask(s3, "Подзадача", "Тест с3", Status.IN_PROGRESS, e,
                "11:30 16.09.2024", 90);
        taskManager.subtaskUpdate(sub4);
        taskManager.subtaskUpdate(sub5);
        taskManager.subtaskUpdate(sub6);

        assertEquals(epic.getStatus(), Status.IN_PROGRESS);

        Subtask sub7 = new Subtask(s1, "Подзадача", "Тест с3", Status.DONE, e,
                "11:30 17.09.2024", 90);
        Subtask sub8 = new Subtask(s2, "Подзадача", "Тест с3", Status.DONE, e,
                "11:30 18.09.2024", 90);
        Subtask sub9 = new Subtask(s3, "Подзадача", "Тест с3", Status.DONE, e,
                "11:30 19.09.2024", 90);
        taskManager.subtaskUpdate(sub7);
        taskManager.subtaskUpdate(sub8);
        taskManager.subtaskUpdate(sub9);

        assertEquals(epic.getStatus(), Status.DONE);

        Subtask sub10 = new Subtask(s3, "Подзадача", "Тест с3", Status.NEW, e,
                "11:30 19.09.2024", 90);
        taskManager.subtaskUpdate(sub10);

        assertEquals(epic.getStatus(), Status.IN_PROGRESS);
    }


}