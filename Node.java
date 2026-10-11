/**
 * A node of the doubly linked list.
 */
public class Node<T> {
    public T data;            // the stored element
    public Node<T> next;      // link to the following node
    public Node<T> previous;  // link to the preceding node

    public Node() {        // empty node
        data = null;
        next = null;
        previous = null;
    }

    public Node(T val) {  // node holding val
        data = val;
        next = null;
        previous = null;
    }
}



