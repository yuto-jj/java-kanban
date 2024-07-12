import managers.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task task1 = new Task("1. Задача", "1. Задача");
        int t1 = taskManager.addTask(task1);
        Task task2 = new Task("2. Задача", "2. Задача");
        int t2 = taskManager.addTask(task2);
        Epic epic1 = new Epic("1. Эпик", "1. Эпик");
        int e1 = taskManager.addEpic(epic1);
        Subtask subtask1 = new Subtask("1. Подзадача", "1. Эпик. 1. Подзадача", e1);
        int s1 = taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("2. Подзадача", "1. Эпик. 2. Подзадача", e1);
        int s2 = taskManager.addSubtask(subtask2);
        Epic epic2 = new Epic("2. Эпик", "2. Эпик");
        int e2 = taskManager.addEpic(epic2);
        Subtask subtask3 = new Subtask("1. Подзадача", "2. Эпик. 1. Подзадача", e2);
        int s3 = taskManager.addSubtask(subtask3);

        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
        System.out.println(taskManager.getSubtasksByEpicId(e1));

        Task task3 = new Task(t1, "1. Задача", "1. Задача - статус изменен", Status.IN_PROGRESS);
        taskManager.taskUpdate(task3);
        Task task4 = new Task(t2, "2. Задача", "2. Задача - статус изменен", Status.DONE);
        taskManager.taskUpdate(task4);
        System.out.println(taskManager.getTask(t1));
        System.out.println(taskManager.getTask(t2));

        Epic epic3 = new Epic(e1, "1. Эпик", "1. Эпик - изменен");
        taskManager.epicUpdate(epic3);
        System.out.println(taskManager.getEpic(e1));

        Subtask subtask4 = new Subtask(s1, "1. Подзадача",
                "1. Эпик. 1. Подзадача - статус изменен", Status.DONE, e1);
        taskManager.subtaskUpdate(subtask4);
        System.out.println(taskManager.getSubtask(s1));
        Subtask subtask5 = new Subtask(s2, "2. Подзадача",
                "1. Эпик. 2. Подзадача - статус изменен", Status.IN_PROGRESS, e1);
        taskManager.subtaskUpdate(subtask5);
        System.out.println(taskManager.getSubtask(s2));
        System.out.println(taskManager.getSubtasksByEpicId(e1));

        Subtask subtask6 = new Subtask(s3,"1. Подзадача",
                "2. Эпик. 1. Подзадача - статус изменен", Status.DONE, e2);
        taskManager.subtaskUpdate(subtask6);
        System.out.println(taskManager.getSubtasksByEpicId(e2));

        System.out.println(taskManager.getEpics());

        taskManager.removeTask(t1);
        System.out.println(taskManager.getTasks());

        taskManager.removeEpic(e1);
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());

    }
}
