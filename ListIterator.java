/**
 * Represents an iterator of a linked list of memory blocks.
 * <br>
 * (Part of Homework 10 in the Intro to CS course, Efi Arazi School of CS)
 */
public class ListIterator {

    // Current position in the list (cursor)
    public Node current;

    /** Constructs a list iterator, starting at the given node */
    public ListIterator(Node node) {
        current = node;
    }

    /** Checks if this iterator has more elements to process */
    public boolean hasNext() {
        return (current != null);
    }

    /**
     * Returns the next job in the list, and advances the cursor position.
     * This method may be called repeatedly, to iterate through the list.
     * 
     * @return the job at the cursor's location
     */
    public Job next() {
        Node currentNode = current;
        current = current.next;
        return currentNode.job;
    }
}