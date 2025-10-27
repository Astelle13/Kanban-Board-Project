// CustomArrayList.java (no changes needed, but add import if any)
package com.kanban;

public class CustomArrayList<E> {
    private static final int DEFAULT_CAPACITY = 10;
    private Object[] elementData;
    private int size;

    public CustomArrayList() {
        elementData = new Object[DEFAULT_CAPACITY];
        size = 0;
    }

    public void add(E e) {
        ensureCapacity();
        elementData[size++] = e;
    }

    public E get(int index) {
        if (index < 0 || index >= size) return null;
        return (E) elementData[index];
    }

    public void set(int index, E e) {
        if (index < 0 || index >= size) return;
        elementData[index] = e;
    }

    public E remove(int index) {
        if (index < 0 || index >= size) return null;
        E old = (E) elementData[index];
        System.arraycopy(elementData, index + 1, elementData, index, size - index - 1);
        elementData[--size] = null;
        return old;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        for (int i = 0; i < size; i++) {
            elementData[i] = null;
        }
        size = 0;
    }

    private void ensureCapacity() {
        if (size == elementData.length) {
            int newCapacity = elementData.length * 2;
            Object[] newData = new Object[newCapacity];
            System.arraycopy(elementData, 0, newData, 0, size);
            elementData = newData;
        }
    }

    public Object[] toArray() {
        Object[] array = new Object[size];
        System.arraycopy(elementData, 0, array, 0, size);
        return array;
    }
}