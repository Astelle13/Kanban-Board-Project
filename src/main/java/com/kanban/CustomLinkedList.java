// CustomLinkedList.java
package com.kanban;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class CustomLinkedList<E> {
    private static class Node<E> {
        E data;
        Node<E> next;
        Node<E> prev;

        Node(E data) {
            this.data = data;
            this.next = null;
            this.prev = null;
        }
    }

    private Node<E> head;
    private Node<E> tail;
    private int size;

    public CustomLinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    public void add(E data) {
        Node<E> newNode = new Node<>(data);
        if (tail != null) {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        } else {
            head = tail = newNode;
        }
        size++;
    }

    public void addFirst(E data) {
        Node<E> newNode = new Node<>(data);
        if (head != null) {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        } else {
            head = tail = newNode;
        }
        size++;
    }

    public void addLast(E data) {
        add(data);
    }

    public E removeFirst() {
        if (head == null) return null;
        E data = head.data;
        head = head.next;
        if (head != null) head.prev = null;
        else tail = null;
        size--;
        return data;
    }

    public E removeLast() {
        if (tail == null) return null;
        E data = tail.data;
        tail = tail.prev;
        if (tail != null) tail.next = null;
        else head = null;
        size--;
        return data;
    }

    public E getFirst() {
        return head != null ? head.data : null;
    }

    public E getLast() {
        return tail != null ? tail.data : null;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private Node<E> current = head;

            public boolean hasNext() {
                return current != null;
            }

            public E next() {
                if (current == null) throw new NoSuchElementException();
                E data = current.data;
                current = current.next;
                return data;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public E get(int index) {
        if (index < 0 || index >= size) return null;
        Node<E> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }

    public void remove(E data) {
        Node<E> current = head;
        while (current != null) {
            if (current.data.equals(data)) {
                if (current.prev != null) current.prev.next = current.next;
                else head = current.next;
                if (current.next != null) current.next.prev = current.prev;
                else tail = current.prev;
                size--;
                return;
            }
            current = current.next;
        }
    }

    public int indexOf(E data) {
        Node<E> current = head;
        int index = 0;
        while (current != null) {
            if (current.data.equals(data)) return index;
            current = current.next;
            index++;
        }
        return -1;
    }

    public void clear() {
        head = tail = null;
        size = 0;
    }

    public Object[] toArray() {
        Object[] array = new Object[size];
        Node<E> current = head;
        for (int i = 0; i < size; i++) {
            array[i] = current.data;
            current = current.next;
        }
        return array;
    }
}