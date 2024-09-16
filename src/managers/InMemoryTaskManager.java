package managers;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int id = 0;
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private boolean timeValidation(Task task) {
    return prioritizedTasks.stream().filter(t -> !(t.equals(task))).noneMatch((t) -> (task.getStartTime().isBefore(t.getEndTime()) &&
                task.getEndTime().isAfter(t.getStartTime())));
    }

    @Override
    public int addTask(Task task) {
        if (timeValidation(task)) {
            id++;
            task.setId(id);
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
            return task.getId();
        }
        return 0;
    }

    @Override
    public int addSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        Epic epicSubtask = epics.get(epicId);

        if (epicSubtask != null && timeValidation(subtask)) {
            id++;
            subtask.setId(id);
            int subId = subtask.getId();
            subtasks.put(subId, subtask);
            prioritizedTasks.add(subtask);
            epicSubtask.addSubId(subId);
            epicUpdate(epicId);
            return subtask.getId();
        }
        return 0;
    }

    private void epicUpdate(int epicId) {
        int numberOfNew = 0;
        int numberOfDone = 0;
        Epic epic = epics.get(epicId);
        LocalDateTime sTime = null;
        LocalDateTime eTime = null;
        Duration duration = Duration.ZERO;
        ArrayList<Integer> subsId = epic.getSubsId();

        if (!subsId.isEmpty()) {
            for (int subId : subsId) {
                Subtask sub = subtasks.get(subId);
                if (subId == subsId.getFirst()) {
                    sTime = sub.getStartTime();
                    eTime = sub.getEndTime();
                    duration = sub.getDuration();
                }  else {
                    duration = duration.plus(sub.getDuration());
                    if (sub.getStartTime().isBefore(sTime)) {
                        sTime = sub.getStartTime();
                    }
                    if (sub.getEndTime().isAfter(eTime)) {
                        eTime = sub.getEndTime();
                    }
                }
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
        epic.setStartTime(sTime);
        epic.setEndTime(eTime);
        epic.setDuration(duration);
    }

    @Override
    public int addEpic(Epic epic) {
            id++;
            epic.setId(id);
            epics.put(epic.getId(), epic);
            epic.setStartTime(null);
            epic.setEndTime(null);
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
        tasks.keySet().forEach((t) -> {
            historyManager.remove(t);
            prioritizedTasks.remove(tasks.get(t));
        });
        tasks.clear();
    }

    @Override
    public void removeSubtasks() {
        subtasks.keySet().forEach((t) -> {
            historyManager.remove(t);
            prioritizedTasks.remove(subtasks.get(t));
        });
        subtasks.clear();
        epics.values().forEach((e) -> {
            e.getSubsId().clear();
            epicUpdate(e.getId());
        });
    }

    @Override
    public void removeEpics() {
        epics.keySet().forEach(historyManager::remove);
        subtasks.keySet().forEach((t) -> {
            historyManager.remove(t);
            prioritizedTasks.remove(subtasks.get(t));
        });
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
        if (tasks.containsKey(task.getId()) && timeValidation(task)) {
                prioritizedTasks.remove(tasks.get(task.getId()));
                tasks.put(task.getId(), task);
                prioritizedTasks.add(task);
        }
    }

    @Override
    public void subtaskUpdate(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId()) && epics.containsKey(subtask.getEpicId()) &&
                timeValidation(subtask)) {
                prioritizedTasks.remove(subtasks.get(subtask.getId()));
                subtasks.put(subtask.getId(), subtask);
                prioritizedTasks.add(subtask);
                epicUpdate(subtask.getEpicId());
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
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            return epic.getSubsId().stream().map(subtasks::get)
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        return new ArrayList<>();
    }

    @Override
    public void removeTask(int taskId) {
        if (tasks.containsKey(taskId)) {
            prioritizedTasks.remove(tasks.get(taskId));
            tasks.remove(taskId);
            historyManager.remove(taskId);
        }
    }

    @Override
    public void removeSubtask(int subId) {
        if (subtasks.containsKey(subId)) {
            Subtask sub = subtasks.get(subId);
            int epicId = sub.getEpicId();
            Epic epic = epics.get(epicId);
            prioritizedTasks.remove(subtasks.get(subId));
            subtasks.remove(subId);
            epic.removeSubId(subId);
            epicUpdate(epicId);
            historyManager.remove(subId);
        }
    }

    @Override
    public void removeEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            ArrayList<Integer> subsId = new ArrayList<>(epics.get(epicId).getSubsId());
            for (Integer subId : subsId) {
                removeSubtask(subId);
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
