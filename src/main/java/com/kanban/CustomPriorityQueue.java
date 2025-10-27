// CustomPriorityQueue.java
package com.kanban;

import java.util.Comparator;

public class CustomPriorityQueue<E> {
    private CustomArrayList<E> heap = new CustomArrayList<>();
    public Comparator<E> comparator;  // Made public for access

    public CustomPriorityQueue(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    public void offer(E e) {
        heap.add(e);
        int index = heap.size() - 1;
        siftUp(index);
    }

    public E poll() {
        if (heap.isEmpty()) return null;
        E root = heap.get(0);
        int last = heap.size() - 1;
        heap.set(0, heap.get(last));
        heap.remove(last);
        siftDown(0);
        return root;
    }

    public E peek() {
        return heap.isEmpty() ? null : heap.get(0);
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public int size() {
        return heap.size();
    }

    private void siftUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (comparator.compare(heap.get(parent), heap.get(index)) <= 0) {
                break;
            }
            swap(parent, index);
            index = parent;
        }
    }

    private void siftDown(int index) {
        int left = 2 * index + 1;
        int right = 2 * index + 2;
        int smallest = index;

        if (left < heap.size() && comparator.compare(heap.get(left), heap.get(smallest)) < 0) {
            smallest = left;
        }
        if (right < heap.size() && comparator.compare(heap.get(right), heap.get(smallest)) < 0) {
            smallest = right;
        }

        if (smallest != index) {
            swap(index, smallest);
            siftDown(smallest);
        }
    }

    private void swap(int i, int j) {
        E temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }
}