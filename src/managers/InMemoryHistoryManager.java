package managers;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private static class Node {
        Task task;
        Node prev;
        Node next;

        private Node(Task task, Node prev, Node next) {
            this.task = task;
            this.prev = prev;
            this.next = next;
        }
    }

    private final Map<Integer, Node> nodeMap = new HashMap<>();
    private Node first;
    private Node last;


    private void linkLast(Task task) {
        Node node = new Node(task, last, null);
        if (last != null) {
            last.next = node;
        } else {
            first = node;
        }
        last = node;
    }

    private void removeNode(Node node) {
        if (node.prev == null) {
            first = node.next;
            if (first != null) {
                first.prev = null;
            } else {
                last = null;
            }
        } else if (node.next != null) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        } else {
            last = node.prev;
            last.next = null;
        }
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            if (nodeMap.containsKey(task.getId())) {
                remove(task.getId());
            }
            linkLast(task);
            nodeMap.put(task.getId(), last);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> tasks = new ArrayList<>();
        Node node = first;
        while (node != null) {
            tasks.add(node.task);
            node = node.next;
        }
        return tasks;
    }

    @Override
    public void remove(int id) {
        Node node = nodeMap.get(id);
        if (node != null) {
            removeNode(node);
            nodeMap.remove(id);
        }
    }
}
