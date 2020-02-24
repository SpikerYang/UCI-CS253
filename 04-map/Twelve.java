import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Twelve {
    /**
     * Define Pair to store words and their frequencies
     */
    static class Pair {
        String s;
        int f;
        public Pair(String s, int f) {
            this.s = s;
            this.f = f;
        }
    }
    /**
     * Define a MyMap to meet requirement in 12.3 of no 'THIS'
     * @param <K> Type of keys in MyMap
     * @param <V> Type of values in MyMap
     */
    static class MyMap<K, V> extends HashMap<K, V> {
        public final MyMap<K, V> me = this;

    }

    static final Map<String, Object> data_storage_obj = new MyMap<String, Object>() {
        {
            me.put("data", new String[0]);
            me.put("init", (Consumer<String>) (p) -> {
                Stream<String> lines = null;
                try {
                    lines = Files.lines(Paths.get(p));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String[] words = lines.map(l -> l.toLowerCase().replaceAll("[^a-zA-Z]", " ")).
                        collect(Collectors.joining(" ")).split("\\s+");
                me.put("data", words);
            });
            me.put("words", (Supplier<String[]>)() -> (String[]) me.get("data"));
        }
    };

    static final Map<String, Object> stop_words_obj = new MyMap<String, Object>() {
        {
            me.put("stop_words", new HashSet<String>());
            me.put("init", (Runnable)() -> {
                HashSet<String> stopSet = null;
                try {
                    stopSet = Files.lines(Paths.get("../stop_words.txt")).map(l -> l.split(",")).
                            flatMap(Arrays::stream).collect(Collectors.toCollection(HashSet::new));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                me.put("stop_words", stopSet);
            });
            me.put("is_stop_word", (Function<String, Boolean>) word -> word.length() <= 1 || ((HashSet<String>)me.get("stop_words")).contains(word));
        }
    };

    static final Map<String, Object> word_freqs_obj = new MyMap<String, Object>() {
        {
            me.put("freqs", new HashMap<String, Integer>());
            me.put("increment_count", (Consumer<String>) word -> ((HashMap<String, Integer>)me.get("freqs")).merge(word, 1, (a, b) -> a+b));
            me.put("sorted", (Supplier<List<Pair>>) () -> ((HashMap<String, Integer>)me.get("freqs")).entrySet().stream().map(e -> new Pair(e.getKey(), e.getValue())).
                    sorted((a, b) -> b.f - a.f).collect(Collectors.toCollection(LinkedList::new)));

        }
    };

    public static void main(String[] args) {
        ((Consumer<String>)data_storage_obj.get("init")).accept(args[0]);
        ((Runnable) stop_words_obj.get("init")).run();
        for (String word : ((Supplier<String[]>) data_storage_obj.get("words")).get()) {
            if ( !((Function<String, Boolean>) stop_words_obj.get("is_stop_word")).apply(word)) {
                ((Consumer<String>) word_freqs_obj.get("increment_count")).accept(word);
            }
        }
        word_freqs_obj.put("top25", (Consumer<Map<String, Object>>) (me) -> {
            for (Pair p : ((Supplier<List<Pair>>) me.get("sorted")).get().subList(0, 25) ) {
                System.out.printf("%s - %d\n", p.s, p.f);
            }
        });
        ((Consumer<Map<String, Object>>)word_freqs_obj.get("top25")).accept(word_freqs_obj);
    }
}
