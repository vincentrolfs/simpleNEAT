package simpleNEAT;

import java.util.List;
import java.util.Random;

class RandomUtil {

    static final Random generator = new Random();

    /**
     * Generates a random double in the specified range. Is not overflow-safe!
     */
    static double getRandomDouble(double min, double max) {
        return generator.nextDouble() * (max - min) + min;
    }

    /**
     * Generates random boolean. Generates "true" with the given probability.
     * @param probability Must be between 0 and 1 inclusive.
     */
    static boolean getRandomBoolean(double probability) {
        return generator.nextDouble() <= probability;
    }

    /**
     * Returns a random value from the list. Returns null if list is empty.
     */
    static <T> T sampleFrom(List<T> list) {
        if (list.size() == 0) {
            return null;
        }

        int index = generator.nextInt(list.size());
        return list.get(index);
    }
}
