package simpleNEAT;

import java.util.*;

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

    /**
     * Samples multiple values from the list without replacement.
     * The amount of values returned is given by {@code amount}.
     * Requires {@code amount >= 1 && amount <= list.size()}.
     */
    static <T> Set<T> sampleMultipleFrom(List<T> list, int amount){
        assert amount >= 1 && amount <= list.size() : "amount must be at least 1 and no greater than the number of elements in the list";

        /* This method implements the Fisher–Yates shuffle. */
        int end = list.size() - amount;

        for (int i = list.size() - 1; i >= end; i--){
            int j = generator.nextInt(i + 1);
            Collections.swap(list, i, j);
        }

        List<T> subList = list.subList(end, list.size());

        return new HashSet<T>(subList);
    }
}
