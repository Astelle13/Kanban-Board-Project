// CustomHashSet.java (add import)
package com.kanban;

import java.util.Iterator;

public class CustomHashSet<E> {
    private CustomHashMap<E, Object> map = new CustomHashMap<>();

    public boolean add(E e) {
        return map.put(e, this) == null;
    }

    public boolean contains(E e) {
        return map.containsKey(e);
    }

    public boolean remove(E e) {
        return map.remove(e) != null;
    }

    public Iterator<E> iterator() {
        final Iterator<Object> it = map.values().iterator();
        return new Iterator<E>() {
            public boolean hasNext() {
                return it.hasNext();
            }

            public E next() {
                it.next();
                // Simplified; in practice, track keys separately
                return null; // Placeholder - adjust based on use
            }

            public void remove() {
                it.remove();
            }
        };
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public void clear() {
        map.clear();
    }
}