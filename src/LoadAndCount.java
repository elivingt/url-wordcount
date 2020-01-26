import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LoadAndCount implements Runnable {
    private String url;
    private ConcurrentHashMap<String, Long> counterMap;
    private Set<String> vocabulary;

    public LoadAndCount(String url, ConcurrentHashMap<String, Long> counterMap, Set<String> vocabulary) {
        this.url = url;
        this.counterMap = counterMap;
        this.vocabulary = vocabulary;
    }

    private static List<String> loadUrl(String url) throws Exception {
        URL file = new URL(url);
        try {
            System.out.printf("loading %s\n",url);
            return EngadgetAddresses.loadUrlContent(file);
        } catch (Exception e) {
            System.out.printf("\nfailed reading from %s\n",url);
            return new LinkedList<>();
        }
    }

    @Override
    public void run() {
        try {
            List<String> lines = loadUrl(this.url);
            for (String line : lines) {
                String[] words = line.split(" ");
                for (String word : words) {
                    if (this.vocabulary.contains(word)) {
                        counterMap.compute(word, (k,v) -> (v == null) ? 1 : v + 1 );
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
