// The ADT List interface: every list implementation must provide these operations
public interface List<T> {
    public void findFirst();     // make current the first element
    public void findNext();      // move current to the next element
    public T retrieve();         // return the element in current
    public void update(T e);     // replace the element in current
    public void insert(T e);     // add a new element after current
    public void remove();        // delete the element in current
    public boolean full();       // is the list full?
    public boolean empty();      // is the list empty?
    public boolean last();       // is current the last element?
}
