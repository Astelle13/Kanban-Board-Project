// CustomHashMap.java (no change needed from previous)
package com.kanban;

import java.util.*;

public class CustomHashMap<K, V> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;
    private LinkedList<Entry<K, V>>[] table;
    private int size;
    private int capacity;

    private static class Entry<K, V> {
        K key;
        V value;
        Entry<K, V> next;

        Entry(K key, V value, Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    @SuppressWarnings("unchecked")
    public CustomHashMap() {
        capacity = DEFAULT_CAPACITY;
        table = new LinkedList[capacity];
        for (int i = 0; i < capacity; i++) {
            table[i] = new LinkedList<>();
        }
        size = 0;
    }

    private int hash(K key) {
        int h = (key == null ? 0 : key.hashCode());
        h ^= (h >>> 16);
        return (h & 0x7fffffff) % capacity;
    }

    public V get(K key) {
        int index = hash(key);
        LinkedList<Entry<K, V>> list = table[index];
        for (Entry<K, V> entry : list) {
            if (key == null ? entry.key == null : key.equals(entry.key)) {
                return entry.value;
            }
        }
        return null;
    }

    public V put(K key, V value) {
        int index = hash(key);
        LinkedList<Entry<K, V>> list = table[index];
        for (Entry<K, V> entry : list) {
            if (key == null ? entry.key == null : key.equals(entry.key)) {
                V old = entry.value;
                entry.value = value;
                return old;
            }
        }
        list.add(new Entry<>(key, value, null));
        size++;
        if ((float) size / capacity > LOAD_FACTOR) {
            resize();
        }
        return null;
    }

    public V remove(K key) {
        int index = hash(key);
        LinkedList<Entry<K, V>> list = table[index];
        for (Entry<K, V> entry : list) {
            if (key == null ? entry.key == null : key.equals(entry.key)) {
                list.remove(entry);
                size--;
                return entry.value;
            }
        }
        return null;
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    public Collection<V> values() {
        List<V> vals = new ArrayList<>();
        for (LinkedList<Entry<K, V>> list : table) {
            for (Entry<K, V> entry : list) {
                vals.add(entry.value);
            }
        }
        return vals;
    }

    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        for (LinkedList<Entry<K, V>> list : table) {
            for (Entry<K, V> entry : list) {
                keys.add(entry.key);
            }
        }
        return keys;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        capacity *= 2;
        LinkedList<Entry<K, V>>[] newTable = new LinkedList[capacity];
        for (int i = 0; i < capacity; i++) {
            newTable[i] = new LinkedList<>();
        }
        for (LinkedList<Entry<K, V>> list : table) {
            for (Entry<K, V> entry : list) {
                int newIndex = hash(entry.key);
                newTable[newIndex].add(entry);
            }
        }
        table = newTable;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        for (int i = 0; i < capacity; i++) {
            table[i].clear();
        }
        size = 0;
    }

    // Add iterator for values
    public Iterator<V> valuesIterator() {
        return new Iterator<V>() {
            private int bucket = 0;
            private Iterator<Entry<K, V>> currentListIt = table[0].iterator();
            private int entryIndex = 0;

            private boolean advance() {
                while (bucket < capacity) {
                    if (currentListIt.hasNext()) {
                        return true;
                    }
                    bucket++;
                    if (bucket < capacity) {
                        currentListIt = table[bucket].iterator();
                    }
                }
                return false;
            }

            public boolean hasNext() {
                return advance();
            }

            public V next() {
                if (!hasNext()) throw new NoSuchElementException();
                V val = currentListIt.next().value;
                return val;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    // Add iterator for keySet
    public Iterator<K> keySetIterator() {
        return new Iterator<K>() {
            private int bucket = 0;
            private Iterator<Entry<K, V>> currentListIt = table[0].iterator();
            private int entryIndex = 0;

            private boolean advance() {
                while (bucket < capacity) {
                    if (currentListIt.hasNext()) {
                        return true;
                    }
                    bucket++;
                    if (bucket < capacity) {
                        currentListIt = table[bucket].iterator();
                    }
                }
                return false;
            }

            public boolean hasNext() {
                return advance();
            }

            public K next() {
                if (!hasNext()) throw new NoSuchElementException();
                K key = currentListIt.next().key;
                return key;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}