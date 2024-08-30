package managers;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    protected int id = 0;
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public int addTask(Task task) {
            id++;
            task.setId(id);
            tasks.put(task.getId(), task);
            return task.getId();
    }

    @Override
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

    @Override
    public int addEpic(Epic epic) {
            id++;
            epic.setId(id);
            epics.put(epic.getId(), epic);
            return epic.getId();
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeTasks() {
        for (Integer taskId : tasks.keySet()) {
            historyManager.remove(taskId);
        }
        tasks.clear();
    }

    @Override
    public void removeSubtasks() {
        for (Integer subId : subtasks.keySet()) {
            historyManager.remove(subId);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubsId().clear();
            epicStatusUpdate(epic.getId());
        }
    }

    @Override
    public void removeEpics() {
        for (Integer epicId : epics.keySet()) {
            historyManager.remove(epicId);
        }
        for (Integer subId : subtasks.keySet()) {
            historyManager.remove(subId);
        }
        epics.clear();
        subtasks.clear();

    }

    @Override
    public Task getTask(int taskId) {
        historyManager.add(tasks.get(taskId));
        return tasks.get(taskId);
    }

    @Override
    public Subtask getSubtask(int subId) {
        historyManager.add(subtasks.get(subId));
        return subtasks.get(subId);
    }

    @Override
    public Epic getEpic(int epicId) {
        historyManager.add(epics.get(epicId));
        return epics.get(epicId);
    }

    @Override
    public void taskUpdate(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void subtaskUpdate(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId()) && epics.containsKey(subtask.getEpicId())) {
            subtasks.put(subtask.getId(), subtask);
            epicStatusUpdate(subtask.getEpicId());
        }
    }

    @Override
    public void epicUpdate(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic newEpic = epics.get(epic.getId());
            newEpic.setName(epic.getName());
            newEpic.setDescription(epic.getDescription());
        }
    }

    @Override
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

    @Override
    public void removeTask(int taskId) {
        tasks.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override
    public void removeSubtask(int subId) {
        if (subtasks.containsKey(subId)) {
            Subtask sub = subtasks.get(subId);
            int epicId = sub.getEpicId();
            Epic epic = epics.get(epicId);
            subtasks.remove(subId);
            epic.removeSubId(subId);
            epicStatusUpdate(epicId);
            historyManager.remove(subId);
        }
    }

    @Override
    public void removeEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            ArrayList<Integer> subsId = new ArrayList<>(epics.get(epicId).getSubsId());
            for (Integer subId : subsId) {
                subtasks.remove(subId);
                historyManager.remove(subId);
            }
            epics.remove(epicId);
            historyManager.remove(epicId);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
