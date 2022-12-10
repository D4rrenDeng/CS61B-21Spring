package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @Darren Deng
 */
public class MyHashMap<K, V> implements Map61B<K, V> {
    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private Set<K> keySet = new HashSet<>();
    private int size = 0;
    private int bucketSize = 16;
    private double loadFactor = 0.75;

    /** Constructors */
    public MyHashMap() {
        buckets = createTable(bucketSize);
    }

    public MyHashMap(int initialSize) {
        this.bucketSize = initialSize;
        buckets = createTable(bucketSize);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.bucketSize = initialSize;
        buckets = createTable(bucketSize);
        loadFactor = maxLoad;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    @Override
    public void clear() {
        buckets = createTable(bucketSize);
        size = 0;
        keySet = new HashSet<>();
    }

    @Override
    public boolean containsKey(K key) {
        return keySet.contains(key);
    }

    @Override
    public V get(K key) {
        if (!containsKey(key)) {
            return null;
        }
        int hashValue = key.hashCode();
        Collection<Node> bucket = buckets[Math.floorMod(hashValue, bucketSize)];
        V value = null;

        for (Node node : bucket) {
            if (node.key.equals(key)) {
                value = node.value;
            }
        }
        return value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        int hashValue = key.hashCode();
        int index = Math.floorMod(hashValue, bucketSize);
        if (containsKey(key)) {
            for (Node n : buckets[index]) {
                if (n.key.equals(key)) {
                    n.value = value;
                }
            }
        } else {
            if (buckets[index] == null) {
                buckets[index] = createBucket();
            }
            size += 1;
            keySet.add(key);
            buckets[index].add(createNode(key, value));
        }
        if (size * 1.0 / bucketSize >= loadFactor) {
            int newBucketSize = bucketSize * 3;
            Collection<Node>[] newBuckets = createTable(newBucketSize);
            for (K k : keySet) {
                hashValue = k.hashCode();
                index = Math.floorMod(hashValue, newBucketSize);
                if (newBuckets[index] == null) {
                    newBuckets[index] = createBucket();
                }
                newBuckets[index].add(createNode(k, get(k)));
            }
            buckets = newBuckets;
            bucketSize = newBucketSize;
        }
    }

    @Override
    public Set<K> keySet() {
        return keySet;
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        return keySet.iterator();
    }
}
