package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        nextFirst = 2;
        nextLast = 3;
        size = 0;
    }

    private T[] resizing(int capacity) {
        T[] newItems = (T[]) new Object[capacity];
        int first = (nextFirst + 1) % items.length;
        int last = nextLast - 1 < 0 ? nextLast - 1 + items.length : nextLast - 1;
        if (first <= last) {
            System.arraycopy(items, first, newItems, 0, size);
            nextFirst = newItems.length - 1;
            nextLast = size;
        } else {
            System.arraycopy(items, 0, newItems, 0, last + 1);
            System.arraycopy(items, first, newItems, newItems.length - items.length
                    + first, items.length - first);
            nextFirst = (newItems.length - 1 - items.length + first) % newItems.length;
            nextLast = (last + 1) % newItems.length;
        }
        return newItems;
    }
    @Override
    public void addFirst(T item) {
        if (size == items.length) {
            items = resizing(size * 2);
        }
        items[nextFirst] = item;
        size++;
        nextFirst = nextFirst - 1 < 0 ? nextFirst - 1 + items.length : nextFirst - 1;
    }
    @Override
    public void addLast(T item) {
        if (size == items.length) {
            items = resizing(size * 2);
        }
        items[nextLast] = item;
        size++;
        nextLast = (nextLast + 1) % items.length;
    }
    @Override
    public int size() {
        return size;
    }
    @Override
    public void printDeque() {
        int first = (nextFirst + 1) % items.length;
        for (int i = 1; i <= size; i++) {
            System.out.print(items[first % items.length] + " ");
            first++;
        }
        System.out.println(items[first % items.length]);
    }
    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        if (((double) size / items.length) < 0.25) {
            items = resizing(items.length / 2);
        }
        nextFirst = (nextFirst + 1) % items.length;
        T returnValue = items[nextFirst];
        size--;
        return returnValue;
    }
    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        if (((double) size / items.length) < 0.25) {
            items = resizing(items.length / 2);
        }
        nextLast = (nextLast - 1) < 0 ? nextLast - 1 + items.length : nextLast - 1;
        T returnValue = items[nextLast];
        size--;
        return returnValue;
    }
    @Override
    public T get(int index) {
        if (index >= size || size == 0) {
            return null;
        }
        int first = (nextFirst + 1) % items.length;
        return items[(first + index) % items.length];
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof Deque)) {
            return false;
        }
        Deque<T> subject = (Deque<T>) o;
        if (size != subject.size()) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!get(i).equals(subject.get(i))) {
                return false;
            }
        }
        return true;
    }

    public Iterator<T> iterator() {
        return new ArrayDeque.ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int cur;
        private int count;
        ArrayDequeIterator() {
            cur = (nextFirst + 1) / items.length;
            count = 0;
        }
        public boolean hasNext() {
            return count <= size;
        }

        public T next() {
            T returnItem = items[cur];
            cur = (cur + 1) % items.length;
            count++;
            return returnItem;
        }
    }
}
