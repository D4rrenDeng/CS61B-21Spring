package bstmap;

public class BSTMapTest {
    public static void main(String[] args) {
        BSTMap<Integer, Integer> map = new BSTMap<>();

        map.put(5, 5);
        map.put(2, 2);
        map.put(1, 1);
        map.put(3, 3);
        map.put(6, 6);
        map.printInOrder();
        map.remove(5);
        map.printInOrder();
        for (Integer key : map) {
            System.out.print(key);
        }
    }
}
