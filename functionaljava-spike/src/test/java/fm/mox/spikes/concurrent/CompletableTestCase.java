package fm.mox.spikes.concurrent;

import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
public class CompletableTestCase {

    // number of threads used in executor
    static final int NOTHREADS = 3;

    // time of each task
    static final int HEATWATER = 1000;
    static final int GRINDBEANS = 1000;
    static final int FROTHMILK = 1000;
    static final int BREWING = 1000;
    static final int COMBINE = 1000;

    // method to simulate work (pause current thread without throwing checked exception)
    public static void pause(long t) {
        try {
            Thread.sleep(t);
        } catch (Exception e) {
            throw new Error(e.toString());
        }
    }

    // task to heat some water
    static class HeatWater implements Supplier<Void> {

        private final Map<String, String> aMap;

        public HeatWater(Map<String, String> aMap) {
            this.aMap = aMap;
        }

        @Override
        public Void get() {
            System.out.println("Heating Water");
            pause(HEATWATER);
            aMap.put("water", "hot water");
//            return "hot water";
            return null;
        }
    }

    // task to grind some beans
    static class GrindBeans implements Supplier<Void> {

        private final Map<String, String> aMap;

        public GrindBeans(Map<String, String> aMap) {
            this.aMap = aMap;
        }

        @Override
        public Void get() {
            System.out.println("Grinding Beans");
            pause(GRINDBEANS);
//            return "grinded beans";
            this.aMap.put("beans", "grinded beans");

            return null;
        }
    }

    // task to froth some milk
    static class FrothMilk implements Runnable {

        private final Map<String, String> aMap;

        public FrothMilk(Map<String, String> aMap) {
            this.aMap = aMap;
        }

        @Override
        public void run() {
            System.out.println("Frothing some milk");
            pause(FROTHMILK);
            this.aMap.put("milk", "some milk");
//            return null;
        }
    }

    // task to brew some coffee
    static class Brew implements BiFunction<Void, Void, Void> {

        private final Map<String, String> aMap;

        public Brew(Map<String, String> aMap) {
            this.aMap = aMap;
        }

        @Override
        public Void apply(Void g, Void h) {
            String groundBeans = this.aMap.get("beans");
            String heatedWater = this.aMap.get("water");
            System.out.println("Brewing coffee with " + groundBeans + " and " + heatedWater);
            pause(BREWING);
            this.aMap.put("brewedCoffee", "brewed coffee");
            return null;
        }
    }

    // task to combine brewed coffee and milk
    static class Combine implements BiFunction<Void, Void, Void> {

        private final Map<String, String> aMap;

        public Combine(Map<String, String> aMap) {
            this.aMap = aMap;
        }

        @Override
        public Void apply(Void s, Void s2) {
            String frothedMilk = this.aMap.get("milk");
            String brewedCoffee = this.aMap.get("brewedCoffee");
            System.out.println("Combining " + frothedMilk + " " + brewedCoffee);
            pause(COMBINE);
            this.aMap.put("coffee", "Final Coffee");
            return null;
        }
    }


    @Test
    public void test() {

        ExecutorService executor = Executors.newFixedThreadPool(NOTHREADS);

        long startTime = System.currentTimeMillis();

        try {
            // create all the tasks and let the executor handle the execution order
            Map<String, String> aMap = new HashMap<>();

            //A: single supply async. simply add more thenCombine()
            CompletableFuture<Void> frothMilk = CompletableFuture.runAsync(new FrothMilk(aMap), executor);
            CompletableFuture<Void> heatWaterFuture = CompletableFuture.supplyAsync(new HeatWater(aMap), executor);
            CompletableFuture<Void> grindBeans = CompletableFuture.supplyAsync(new GrindBeans(aMap), executor);

            //V: merge 2 dependencies
            CompletableFuture<Void> brew = heatWaterFuture.thenCombine(grindBeans, new Brew(aMap));
//            CompletableFuture<Void> coffee = brew.thenCombine(frothMilk, new Combine(aMap));

            //V: alternative to merge N dependencies and run another:
            Combine combine = new Combine(aMap);
            CompletableFuture<Void> coffee = CompletableFuture
                    .allOf(brew, frothMilk)
                    .thenApply(aVoid -> {
                        Void apply = combine.apply(null, null);
                        return null;
                    });

            // final coffee
            Void aVoid = coffee.get();
            System.out.println("Here is the coffee:" + aMap.get("coffee"));

            // analyzing times:
            System.out.println("\n\n");
            System.out.println("Actual time: \t\t\t\t" + (System.currentTimeMillis() - startTime) / 1000.0);

            // compute the quickest possible time:
            long path1 = Math.max(GRINDBEANS, HEATWATER) + BREWING + COMBINE;
            long path2 = FROTHMILK + COMBINE;
            System.out.println("Quickest time multi-threaded:\t\t" + Math.max(path1, path2) / 1000.0);

            // compute the longest possible time:
            long longestTime = HEATWATER + GRINDBEANS + FROTHMILK + BREWING + COMBINE;
            System.out.println("Quickest time single-threaded thread:\t" + longestTime / 1000.0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

    }
}

