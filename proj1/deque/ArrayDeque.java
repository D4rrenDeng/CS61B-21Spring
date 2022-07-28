package deque;

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

    public void addFirst(T item) {
        if (size == items.length)
            items = resizing();
        items[nextFirst] = item;
        size++;
        nextFirst = nextFirst - 1 < 0 ? nextFirst + items.length : nextFirst - 1;
    }

    public void addLast(T item) {
        if (size == items.length)
            items = resizing();
        items[nextLast] = item;
        size++;
        nextLast = (nextLast + 1) % items.length;
    }

    public boolean isEmpty() {
        return items.length == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        int first = (nextFirst + 1) % items.length;
        for (int i = 1; i < size; i++) {
            System.out.print(items[first++] + " ");
        }
        System.out.println(items[first]);
    }

    public T removeFirst() {
        if (size == 0)
            return null;
        nextFirst = (nextFirst + 1) % items.length;
        T returnValue = items[nextFirst];
        size--;
        return returnValue;
    }

    public T removeLast() {
        if (size == 0)
            return null;
        nextLast = (nextLast - 1) < 0 ? nextLast - 1 + items.length : nextLast - 1;
        T returnValue = items[nextLast];
        size--;
        return returnValue;
    }

    public T get(int index) {
        if (index >= size || size == 0)
                return null;
        int first = (nextFirst + 1) % items.length;
        return items[(first + index) % items.length];
    }
}
