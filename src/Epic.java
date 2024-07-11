import java.util.ArrayList;

public class Epic extends Task {
    ArrayList<Integer> subsId = new ArrayList<>();

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
        int subId = subsId.indexOf(newId);
        subsId.remove(subId);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", id=" + getId() +
                '}';
    }
}
