package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    int addTask(Task task);

    int addSubtask(Subtask subtask);

    int addEpic(Epic epic);

    ArrayList<Task> getTasks();

    ArrayList<Subtask> getSubtasks();

    ArrayList<Epic> getEpics();

    void removeTasks();

    void removeSubtasks();

    void removeEpics();

    Task getTask(int taskId);

    Subtask getSubtask(int subId);

    Epic getEpic(int epicId);

    void taskUpdate(Task task);

    void subtaskUpdate(Subtask subtask);

    void epicUpdate(Epic epic);

    ArrayList<Subtask> getSubtasksByEpicId(int epicId);

    void removeTask(int taskId);

    void removeSubtask(int subId);

    void removeEpic(int epicId);

    List<Task> getHistory();
}
