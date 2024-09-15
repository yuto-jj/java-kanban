package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subsId = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, LocalDateTime.now().format(DATE_TIME_FORMATTER), 0);
        endTime = LocalDateTime.now();
    }

    public Epic(int newId, String newName, String newDescription) {
        super(newName, newDescription, LocalDateTime.now().format(DATE_TIME_FORMATTER), 0);
        this.id = newId;
    }

    @Override
    public void setStartTime(String startTime) {
        if (startTime.equals("null")) {
            this.startTime = null;
        } else {
            this.startTime = LocalDateTime.parse(startTime, DATE_TIME_FORMATTER);
        }
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setEndTime(String endTime) {
        if (endTime.equals("null")) {
            this.endTime = null;
        } else {
            this.endTime = LocalDateTime.parse(endTime, DATE_TIME_FORMATTER);
        }
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
    public Type getType() {
        return Type.EPIC;
    }

    @Override
    public String toString() {
        return "tasks.Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", id=" + id +
                ", subsId=" + subsId +
                ", startTime=" + (startTime != null ? startTime.format(DATE_TIME_FORMATTER) : "null") +
                ", duration=" + duration.toMinutes() +
                ", endTime=" + (endTime != null ? startTime.format(DATE_TIME_FORMATTER) : "null") +
                '}';
    }
}

