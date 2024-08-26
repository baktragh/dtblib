package org.baktra.dtblib;

/**
 * Auto-growing storage for integers
 *
 */
public class QuickIntegerVector {

    private int[] storage;
    private int pointer;

    /**
     * Create new stream with default initial capacity
     */
    public QuickIntegerVector() {
        this(1_024);
    }

    /**
     * Create new stream
     *
     * @param initialCapacity Initial capacity
     */
    public QuickIntegerVector(int initialCapacity) {
        storage = new int[initialCapacity];
        pointer = 0;
    }

    /**
     * Add single oneInt
     *
     * @param oneInt
     */
    public void add(int oneInt) {

        /* Check capacity*/
        if (pointer == storage.length - 1) {
            increaseCapacity(512);
        }
        /* Add oneInt*/
        storage[pointer] = oneInt;
        pointer++;

    }

    /**
     * Add intData
     *
     * @param intData Instructions
     */
    public void add(int[] intData) {

        /* Check capacity*/
        int free = storage.length - 1 - pointer;
        if (free < intData.length) {
            increaseCapacity(intData.length * 2);
        }
        System.arraycopy(intData, 0, storage, pointer, intData.length);
        pointer += intData.length;
    }

    /**
     *
     * @return
     */
    public int[] toArray() {
        int[] instructions = new int[pointer];
        System.arraycopy(storage, 0, instructions, 0, instructions.length);
        return instructions;
    }

    private void increaseCapacity(int increment) {
        int newSize = storage.length + increment;
        int[] newStorage = new int[newSize];
        System.arraycopy(storage, 0, newStorage, 0, pointer);
        storage = newStorage;

    }
    
}
