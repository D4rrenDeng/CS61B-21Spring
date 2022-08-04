package deque;

public interface Deque<T> {
    void addFirst(T item);
    void addLast(T item);
    default public boolean isEmpty() {
        if (size() == 0) {
            return true;
        }
        return false;
    }
    int size();
    void printDeque();
    T removeFirst();
    T removeLast();
    T get(int index);
}
