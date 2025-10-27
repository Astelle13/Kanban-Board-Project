// CustomQueue.java
package com.kanban;

import java.util.Iterator;

public class CustomQueue<E> {
    private CustomLinkedList<E> list = new CustomLinkedList<>();

    public void offer(E e) {
        list.addLast(e);
    }

    public E poll() {
        return list.removeFirst();
    }

    public E peek() {
        return list.getFirst();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int size() {
        return list.size();
    }

    public CustomLinkedList<E> getList() {
        return list;
    }

    public Iterator<E> iterator() {
        return list.iterator();
    }
}