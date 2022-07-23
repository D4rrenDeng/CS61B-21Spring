package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> good = new AListNoResizing<>();
        BuggyAList<Integer> bad = new BuggyAList<>();

        good.addLast(4);
        bad.addLast(4);
        good.addLast(5);
        bad.addLast(5);
        good.addLast(6);
        bad.addLast(6);

        assertEquals(good.removeLast(), bad.removeLast());
        assertEquals(good.removeLast(), bad.removeLast());
        assertEquals(good.removeLast(), bad.removeLast());
    }
    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> broken = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                broken.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int size1 = broken.size();
                assertEquals(size, size1);
            } else if (operationNumber == 2) {
                if (L.size() == 0)
                    continue;
                int last = L.getLast();
                int last1 = broken.getLast();
                assertEquals(last, last1);
            } else {
                if (L.size() == 0)
                    continue;
                assertEquals(L.removeLast(), broken.removeLast());
            }
        }
    }
}
