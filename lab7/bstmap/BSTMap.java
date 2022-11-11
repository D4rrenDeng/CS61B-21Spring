package bstmap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private class BSTNode<K, V> {
        private K key;
        private V val;
        private BSTNode<K, V> left;
        private BSTNode<K, V> right;
        private BSTNode(K key, V val) {
            this.key = key;
            this.val = val;
        }
    }

    private BSTNode<K, V> root;
    private int size = 0;
    public void printInOrder() {
        inOrder(root);
        System.out.println();
    }

    public void inOrder(BSTNode<K, V> node) {
        if (node == null) {
            return;
        }
        inOrder(node.left);
        System.out.print(node.val);
        inOrder(node.right);
    }

    @Override
    public void clear() {
        size = 0;
        root = null;
    }

    @Override
    public boolean containsKey(K key) {
        BSTNode<K, V> cur = root;

        while (cur != null) {
            if (cur.key.compareTo(key) == 0) {
                return true;
            } else if (key.compareTo(cur.key) < 0) {
                cur = cur.left;
            } else {
                cur = cur.right;
            }
        }
        return false;
    }

    @Override
    public V get(K key) {
        BSTNode<K, V> cur = root;

        while (cur != null) {
            if (cur.key.compareTo(key) == 0) {
                return cur.val;
            } else if (cur.key.compareTo(key) < 0) {
                cur = cur.left;
            } else {
                cur = cur.right;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        if (root == null) {
            root = new BSTNode(key, value);
            size++;
            return;
        }

        BSTNode<K, V> cur = root;

        while (cur != null) {
            if (cur.key.compareTo(key) == 0) {
                cur.val = value;
                return;
            } else if (cur.key.compareTo(key) > 0) {
                if (cur.left != null) {
                    cur = cur.left;
                } else {
                    cur.left = new BSTNode(key, value);
                    size++;
                    return;
                }
            } else {
                if (cur.right != null) {
                    cur = cur.right;
                } else {
                    cur.right = new BSTNode(key, value);
                    size++;
                    return;
                }
            }
        }

    }

    @Override
    public Set<K> keySet() {
        Set<K> keySet = new HashSet<>();
        Stack<BSTNode> stack = new Stack<>();

        stack.push(root);
        while (!stack.isEmpty()) {
            BSTNode<K, V> cur = stack.pop();
            keySet.add(cur.key);
            if (cur.right != null) {
                stack.push(cur.right);
            }
            if (cur.left != null) {
                stack.push(cur.left);
            }
        }
        return keySet;
    }

    @Override
    public V remove(K key) {
        BSTNode<K, V> prev = null;
        BSTNode<K, V> cur = root;

        while (cur != null) {
            if (key.compareTo(cur.key) < 0) {
                prev = cur;
                cur = cur.left;
            } else if (key.compareTo(cur.key) > 0) {
                prev = cur;
                cur = cur.right;
            } else {
                V value = cur.val;
                if (cur.left == null && cur.right == null) {
                    if (prev == null) {
                        root = null;
                    } else if (prev.left == cur) {
                        prev.left = null;
                    } else {
                        prev.right = null;
                    }
                    size--;
                    return value;
                } else if (cur.right == null) {
                    if (prev == null) {
                        root = root.left;
                    } else if (prev.left == cur) {
                        prev.left = cur.left;
                    } else {
                        prev.right = cur.left;
                    }
                    size--;
                    return value;
                } else if (cur.left == null) {
                    if (prev == null) {
                        root = root.right;
                    } else if (prev.left == cur) {
                        prev.left = cur.right;
                    } else {
                        prev.right = cur.right;
                    }
                    size--;
                    return value;
                } else {
                    BSTNode<K, V> predecessor = cur.left;
                    prev = cur;
                    while (predecessor.right != null) {
                        prev = predecessor;
                        predecessor = predecessor.right;
                    }
                    cur.val = predecessor.val;
                    cur.key = predecessor.key;
                    if (prev == cur) {
                        cur.left = predecessor.left;
                    } else {
                        prev.right = predecessor.left;
                    }
                    size--;
                    return value;
                }
            }
        }
        return null;
    }

    @Override
    public V remove(K key, V value) {
        BSTNode<K, V> prev = null;
        BSTNode<K, V> cur = root;

        while (cur != null) {
            if (key.compareTo(cur.key) < 0) {
                prev = cur;
                cur = cur.left;
            } else if (key.compareTo(cur.key) > 0) {
                prev = cur;
                cur = cur.right;
            } else if (key.compareTo(cur.key) == 0 && cur.val == value) {
                if (cur.left == null && cur.right == null) {
                    if (prev == null) {
                        root = null;
                    } else if (prev.left == cur) {
                        prev.left = null;
                    } else {
                        prev.right = null;
                    }
                    size--;
                    return value;
                } else if (cur.right == null) {
                    if (prev == null) {
                        root = root.left;
                    } else if (prev.left == cur) {
                        prev.left = cur.left;
                    } else {
                        prev.right = cur.left;
                    }
                    size--;
                    return value;
                } else if (cur.left == null) {
                    if (prev == null) {
                        root = root.right;
                    } else if (prev.left == cur) {
                        prev.left = cur.right;
                    } else {
                        prev.right = cur.right;
                    }
                    size--;
                    return value;
                } else {
                    BSTNode<K, V> predecessor = cur.left;
                    prev = cur;
                    while (predecessor.right != null) {
                        prev = predecessor;
                        predecessor = predecessor.right;
                    }
                    cur.val = predecessor.val;
                    cur.key = predecessor.key;
                    if (prev == cur) {
                        cur.left = predecessor.left;
                    } else {
                        prev.right = predecessor.left;
                    }
                    size--;
                    return value;
                }
            }
        }
        return null;
    }

    private class BSTMapIterator implements Iterator<K> {
        private Set<K> keySet;
        public BSTMapIterator() {
            keySet = keySet();
        }

        public boolean hasNext() {
            return keySet.size() > 0;
        }

        public K next() {
            K key = null;
            for (K k : keySet) {
                key = k;
                keySet.remove(k);
                break;
            }
            return key;
        }
    }

    @Override
    public Iterator<K> iterator() {
        return new BSTMapIterator();
    }
}
