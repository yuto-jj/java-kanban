package managers;

import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.nio.file.Files;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File tasksInFile;
    private static final String HEADER = "id,type,name,status,description,epic";

    public FileBackedTaskManager(File tasksInFile) {
        super();
        this.tasksInFile = tasksInFile;
    }

    private void save() {
        try (BufferedWriter buffWriter = new BufferedWriter(new FileWriter(tasksInFile))) {
            buffWriter.write(HEADER + "\n");
            for (Task task : tasks.values()) {
                buffWriter.write(taskToString(task) + "\n");
            }
            for (Epic epic : epics.values()) {
                buffWriter.write(epicToString(epic) + "\n");
            }
            for (Subtask subtask : subtasks.values()) {
                buffWriter.write(subToString(subtask) + "\n");
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
                        break;
                    case EPIC:
                        manager.epics.put(task.getId(), (Epic) task);
                        break;
                    case SUBTASK:
                        Subtask subtask = (Subtask) task;
                        manager.subtasks.put(task.getId(), subtask);
                        Epic epicSub = manager.epics.get(subtask.getEpicId());
                        epicSub.addSubId(subtask.getId());
                        break;
                }
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
            int epicId = 0;
            if (tasksArray.length > 5) {
                epicId = Integer.parseInt(tasksArray[5]);
            }
            switch (type) {
                case TASK:
                    task = new Task(id, name, description, status);
                    break;
                case EPIC:
                    task = new Epic(id, name, description);
                    task.setStatus(status);
                    break;
                case SUBTASK:
                    task = new Subtask(id, name, description, status, epicId);
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
                task.getDescription();
    }

    private String epicToString(Epic epic) {
        return epic.getId() + "," +
                epic.getType() + "," +
                epic.getName() + "," +
                epic.getStatus() + "," +
                epic.getDescription();
    }

    private String subToString(Subtask sub) {
        return sub.getId() + "," +
                sub.getType() + "," +
                sub.getName() + "," +
                sub.getStatus() + "," +
                sub.getDescription() + "," +
                sub.getEpicId();
    }
}


