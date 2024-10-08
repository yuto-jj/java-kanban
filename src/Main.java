import managers.FileBackedTaskManager;
import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("1. Задача", "1. Задача", LocalDateTime.of(2024, 9, 10, 11, 30), 90);
        int t1 = taskManager.addTask(task1);
        Task task2 = new Task("2. Задача", "2. Задача", LocalDateTime.of(2024, 9, 11, 11, 30), 90);
        int t2 = taskManager.addTask(task2);
        Epic epic1 = new Epic("1. Эпик", "1. Эпик");
        int e1 = taskManager.addEpic(epic1);
        Subtask subtask1 = new Subtask("1. Подзадача", "1. Эпик. 1. Подзадача", e1,
                LocalDateTime.of(2024, 9, 12, 11, 30), 90);
        int s1 = taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("2. Подзадача", "1. Эпик. 2. Подзадача", e1,
                LocalDateTime.of(2024, 9, 13, 11, 30), 90);
        int s2 = taskManager.addSubtask(subtask2);
        Epic epic2 = new Epic("2. Эпик", "2. Эпик");
        int e2 = taskManager.addEpic(epic2);
        Subtask subtask3 = new Subtask("1. Подзадача", "2. Эпик. 1. Подзадача", e2,
                LocalDateTime.of(2024, 9, 14, 11, 30), 90);
        int s3 = taskManager.addSubtask(subtask3);

        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
        System.out.println(taskManager.getSubtasksByEpicId(e1));

        Task task3 = new Task(t1, "1. Задача", "1. Задача - статус изменен", Status.IN_PROGRESS,
                LocalDateTime.of(2024, 9, 15, 11, 30), 90);
        taskManager.taskUpdate(task3);

        Task task4 = new Task(t2, "2. Задача", "2. Задача - статус изменен", Status.DONE,
                LocalDateTime.of(2024, 9, 16, 11, 30), 90);
        taskManager.taskUpdate(task4);

        System.out.println(taskManager.getTask(t1));
        printAllTasks(taskManager);
        System.out.println(taskManager.getTask(t2));
        printAllTasks(taskManager);

        Epic epic3 = new Epic(e1, "1. Эпик", "1. Эпик - изменен");
        taskManager.epicUpdate(epic3);
        System.out.println(taskManager.getEpic(e1));
        printAllTasks(taskManager);

        Subtask subtask4 = new Subtask(s1, "1. Подзадача",
                "1. Эпик. 1. Подзадача - статус изменен", Status.DONE, e1,
                LocalDateTime.of(2024, 9, 17, 11, 30), 90);
        taskManager.subtaskUpdate(subtask4);
        System.out.println(taskManager.getSubtask(s1));
        printAllTasks(taskManager);

        Subtask subtask5 = new Subtask(s2, "2. Подзадача",
                "1. Эпик. 2. Подзадача - статус изменен", Status.IN_PROGRESS, e1,
                LocalDateTime.of(2024, 9, 18, 11, 30), 90);
        taskManager.subtaskUpdate(subtask5);
        System.out.println(taskManager.getSubtask(s2));
        printAllTasks(taskManager);
        System.out.println(taskManager.getSubtasksByEpicId(e1));

        Subtask subtask6 = new Subtask(s3, "1. Подзадача",
                "2. Эпик. 1. Подзадача - статус изменен", Status.DONE, e2,
                LocalDateTime.of(2024, 9, 19, 11, 30), 90);
        taskManager.subtaskUpdate(subtask6);
        System.out.println(taskManager.getSubtasksByEpicId(e2));

        System.out.println(taskManager.getEpics());

        taskManager.removeTask(t1);
        System.out.println(taskManager.getTasks());

        taskManager.removeEpic(e1);
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());

        Task task5 = new Task("Задача для истории", "Описание для истории",
                LocalDateTime.of(2024, 9, 20, 11, 30), 90);
        int t5 = taskManager.addTask(task5);
        taskManager.getTask(t5);
        taskManager.getTask(t2);
        printAllTasks(taskManager);

        taskManager.removeTasks();
        printAllTasks(taskManager);

        taskManager.getEpic(e2);
        printAllTasks(taskManager);

        File file = new File("resources/tasks.csv");

        FileBackedTaskManager fileManager = new FileBackedTaskManager(file);
        Task task = new Task("Первая задача", "Описание - 1",
                LocalDateTime.of(2024, 9, 21, 11, 30), 90);
        fileManager.addTask(task);

        Epic epic = new Epic("Первый эпик", "Опсание - 2");
        int epicId = fileManager.addEpic(epic);

        Subtask subtask = new Subtask("Первая подзадача", "Опсание - 3", epicId,
                LocalDateTime.of(2024, 9, 22, 11, 30), 90);
        fileManager.addSubtask(subtask);
        Epic epic33 = new Epic("1", "2");
        Epic epic44 = new Epic("2", "3");
        fileManager.addEpic(epic33);
        fileManager.addEpic(epic44);

        System.out.println("Текущая рабочая директория: " + System.getProperty("user.dir"));

        FileBackedTaskManager fileManager2 = FileBackedTaskManager.loadFromFile(file);
        Task task20 = new Task("Вторая задача", "Описание - 4",
                LocalDateTime.of(2024, 9, 23, 11, 30), 90);
        Task task21 = new Task("Вторая задача", "Описание - 4",
                LocalDateTime.of(2024, 9, 3, 11, 30), 90);
        fileManager2.addTask(task20);
        fileManager2.addTask(task21);
        printAllTasks(fileManager2);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpics()) {
            System.out.println(epic);

            for (Task task : manager.getSubtasksByEpicId(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }

        System.out.println("Задачи по приоритету:");
        manager.getPrioritizedTasks().forEach(System.out::println);
        System.out.println("--------");
    }
}
