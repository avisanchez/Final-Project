package mydatastructs;

import java.util.Iterator;

public class MyArrayList<E> implements Iterable<E> {
    private Object[] list;
    private int capacity;
    private int size;

    public MyArrayList() {
        this.capacity = 10;
        this.size = 0;

        list = new Object[capacity];
    }

    public boolean add(E newElement) {
        if (capacity == size) {
            capacity += 10;
            Object[] newList = new Object[capacity];

            for (int i = 0; i < size; i++) {
                newList[i] = list[i];
            }

            list = newList;
        }

        size++;
        list[size - 1] = newElement;

        return true;
    }

    @SuppressWarnings("unchecked")
    public E get(int index) {
        if (index >= 0 && index < size) {
            return (E) list[index];
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public E remove(int index) {
        if (index >= 0 && index < size) {
            E removedElement = (E) list[index];
            for (int i = index; i < size - 1; i++) {
                list[i] = list[i + 1];
            }
            size--;
            return removedElement;
        }
        return null;
    }

    public void set(int index, E newElement) {
        if (index >= 0 && index < size) {
            list[index] = newElement;
        }
    }

    @SuppressWarnings("unchecked")
    public boolean swap(int index1, int index2) {
        if (IndexOutOfBounds(index1) || IndexOutOfBounds(index2)) {
            return false;
        }

        E item1 = (E) list[index1];
        E item2 = (E) list[index2];

        list[index1] = item2;
        list[index2] = item1;

        return true;
    }

    public String toString() {
        String listAsString = "[";

        for (int i = 0; i < size; i++) {
            listAsString += list[i];
            listAsString += i < size - 1 ? ", " : "";
        }

        return listAsString + "]";
    }

    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    private boolean IndexOutOfBounds(int index) {
        return index < 0 || index >= list.length;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            int index = 0;

            @Override
            public boolean hasNext() {
                return !IndexOutOfBounds(index);
            }

            @Override
            public E next() {
                index++;
                return get(index);
            }

        };
    }
}