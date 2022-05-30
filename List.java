/**
 * Represents a list of Nodes. Each node holds a reference to a memory block.
 * <br>
 * (Part of Homework 10 in the Intro to CS course, Efi Arazi School of CS)
 */
public class List {

    private Node first; // The first (dummy) node of this list
    private Node last; // The last node of this list
    private int size = 0; // Number of elements (nodes) in this list

    /**
     * Constructs a new list of Node objects, each holding a memory block (MemBlock
     * object)
     */
    public List() {
        // Creates a dummy node and makes first and last point to it.
        first = new Node(null);
        last = first;
    }

    /**
     * Adds the given memory block to the end of this list.
     * Executes efficiently, in O(1).
     *
//     * @param block The memory block that is added at the list's end
     */
    public void addLast(Job job) {
        Node newNode = new Node(job);
        size++;
        last.next = newNode;
        last = newNode;
    }

    /**
     * Adds the given memory block at the beginning of this list.
     * Executes efficiently, in O(1).
     *
     * @param block The memory block that is added at the list's beginning
     */
    public void addFirst(Job job) {
        Node newNode = new Node(job);
        size++;
        if (first.next != null) {
            newNode.next = first.next;
            first.next = newNode;
        } else {
            newNode.next = first.next;
            first.next = newNode;
            last = first.next;
        }
    }

    /**
     * Gets the node located at the given index in this list.
     *
     * @param index The index of the node to get, between 0 and size - 1
     * @return The node at the given index
     * @throws IllegalArgumentException If index is negative or greater than size -
     *                                  1
     */
    public Node getNode(int index) {
        if (index < 0 || index > size - 1) {
            throw new IllegalArgumentException(index + " is out of bounds");
        }
        ListIterator listIterator = new ListIterator(first.next);
        int iteratingIndex = 0;
        while (iteratingIndex < index) {
            listIterator.current = listIterator.current.next;
            iteratingIndex++;
        }
        return listIterator.current;
    }

    /**
     * Gets the memory block located at the given index in this list.
     *
     * @param index The index of the memory block to get, between 0 and size - 1
     * @return The memory block at the given index
     * @throws IllegalArgumentException If index is negative or greater than size -
     *                                  1
     */
    public Job getJob(int index) {
        return getNode(index).job;
    }

    public int getSize() {
        return this.size;
    }

    /**
     * Gets the index of the node containing the given memory block.
     *
     * @param block The given memory block
     * @return The index of the memory block, or -1 if the memory block is not in
     * this list
     */
    public int indexOf(Job job) {
        int iteratingIndex = 0;
        while (iteratingIndex < size) {
            if (getJob(iteratingIndex) == job) {
                return iteratingIndex;
            }
            iteratingIndex++;
        }
        return -1;
    }

    /**
     * Adds a new node to this list, as follows:
     * Creates a new node containing the given memory block,
     * and inserts the node at the given index in this list.
     * For example, if this list is (m7, m3, m1, m6), then
     * add(2,m5) will make this list (m7, m3, m5, m1, m6).
     * If the given index is 0, the new node becomes the first node in this list.
     * If the given index equals the list's size - 1, the new node becomes the last
     * node in this list.
     * If the new element is added at the beginning or at the end of this list,
     * the addition's runtime is O(1). Othewrise is it O(size).
     *
     * @param block The memory block to add
     * @param index Where to insert the memory block
     * @throws IllegalArgumentException If index is negative or greater than the
     *                                  list's size - 1
     */
    public void add(int index, Job job) {
        if (index < 0 || ((size > 0) && index > (size))) {
            throw new IllegalArgumentException("index must be between 0 and size");
        }
        if (index == 0) {
            addFirst(job);
        } else if (index == size) {
            addLast(job);
        } else {
            Node newNode = new Node(job);
            newNode.next = getNode(index);
            getNode(index - 1).next = newNode;
        }
        size++;
    }

    /**
     * Removes the first memory block from this list.
     * Executes efficiently, in O(1).
     *
     * @throws IllegalArgumentException If trying to remove from an empty list
     */
    public void removeFirst() {
        if (size < 1) {
            throw new IllegalArgumentException();
        }
        Node temp = new Node(null);
        temp.next = first.next.next;
        first.next.next = null;
        first.next = temp.next;
        temp.next = null;
        last = first;
        size--;

    }

    /**
     * Removes the given job from this list.
     *
     * @param job The job to remove
     */
    public void remove(Job job) {
        int index = indexOf(job);
        // Handles index > 0 - from 1 to size (distinguishes between size and size - 1)
        // expetcting to get indexes above size - suppose to crash during indexOf method
        if (index > 0) {
            Node removePrev = getNode(index - 1); // Gets the previous node of the one holding the given job
            if (index > 0 && index < size - 1) {
                Node temp = new Node(null);
                temp.next = removePrev.next.next;
                removePrev.next.next = null;
                removePrev.next = temp.next;
                temp.next = null;
            } else {
                last = removePrev;
                removePrev.next = null;
            }
            size--;
            // Handels index < 0 - mainly 0 and -1 which is recieved from indexOf, in case
            // the MemBlock is not within the list
        } else {
            if (index == 0) {
                removeFirst();
            } else {
                return;
            }
        }
    }

    /**
     * Returns an iterator over this list, starting with the first element.
     *
     * @return A ListIterator object
     */
    public ListIterator iterator() {
        return new ListIterator(first.next);
    }

    /**
     * A textual representation of this list.
     *
     * @return A string representing this list
     */
    public String toString() {
        String s = "[ ";
        Node current = first.next; // Skips the dummy
        while (current != null) {
            s = s + current.job.toString2() + "     ";
            current = current.next;
        }
        return s.substring(0, s.length() - 1) + "]";
    }
}
