package deque;

import org.junit.Test;
import edu.princeton.cs.algs4.StdRandom;
import java.util.Comparator;
import static org.junit.Assert.*;

public class MaxArrayDequeTest {
    public class IntComparator implements Comparator<Integer> {
        public int compare (Integer a, Integer b) {
            return a - b;
        }
    }

    public Comparator<Integer> GetIntComparator() {
        return new IntComparator();
    }

    public class StringComparator implements Comparator<String> {
        public int compare (String a, String b) {
            return a.compareTo(b);
        }
    }

    public Comparator<String> GetStringComparator() {
        return new StringComparator();
    }

    @Test
    public void randomizedIntegerTest() {

        Comparator<Integer> c = GetIntComparator();
        MaxArrayDeque<Integer> ad = new MaxArrayDeque<>(c);

        int N = 100;
        for (int i = 0; i < N; i++) {
            int operationIndex = StdRandom.uniform(0, 2);
            if (operationIndex == 0) {
                int randVal = StdRandom.uniform(0, 100);
                ad.addFirst(randVal);
            } else if (operationIndex == 1) {
                int randVal = StdRandom.uniform(0, 100);
                ad.addLast(randVal);
            }
        }
        Integer max1 = ad.max();
        Integer max2 = ad.max(c);
        assertEquals(max1, max2);
    }

    @Test
    public void randomizedStringTest() {

        Comparator<String> c = GetStringComparator();
        MaxArrayDeque<String> ad = new MaxArrayDeque<>(c);

        int N = 100;
        ad.addFirst("I'm");
        ad.addFirst("Happy");
        ad.addLast("Very");
        String max1 = ad.max();
        String max2 = ad.max(c);
        assertEquals(max1, max2);
    }
}
