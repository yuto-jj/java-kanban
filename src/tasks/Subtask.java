package tasks;

import java.time.LocalDateTime;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, int epicId, LocalDateTime startTime, int duration) {
        super(name, description, startTime, duration);
        this.epicId = epicId;
        this.type = Type.SUBTASK;
    }

    public Subtask(int newId, String newName, String newDescription, Status newStatus, int epicId,
                   LocalDateTime startTime, int duration) {
        super(newId, newName, newDescription, newStatus, startTime, duration);
        this.epicId = epicId;
        this.type = Type.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "tasks.Subtask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", id=" + id +
                ", epicId=" + epicId +
                ", startTime=" + (startTime != null ? startTime.format(DATE_TIME_FORMATTER) : "null") +
                ", duration=" + duration.toMinutes() +
                ", endTime=" + (getEndTime() != null ? getEndTime().format(DATE_TIME_FORMATTER) : "null") +
                '}';
    }
}


