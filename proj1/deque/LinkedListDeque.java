package deque;

import java.util.Iterator;



public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private class Node {
        private T item;
        private Node next;
        private Node prev;

        private Node(T i, Node n, Node p) {
            item = i;
            next = n;
            prev = p;
        }
    }
    private Node sentinel;
    private int size;

    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }
    @Override
    public void addFirst(T item) {
        Node node = new Node(item, sentinel.next, sentinel);
        sentinel.next.prev = node;
        sentinel.next = node;
        size++;
    }
    @Override
    public void addLast(T item) {
        Node node = new Node(item, sentinel, sentinel.prev);
        sentinel.prev.next = node;
        sentinel.prev = node;
        size++;
    }
    @Override
    public int size() {
        return size;
    }
    @Override
    public void printDeque() {
        if (size == 0) {
            return;
        }
        Node cur = sentinel;
        while (cur.next.next != sentinel) {
            cur = cur.next;
            System.out.print(cur.item + " ");
        }
        System.out.println(cur.item);
    }
    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        T first = sentinel.next.item;
        Node temp = sentinel.next.next;
        sentinel.next = sentinel.next.next;
        temp.prev = sentinel;
        size--;
        return first;
    }
    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        T last = sentinel.prev.item;
        Node temp = sentinel.prev.prev;
        sentinel.prev = sentinel.prev.prev;
        temp.next = sentinel;
        size--;
        return last;
    }
    @Override
    public T get(int index) {
        if (size < index + 1) {
            return null;
        }
        Node cur = sentinel;
        for (int i = 0; i <= index; i++) {
            cur = cur.next;
        }
        return cur.item;
    }

    private T getRecursion(Node n, int index) {
        if (index == 0) {
            return n.item;
        }
        return getRecursion(n.next, index - 1);
    }

    public T getRecursive(int index) {
        if (index + 1 > size) {
            return null;
        }
        return getRecursion(sentinel.next, index);
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
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        private Node cur;
        public LinkedListDequeIterator() {
            cur = sentinel.next;
        }
        public boolean hasNext() {
            return cur != sentinel;
        }

        public T next() {
            T returnItem = cur.item;
            cur = cur.next;
            return returnItem;
        }
    }
}


