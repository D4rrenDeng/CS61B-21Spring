package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        Stopwatch sw = new Stopwatch();
        SLList<Integer> list = new SLList<>();
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCounts = new AList<>();
        int numberOp = 10000;
        int i = 1;

        for (int count = 1000; count <= 128000; count *= 2) {
            while (i <= count)
                list.addLast(i++);
            double timeInSeconds = sw.elapsedTime();
            for (int j = 1; j <= numberOp; j++)
                list.getLast();
            Ns.addLast(count);
            times.addLast(timeInSeconds);
            opCounts.addLast(numberOp);
        }
        printTimingTable(Ns, times, opCounts);
    }

}
