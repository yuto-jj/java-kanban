package managers;

import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File tasksInFile;
    private static final String HEADER = "id,type,name,status,description,epic,start,duration,end";
    protected static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

    public FileBackedTaskManager(File tasksInFile) {
        super();
        this.tasksInFile = tasksInFile;
    }

    private void save() {
        try (BufferedWriter buffWriter = new BufferedWriter(new FileWriter(tasksInFile))) {
            buffWriter.write(HEADER + "\n");

            for (Task task : prioritizedTasks) {
                switch (task.getType()) {
                    case TASK:
                        buffWriter.write(taskToString(task) + "\n");
                        break;
                    case EPIC:
                        Epic epic = (Epic) task;
                        buffWriter.write(epicToString(epic) + "\n");
                        break;
                    case SUBTASK:
                        Subtask sub = (Subtask) task;
                        buffWriter.write(subToString(sub) + "\n");
                        break;
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }


    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            String tasks = Files.readString(file.toPath());
            String[] tasksArray = tasks.split("\n");
            for (String str : tasksArray) {
                if (str.equals(tasksArray[0])) {
                    continue;
                }
                Task task = fromString(str);
                if (task.getId() > manager.id) {
                    manager.id = task.getId();
                }
                switch (task.getType()) {
                    case TASK:
                        manager.tasks.put(task.getId(), task);
                        manager.prioritizedTasks.add(task);
                        break;
                    case EPIC:
                        Epic epic = (Epic) task;
                        manager.epics.put(task.getId(), epic);
                        manager.prioritizedTasks.add(epic);
                        break;
                    case SUBTASK:
                        Subtask subtask = (Subtask) task;
                        manager.subtasks.put(task.getId(), subtask);
                        manager.prioritizedTasks.add(subtask);
                        break;
                }
            }
            for (Subtask subtask : manager.subtasks.values()) {
                Epic epicSub = manager.epics.get(subtask.getEpicId());
                epicSub.addSubId(subtask.getId());
            }
        } catch (IOException e) {
            throw new ManagerLoadException(e.getMessage());
        }
        return manager;
    }

    private static Task fromString(String str) {
            Task task = null;
            String[] tasksArray = str.split(",");
            int id = Integer.parseInt(tasksArray[0]);
            Type type = Type.valueOf(tasksArray[1]);
            String name = tasksArray[2];
            Status status = Status.valueOf(tasksArray[3]);
            String description = tasksArray[4];
            String startTime;
            int duration;
            int epicId = 0;
            if (tasksArray.length > 8) {
                epicId = Integer.parseInt(tasksArray[5]);
                startTime = tasksArray[6];
                duration = Integer.parseInt(tasksArray[7]);
            } else {
                startTime = tasksArray[5];
                duration = Integer.parseInt(tasksArray[6]);
            }
            switch (type) {
                case TASK:
                    task = new Task(id, name, description, status, startTime, duration);
                    break;
                case EPIC:
                    task = new Epic(id, name, description);
                    task.setStatus(status);
                    task.setStartTime(startTime);
                    task.setDuration(duration);
                    Epic epic = (Epic) task;
                    epic.setEndTime(tasksArray[7]);

                    break;
                case SUBTASK:
                    task = new Subtask(id, name, description, status, epicId, startTime, duration);
                    break;
            }
            return task;
    }


    @Override
    public int addTask(Task task) {
        int id = super.addTask(task);
        save();
        return id;
    }

    @Override
    public int addEpic(Epic epic) {
        int id = super.addEpic(epic);
        save();
        return id;
    }

    @Override
    public int addSubtask(Subtask subtask) {
        int id = super.addSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void removeTasks() {
        super.removeTasks();
        save();
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
        save();
    }

    @Override
    public void removeSubtasks() {
        super.removeSubtasks();
        save();
    }

    @Override
    public void taskUpdate(Task task) {
        super.taskUpdate(task);
        save();
    }

    @Override
    public void epicUpdate(Epic epic) {
        super.epicUpdate(epic);
        save();
    }

    @Override
    public void subtaskUpdate(Subtask subtask) {
        super.subtaskUpdate(subtask);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    private String taskToString(Task task) {
        return task.getId() + "," +
                task.getType() + "," +
                task.getName() + "," +
                task.getStatus() + "," +
                task.getDescription() + "," +
                task.getStartTime().format(DATE_TIME_FORMATTER) + "," +
                task.getDuration().toMinutes() + "," +
                task.getEndTime().format(DATE_TIME_FORMATTER);
    }

    private String epicToString(Epic epic) {
        return epic.getId() + "," +
                epic.getType() + "," +
                epic.getName() + "," +
                epic.getStatus() + "," +
                epic.getDescription() + "," +
                epic.getStartTime().format(DATE_TIME_FORMATTER) + "," +
                epic.getDuration().toMinutes() + "," +
                epic.getEndTime().format(DATE_TIME_FORMATTER);
    }

    private String subToString(Subtask sub) {
        return sub.getId() + "," +
                sub.getType() + "," +
                sub.getName() + "," +
                sub.getStatus() + "," +
                sub.getDescription() + "," +
                sub.getEpicId() + "," +
                sub.getStartTime().format(DATE_TIME_FORMATTER) + "," +
                sub.getDuration().toMinutes() + "," +
                sub.getEndTime().format(DATE_TIME_FORMATTER);

    }
}


