package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    ArrayDeque<T> ad;
    Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> c) {
        ad = new ArrayDeque<>();
        comparator = c;
    }

    public T max() {
        if (size() == 0)
            return null;
        T max = get(0);
        for (int i = 1; i < size(); i++) {
            if (comparator.compare(max, get(i)) < 0)
                max = get(i);
        }
        return max;
    }

    public T max(Comparator<T> c) {
        if (size() == 0)
            return null;
        T max = get(0);
        for (int i = 1; i < size(); i++) {
            if (c.compare(max, get(i)) < 0)
                max = get(i);
        }
        return max;
    }
}
