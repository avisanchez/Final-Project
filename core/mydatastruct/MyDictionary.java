package core.mydatastruct;

import java.io.Serializable;

public class MyDictionary<K, V> implements Serializable {
    private Object[] hashArray;
    private int capacity;
    private int size;
    private MySet<K> keySet;

    public MyDictionary() {
        capacity = 1000;
        size = 0;
        hashArray = new Object[capacity];
        keySet = new MySet<K>();
    }

    @SuppressWarnings("unchecked")
    public V put(K key, V value) {
        int index = key.hashCode() % capacity;
        keySet.add(key);
        if (hashArray[index] == null) {
            hashArray[index] = value;
            size++;
        } else {
            V temp = (V) hashArray[index];
            hashArray[index] = value;
            return temp;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public V get(Object obj) {
        K key = (K) obj;
        int index = key.hashCode() % capacity;
        return (V) hashArray[index];
    }

    @SuppressWarnings("unchecked")
    public V remove(Object obj) {
        K key = (K) obj;
        int index = key.hashCode() % capacity;
        if (hashArray[index] == null) {
            return null;
        } else {
            V val = (V) hashArray[index];
            hashArray[index] = null;
            keySet.remove(key);
            size--;
            return val;
        }
    }

    public int size() {
        return size;
    }

    public MySet<K> getKeys() {
        return keySet;
    }

    @SuppressWarnings("unchecked")
    public String toString() {
        String output = "";

        MyArrayList<K> keyList = keySet.toMyArrayList();

        System.out.print("Key List: ");
        System.out.println(keyList);

        for (int i = 0; i < keyList.size(); i++) {
            K key = keyList.get(i);
            V value = (V) hashArray[key.hashCode() % capacity];

            output += (key + "=" + value);
            output += (i < keyList.size() - 1) ? ", " : "";
        }

        return "{" + output + "}";
    }

}