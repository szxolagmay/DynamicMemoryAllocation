// Class: Dictionary

public abstract class Dictionary {

    // The data stored in the dictionary
    public int key;             // Key on Which the directory is indexed.
    public int address, size;   // Contains the address and size of the memory block

    public Dictonary(int address, int size, int key) { // Constructor for the dictionary element
        this.address = address;
        this.size = size;
        this.key = key;
    }

    public abstract Dictionary insert(int address, int size, int key);
    // inserts node in the dictionary and returns the correspanding Dictionary element created and inserted

    tract boolean Delete(Dictionary d);
    // Deletes the entry corresponding to d from the dictionary.
    // Searches for the d.key in the dictionary
    // Returns false if d not found in the dictionary.
    // Deletes the element it is found in the dictionary and returns true. 

    public abstract Dictionary Find(int k,boolean exact);
    // Searches for the key in the dictionary. 
    // returns an element with key >= k in the dictionary.  Returns null in case no such element found.
    // If exact is true, then performs and exact match and returns an element of the dictionary with key = k

    public abstract Dictionary getFirst();
    public abstract Dictionary getNext();
    public abstract Dictionary getLast();

    // The getFirst and getNext functions are for traversal of the dictionary. 
    // The getFirst() returns the first element of the dictionary and null if the dictionary is empty
    // The getNext(d) returns the next element after d
    // The dictionary class does not define any order in which the elements of the dictionary are to be traversed. 
    // The only requirement is that using the following loop, getFirst() and getNext() and getLast() should be able to 
    // traverse all the elements in the dictionary. 
    // count = 0; for (d = dict.getFirst(); d != null; d = d.getNext d = getLast()) count = count + 1;
    // The getLast() returns the last element of the dictionary and null if the dictionary is empty

    public abstract int sanity(); 
    // Checks the sanity of the data structure.
}