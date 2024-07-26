package tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subsId = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(int newId, String newName, String newDescription) {
        super(newName, newDescription);
        this.id = newId;
    }

    public void addSubId(int newId) {
        subsId.add(newId);
    }

    public ArrayList<Integer> getSubsId() {
        return subsId;
    }

    public void removeSubId(int newId) {
        subsId.remove(Integer.valueOf(newId));
    }

    @Override
    public String toString() {
        return "tasks.Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", id=" + id +
                ", subsId=" + subsId +
                '}';
    }
}
