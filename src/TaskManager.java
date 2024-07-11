import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static int id;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.id = 0;
    }

    public int addTask(Task task) {
            id++;
            task.setId(id);
            tasks.put(task.getId(), task);
            return task.getId();
    }

    public int addSubtask(Subtask subtask) {
        int subId;
        int epicId = subtask.getEpicId();
        Epic epicSubtask = epics.get(epicId);

        if (epicSubtask != null) {
            id++;
            subtask.setId(id);
            subId = subtask.getId();
            subtasks.put(subId, subtask);
            epicSubtask.addSubId(subId);
            epicStatusUpdate(epicId);
        }
        return subtask.getId();
    }

    public void epicStatusUpdate(int epicId) {
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
        ArrayList<Task> listOfTasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            listOfTasks.add(task);
        }
        return listOfTasks;
    }

    public ArrayList<Subtask> getSubtasks() {
        ArrayList<Subtask> listOfSubtasks = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            listOfSubtasks.add(subtask);
        }
        return listOfSubtasks;
    }

    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> listOfEpics = new ArrayList<>();
        for (Epic epic : epics.values()) {
            listOfEpics.add(epic);
        }
        return listOfEpics;
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
        tasks.put(task.getId(), task);
    }

    public void subtaskUpdate(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        epicStatusUpdate(subtask.getEpicId());
    }

    public void epicUpdate(Epic epic) {
        Epic newEpic = epics.get(epic.getId());
        newEpic.setName(epic.getName());
        newEpic.setDescription(epic.getDescription());
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
        Subtask sub = subtasks.get(subId);
        int epicId = sub.getEpicId();
        Epic epic = epics.get(epicId);
        subtasks.remove(subId);
        epic.removeSubId(subId);
    }

    public void removeEpic(int epicId) {
        ArrayList<Integer> subsId = new ArrayList<>(epics.get(epicId).getSubsId());
        for (Integer subId : subsId) {
            removeSubtask(subId);
        }
        epics.remove(epicId);
    }
}
