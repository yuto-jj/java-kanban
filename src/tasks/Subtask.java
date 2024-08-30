package tasks;

public class Subtask extends Task {
        private int epicId;
        private final Type type = Type.SUBTASK;

        public Subtask(String name, String description, int epicId) {
            super(name, description);
            this.epicId = epicId;
        }

        public Subtask(int newId, String newName, String newDescription, Status newStatus, int epicId) {
            super(newId, newName, newDescription, newStatus);
            this.epicId = epicId;
        }

        @Override
        public Type getType() {
            return type;
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
                    '}';
        }
}
