import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TwentySix {

    private static class Column<V> {
        private V data;
        private Supplier<V> source;
        public Column(Supplier<V> source) {
            this.source = source;
        }
        private V getData() {
            return data;
        }
        private void refresh() {
            data = source.get();
        }
    }
    private static class Sheet {
        private static String path;
        private static List<Column<?>> list;
        private static Column<Set<String>> stopSet = new Column<>(() -> {
            try {
                return Files.lines(Paths.get("../stop_words.txt")).map(l -> l.split(",")).flatMap(Arrays::stream).collect(Collectors.toCollection(HashSet::new));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
        private static Column<Map<String, Integer>> freMap = new Column<>(() -> {
            Map<String, Integer> freMap = new HashMap<>();
            try {
                Files.lines(Paths.get(path)).map(l -> l.toLowerCase().replaceAll("[^a-zA-Z]", " ").split(" ")).
                        flatMap(Arrays::stream).filter(w -> w.length() > 1).filter(w -> !stopSet.getData().contains((w))).forEach(w -> freMap.merge(w, 1, (a, b) -> a+b));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return freMap;
        });
        public Sheet(String s) {
            path = s;
            list = new ArrayList<>();
            list.add(stopSet);
            list.add(freMap);
        }
        private void update() {
            for (Column c : list) {
                c.refresh();
            }
        }
    }
    public static void main(String[] args) {
        Sheet sheet = new Sheet(args[0]);
        sheet.update();
        Map<String, Integer> freMap = sheet.freMap.getData();
        freMap.keySet().stream().sorted((a, b) -> freMap.get(b) - freMap.get(a)).limit(25).forEach(w -> System.out.printf("%s - %d\n", w, freMap.get(w)));
    }
}
