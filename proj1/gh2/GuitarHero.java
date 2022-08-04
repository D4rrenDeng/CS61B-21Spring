package gh2;
import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {
    private static String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
    public static void main(String[] args) {
        /* create two guitar strings, for concert A and C */
        GuitarString[] strings = new GuitarString[37];

        for (int i = 0; i < 37; i++) {
            strings[i] = new GuitarString(440.0 * Math.pow(2.0, (i - 24.0) / 12.0));
        }

        while (true) {
            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (keyboard.indexOf(key) < 0) {
                    continue;
                }

                strings[keyboard.indexOf(key)].pluck();
            }
            double sample = 0;
            for (int i = 0; i < 37; i++) {
                sample += strings[i].sample();
            }
            /* play the sample on standard audio */
            StdAudio.play(sample);

            for (int i = 0; i < 37; i++) {
                /* advance the simulation of each guitar string by one step */
                strings[i].tic();
            }
        }
    }
}
