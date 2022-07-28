package deque;

import java.util.Iterator;



public class LinkedListDeque<T> implements Deque<T>{
    private class Node {
        private T item;
        private Node next;
        private Node prev;

        public Node(T i, Node n, Node p) {
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

    public void addFirst(T item) {
        Node node = new Node(item, sentinel.next, sentinel);
        sentinel.next.prev = node;
        sentinel.next = node;
        size++;
    }

    public void addLast(T item) {
        Node node = new Node(item, sentinel, sentinel.prev);
        sentinel.prev.next = node;
        sentinel.prev = node;
        size++;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        if (size == 0)
            return;
        Node cur = sentinel;
        while (cur.next.next != sentinel) {
            cur = cur.next;
            System.out.print(cur.item + " ");
        }
        System.out.println(cur.item);
    }

    public T removeFirst() {
        if (size == 0)
            return null;
        T first = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        size--;
        return first;
    }

    public T removeLast() {
        if (size == 0)
            return null;
        T last = sentinel.prev.item;
        sentinel.prev = sentinel.prev.prev;
        size--;
        return last;
    }

    public T get(int index) {
        if (size < index + 1)
            return null;
        Node cur = sentinel;
        for (int i = 0; i <= index; i++)
            cur = cur.next;
        return cur.item;
    }

    private T getRecursion(Node n, int index) {
        if (index == 0)
            return n.item;
        return getRecursion(n.next, index - 1);
    }

    public T getRecursive(int index) {
        if (index + 1 > size)
            return null;
        return getRecursion(sentinel.next, index);
    }

    public Iterator<T> iterator(){
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
    }
}


