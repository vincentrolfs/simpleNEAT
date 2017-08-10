package simpleNEAT;

import java.util.Random;

class RandomUtil {

    static final Random generator = new Random();

    /**
     * Generates a random double in the specified range. Is not overflow-safe!
     */
    static double getRandomDouble(double min, double max){
        return generator.nextDouble() * (max - min) + min;
    }

}
