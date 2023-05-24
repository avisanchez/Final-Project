package core.mydatastruct;

import java.io.Serializable;
import java.util.Iterator;

public class MySet<E> implements Serializable, Iterable<E> {
    private Object[] list;
    private int capacity;
    private int size = 0;

    public MySet() {
        capacity = 1000;
        list = new Object[capacity];
    }

    public boolean add(E item) {
        int index = item.hashCode() % capacity;
        if (list[index] != null) {
            return false;
        }
        list[index] = item;
        size++;
        return true;
    }

    @SuppressWarnings("unchecked")
    public boolean contains(Object o) {
        E item = (E) o;
        return item.equals(list[item.hashCode() % capacity]);
    }

    @SuppressWarnings("unchecked")
    public boolean remove(Object o) {
        E item = (E) o;
        int location = item.hashCode();

        if (!item.equals(list[location % capacity])) {
            return false;
        }

        list[location % capacity] = null;
        size--;
        return true;
    }

    public void clear() {
        list = new Object[capacity];
        size = 0;
    }

    public int size() {
        return size;
    }

    @SuppressWarnings("unchecked")
    public MyArrayList<E> toMyArrayList() {
        MyArrayList<E> arrList = new MyArrayList<E>();
        for (int i = 0; i < capacity; i++) {
            if (list[i] != null) {
                arrList.add((E) list[i]);
            }
        }
        return arrList;
    }

    public String toString() {
        String output = "";
        for (int i = 0; i < capacity; i++) {
            if (list[i] != null) {
                output += list[i].toString() + "\n";
            }
        }
        return output.substring(0, output.length() - 2);
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            int index = 0;

            @Override
            public boolean hasNext() {
                while (index < capacity) {
                    if (list[index] != null) {
                        return true;
                    }
                    index++;
                }
                return false;
            }

            @Override
            @SuppressWarnings("unchecked")
            public E next() {
                E data = (E) list[index];
                index++;
                return data;
            }

        };
    }

}