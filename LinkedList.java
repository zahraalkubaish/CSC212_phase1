/**
 * Custom doubly linked list (java.util is not allowed in this project).
 * Source: Lecture11_DLL slides 9-81, plus a size counter (ADT List slide 177).
 * The list keeps a "current" pointer: insert adds AFTER current,
 * and remove deletes current.
 */
public class LinkedList<T> implements List<T> {
    private Node<T> head;     // first node of the list
    private Node<T> current;  // node the list is currently pointing at
    private int size;         // number of elements in the list

    public LinkedList() {
        head = current = null;
        size = 0;
    }

    // Returns true if the list has no elements
    public boolean empty() {
        return head == null;
    }

    // Returns true if current is the last node (list must not be empty)
    public boolean last() {
        return current.next == null;
    }

    // Returns true if current is the first node (list must not be empty)
    public boolean first() {
        return current.previous == null;
    }

    // A linked list is never full
    public boolean full() {
        return false;
    }

    // Moves current to the first node
    public void findFirst() {
        current = head;
    }

    // Moves current to the next node
    public void findNext() {
        current = current.next;
    }

    // Moves current to the previous node
    public void findPrevious() {
        current = current.previous;
    }

    // Returns the element stored in current
    public T retrieve() {
        return current.data;
    }

    // Replaces the element stored in current
    public void update(T val) {
        current.data = val;
    }

    // Inserts a new node after current and makes it the new current
    public void insert(T val) {
        Node<T> tmp = new Node<T>(val);
        if (empty()) {
            current = head = tmp;
        } else {
            tmp.next = current.next;
            tmp.previous = current;
            if (current.next != null)
                current.next.previous = tmp;
            current.next = tmp;
            current = tmp;
        }
        size++;
    }

    // Removes current; its successor becomes current (head if it was the last node)
    public void remove() {
        if (current == head) {
            head = head.next;
            if (head != null)
                head.previous = null;
        } else {
            current.previous.next = current.next;
            if (current.next != null)
                current.next.previous = current.previous;
        }
        if (current.next == null)
            current = head;
        else
            current = current.next;
        size--;
    }

    // Moves current to the last node (list must not be empty)
    public void findLast() {
        while (current.next != null)
            current = current.next;
    }

    // Returns the number of elements in the list
    public int size() {
        return size;
    }
}
