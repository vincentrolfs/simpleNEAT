package simpleNEAT;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class RandomUtilTest {
    @RepeatedTest(10)
    void randomDoublesAreDistributedEvenly() {
        int amountSmall = 0;

        for (int i = 0; i < 10000; i++) {
            double weight = RandomUtil.getRandomDouble(-0.5, 3);
            if (weight <= 1.25){
                amountSmall++;
            }
        }

        // 99,7%-confidence interval
        assertEquals(5000, amountSmall, 150);
    }

    @Test
    void randomDoublesAreInRange() {
        for (int i = 0; i < 10000; i++) {
            double weight = RandomUtil.getRandomDouble(-1.23, 12.3);
            assertTrue(-1.23 <= weight && weight <= 12.3);
        }
    }

    @RepeatedTest(10)
    void randomBooleansOccurWithCorrectProbability() {
        int amountTrue = 0;

        for (int i = 0; i < 10000; i++) {
            boolean decision = RandomUtil.getRandomBoolean(0.379);
            if (decision){
                amountTrue++;
            }
        }

        // 99,7%-confidence interval
        assertEquals(3790, amountTrue, 146);
    }

    @Test
    void sampleFromReturnsNullIfListEmpty() {
        List<String> list = new ArrayList<>();
        assertNull(RandomUtil.sampleFrom(list));
    }

    @Test
    void sampleFromDistributesEvenly() {
        List<String> list = new ArrayList<>();
        list.add("b");
        list.add("a");
        list.add("c");
        list.add("b");
        list.add("a");
        list.add("a");
        list.add("c");
        list.add("a");

        int amountA = 0;

        for (int i = 0; i < 10000; i++) {
            String value = RandomUtil.sampleFrom(list);
            if (value.equals("a")){
                amountA++;
            }
        }

        // 99,7%-confidence interval
        assertEquals(5000, amountA, 150);
    }

    @Test
    void sampleMultipleFromDistributesEvenly2() {
        List<Integer> list = new ArrayList<>();
        Map<Set<Integer>, Integer> resultCounts = new HashMap<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);

        for (int i = 0; i < 10000; i++) {
            Set<Integer> result = RandomUtil.sampleMultipleFrom(list, 2);

            if (resultCounts.containsKey(result)){
                resultCounts.put(result, resultCounts.get(result) + 1);
            } else {
                resultCounts.put(result, 1);
            }
        }

        assertEquals(15, resultCounts.size());
        double expectedCount = 10000.0 / 15.0;
        double chiSquared = 0;

        for (Set<Integer> result : resultCounts.keySet()) {
            int count = resultCounts.get(result);
            chiSquared += Math.pow(count - expectedCount, 2) / expectedCount;
        }

        // 99,5% confidence
        assertTrue(chiSquared <= 31.32, "chiSquared was too big: " + chiSquared);
    }
}