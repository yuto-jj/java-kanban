package managers;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int id = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>() ;
    private final HashMap<Integer, Epic> epics = new HashMap<>() ;
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public int addTask(Task task) {
            id++;
            task.setId(id);
            tasks.put(task.getId(), task);
            return task.getId();
    }

    public int addSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        Epic epicSubtask = epics.get(epicId);

        if (epicSubtask != null) {
            id++;
            subtask.setId(id);
            int subId = subtask.getId();
            subtasks.put(subId, subtask);
            epicSubtask.addSubId(subId);
            epicStatusUpdate(epicId);
        }
        return subtask.getId();
    }

    private void epicStatusUpdate(int epicId) {
        int numberOfNew = 0;
        int numberOfDone = 0;
        Epic epic = epics.get(epicId);
        ArrayList<Integer> subsId = epic.getSubsId();

        if (!subsId.isEmpty()) {
            for (Integer subId : subsId) {
                Subtask sub = subtasks.get(subId);
                if (sub.getStatus() == Status.DONE) {
                    numberOfDone++;
                } else if (sub.getStatus() == Status.NEW) {
                    numberOfNew++;
                }
            }
            if (numberOfDone == subsId.size()) {
                epic.setStatus(Status.DONE);
            } else if (numberOfNew == subsId.size()) {
                epic.setStatus(Status.NEW);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        } else {
            epic.setStatus(Status.NEW);
        }
    }

    public int addEpic(Epic epic) {
            id++;
            epic.setId(id);
            epics.put(epic.getId(), epic);
            return epic.getId();
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public void removeTasks() {
        tasks.clear();
    }

    public void removeSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubsId().clear();
            epicStatusUpdate(epic.getId());
        }
    }

    public void removeEpics() {
        epics.clear();
        subtasks.clear();
    }

    public Task getTask(int taskId) {
        return tasks.get(taskId);
    }

    public Subtask getSubtask(int subId) {
        return subtasks.get(subId);
    }

    public Epic getEpic(int epicId) {
        return epics.get(epicId);
    }

    public void taskUpdate(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void subtaskUpdate(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId()) && epics.containsKey(subtask.getEpicId())) {
            subtasks.put(subtask.getId(), subtask);
            epicStatusUpdate(subtask.getEpicId());
        }
    }

    public void epicUpdate(Epic epic) {
        Epic newEpic = epics.get(epic.getId());
        if (epics.containsKey(epic.getId())) {
            newEpic.setName(epic.getName());
            newEpic.setDescription(epic.getDescription());
        }
    }

    public ArrayList<Subtask> getSubtasksByEpicId(int epicId) {
        ArrayList<Subtask> listOfSubtasks = new ArrayList<>();
        Epic epic = epics.get(epicId);
        ArrayList<Integer> subsId = epic.getSubsId();
        for (Integer subId : subsId) {
            Subtask sub = subtasks.get(subId);
            listOfSubtasks.add(sub);
        }
        return listOfSubtasks;
    }

    public void removeTask(int taskId) {
        tasks.remove(taskId);
    }

    public void removeSubtask(int subId) {
        if (subtasks.containsKey(subId)) {
            Subtask sub = subtasks.get(subId);
            int epicId = sub.getEpicId();
            Epic epic = epics.get(epicId);
            subtasks.remove(subId);
            epic.removeSubId(subId);
            epicStatusUpdate(epicId);
        }
    }

    public void removeEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            ArrayList<Integer> subsId = new ArrayList<>(epics.get(epicId).getSubsId());
            for (Integer subId : subsId) {
                removeSubtask(subId);
            }
            epics.remove(epicId);
        }
    }
}
