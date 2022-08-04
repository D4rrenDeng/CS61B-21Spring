package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T> {
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

    public T[] resizing() {
        T[] newItems = (T[]) new Object[size * 2];
        int first = (nextFirst + 1) % items.length;
        int last = nextLast - 1 < 0 ? nextLast - 1 + items.length : nextLast - 1;
        if (last == items.length - 1) {
            System.arraycopy(items, 0, newItems, 0, size);
            nextFirst = newItems.length - 1;
            nextLast = size;
        }
        else {
            System.arraycopy(items, 0, newItems, 0, last + 1);
            System.arraycopy(items, last + 1, newItems, newItems.length - items.length + last + 1, items.length - last - 1);
            nextFirst = newItems.length - items.length + last;
            nextLast = last + 1;
        }
        return newItems;
    }
    @Override
    public void addFirst(T item) {
        if (size == items.length)
            items = resizing();
        items[nextFirst] = item;
        size++;
        nextFirst = nextFirst - 1 < 0 ? nextFirst - 1 + items.length : nextFirst - 1;
    }
    @Override
    public void addLast(T item) {
        if (size == items.length)
            items = resizing();
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
        if (size == 0)
            return null;
        nextFirst = (nextFirst + 1) % items.length;
        T returnValue = items[nextFirst];
        size--;
        return returnValue;
    }
    @Override
    public T removeLast() {
        if (size == 0)
            return null;
        nextLast = (nextLast - 1) < 0 ? nextLast - 1 + items.length : nextLast - 1;
        T returnValue = items[nextLast];
        size--;
        return returnValue;
    }
    @Override
    public T get(int index) {
        if (index >= size || size == 0)
                return null;
        int first = (nextFirst + 1) % items.length;
        return items[(first + index) % items.length];
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof Deque))
            return false;
        Deque<T> subject = (Deque<T>) o;
        if (size != subject.size())
            return false;
        for (int i = 0; i < size; i++)
            if (!get(i).equals(subject.get(i)))
                return false;
        return true;
    }

    public Iterator<T> iterator(){
        return new ArrayDeque.ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int cur;
        private int count;
        public ArrayDequeIterator() {
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
