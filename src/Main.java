
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {


    private static final int N_THREADS = 20;

    public static void main(String[] args) throws Exception {
        // load vocabulary and urls from file
        Set<String> vocabulary = EnDictionary.load();
        List<String> urls = EngadgetAddresses.load();
        ConcurrentHashMap<String, Long> counterMap = new ConcurrentHashMap<>();
        long startTime = System.currentTimeMillis();

        doIt(urls, counterMap, vocabulary);
        long time = System.currentTimeMillis() - startTime;
        System.out.println("entries:" + counterMap.size() + " time took sec " + time / 1000 );
    }


    private static void doIt(List<String> urls, ConcurrentHashMap<String, Long> counterMap,
                             Set<String> vocabulary) {
        ExecutorService loaderPool = Executors.newFixedThreadPool(N_THREADS);
        for (String url : urls) {
            loaderPool.execute(new LoadAndCount(url, counterMap, vocabulary));
        }
        loaderPool.shutdown();

        try {
            if (!loaderPool.awaitTermination(30, TimeUnit.MINUTES)) {
                loaderPool.shutdownNow();
                System.out.println("terminated after timeout");
            }
        } catch (InterruptedException ex) {
            loaderPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
