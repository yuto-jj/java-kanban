package tasks;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, int epicId, String startTime, int duration) {
        super(name, description, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(int newId, String newName, String newDescription, Status newStatus, int epicId,
                   String startTime, int duration) {
        super(newId, newName, newDescription, newStatus, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public Type getType() {
        return Type.SUBTASK;
    }

    @Override
    public String toString() {
        return "tasks.Subtask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", id=" + id +
                ", epicId=" + epicId +
                ", startTime=" + startTime.format(DATE_TIME_FORMATTER) +
                ", duration=" + duration.toMinutes() +
                ", endTime=" + getEndTime().format(DATE_TIME_FORMATTER) +
                '}';
    }
}


