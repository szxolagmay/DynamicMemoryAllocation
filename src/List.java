

public abstract class List extends Dictionary {
    public List(int address, int size, int key) {
        super(address, size, key);
    }

    public abstract Dictionary Insert(int address, int size, int key);
    // inserts node in the list and returns the correspanding List element created and inserted

    public abstract boolean Delete(Dictionary d);
    // Deletes the entry corresponding to d from the list.
    // Searches for the d.key in the list
    // Returns false if d not found in the list.
    // Deletes the element it is found in the list and returns true. 

    public abstract Dictionary Find(int k, boolean exact);
    // Searches for the key in the list. 
    // returns an element with key >= k in the list.  Returns null in case no such element found.
    // If exact is true, then performs and exact match and returns an element of the list with key = k

    public abstract Dictionary getFirst();
    public abstract Dictionary getNext();
    public abstract Dictionary getLast();

    // The getFirst and getNext functions are for traversal of the list. 
    // The getFirst() returns the first element of the list and null if the list is empty
    // The getNext(d) returns the next element after d
    // The list class does not define any order in which the elements of the list are to be traversed. 
    // The only requirement is that using the following loop, getFirst() and getNext() and getLast() should be able to 
    // traverse all the elements in the list. 
    // count = 0; for (d = list.getFirst(); d != null; d = d.getNext()) count = count + 1;
    // The getLast() returns the last element of the list and null if the list is empty

    public abstract int sanity();
    // Checks the sanity of the data structure.
}