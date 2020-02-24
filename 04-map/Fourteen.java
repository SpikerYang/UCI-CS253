import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Fourteen {
    /**
     * WordFrequencyFramework implemented inversion of control(CI)
     * Providing interfaces to register Runnable and Consumer element
     */
    static class WordFrequencyFramework {
        private List<Consumer> loadEventHandlers = new ArrayList<>();
        private List<Runnable> doworkEventHandlers = new ArrayList<>();
        private List<Runnable> endEventHandlers = new ArrayList<>();

        public void register_for_load_event(Consumer<String> handler) {
            loadEventHandlers.add(handler);
        }
        public void register_for_dowork_event(Runnable handler) {
            doworkEventHandlers.add(handler);
        }
        public void register_for_end_event(Runnable handler) {
            endEventHandlers.add(handler);
        }

        public void run(String path_to_file) {
            for (Consumer h : loadEventHandlers) h.accept(path_to_file);
            for (Runnable h : doworkEventHandlers) h.run();
            for (Runnable h : endEventHandlers) h.run();
        }
    }
    static class DataStorage {
        Stream<String> data;
        StopWordFilter stopWordFilter;
        List<Consumer> wordEventHandlers = new ArrayList<>();

        Consumer<String> load = (p) -> {
            try {
                data = Files.lines(Paths.get(p)).map(l -> l.toLowerCase().replaceAll("[^a-zA-Z]", " ").split(" "))
                        .flatMap(Arrays::stream).filter(w -> w.length() > 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        Runnable produce_words = () -> {
            for (String w : data.collect(Collectors.toCollection(ArrayList::new))) {
                if (!stopWordFilter.is_stop_words(w)) {
                    for (Consumer h : wordEventHandlers) {
                        h.accept(w);
                    }
                }
            }
        };

        public DataStorage (WordFrequencyFramework wfapp, StopWordFilter swf) {
            stopWordFilter = swf;
            wfapp.register_for_load_event(load);
            wfapp.register_for_dowork_event(produce_words);
        }
        public void register_for_word_event(Consumer handler) {
            wordEventHandlers.add(handler);
        }

    }
    static class StopWordFilter {
        ArrayList<String> stop_words = new ArrayList<>();

        Consumer<String> load = (p) -> {
            try {
                stop_words = Files.lines(Paths.get("../stop_words.txt")).map(l -> l.split(",")).
                        flatMap(Arrays::stream).collect(Collectors.toCollection(ArrayList::new));
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        public StopWordFilter (WordFrequencyFramework wfapp) {
            wfapp.register_for_load_event(load);
        }
        public boolean is_stop_words(String word) {
            return stop_words.contains(word);
        }
    }
    static class WordFrequencyCounter {
        Map<String, Integer> word_freqs = new HashMap<>();

        Consumer<String> increment_count = (word) -> {
            word_freqs.put(word, word_freqs.getOrDefault(word, 0) + 1);
        };

        Runnable print_freqs = () -> {
            System.out.println("Top 25 Frequent Words:");
            word_freqs.keySet().stream().sorted((a, b) -> word_freqs.get(b) - word_freqs.get(a)).limit(25).
                    forEach(w -> System.out.printf("%s - %d\n", w, word_freqs.get(w)));
        };
        public WordFrequencyCounter (WordFrequencyFramework wfapp, DataStorage dataStorage) {
            dataStorage.register_for_word_event(increment_count);
            wfapp.register_for_end_event(print_freqs);
        }
    }

    /**
     *  Added FrequencyCounter to count words containing z
     */
    static class ZFrequencyCounter {
        Map<String, Integer> word_freqs = new HashMap<>();

        Consumer<String> increment_count = (word) -> {
            if (word.contains("z")) word_freqs.put(word, word_freqs.getOrDefault(word, 0) + 1);
        };

        Runnable print_freqs = () -> {
            System.out.printf("\n\n\n");
            System.out.println("Words with z:");
            word_freqs.keySet().stream().sorted((a, b) -> word_freqs.get(b) - word_freqs.get(a)).
                    forEach(w -> System.out.printf("%s - %d\n", w, word_freqs.get(w)));
        };
        public ZFrequencyCounter (WordFrequencyFramework wfapp, DataStorage dataStorage) {
            dataStorage.register_for_word_event(increment_count);
            wfapp.register_for_end_event(print_freqs);
        }
    }

    public static void main(String[] args) {
        WordFrequencyFramework wfapp = new WordFrequencyFramework();
        StopWordFilter stop_word_filter = new StopWordFilter(wfapp);
        DataStorage data_storage = new DataStorage(wfapp, stop_word_filter);
        WordFrequencyCounter word_frequency_counter = new WordFrequencyCounter(wfapp, data_storage);
        ZFrequencyCounter z_frequency_counter = new ZFrequencyCounter(wfapp, data_storage);
        wfapp.run(args[0]);
    }
}

