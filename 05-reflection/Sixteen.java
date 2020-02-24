import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

public class Sixteen {
    public static HashSet<String> read_stop_words() throws IOException {
        if (Thread.currentThread().getStackTrace()[6].getMethodName() != "extract_words") return null;
        return Files.lines(Paths.get("../stop_words.txt")).map(l -> l.split(",")).flatMap(Arrays::stream).collect(Collectors.toCollection(HashSet::new));
    }
    public static Map<String, Integer> extract_words(String path) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Map<String, Integer> freMap = new HashMap<>();
        // call read_stop_words with reflection
        HashSet<String> stopSet = (HashSet<String>)Sixteen.class.getMethod("read_stop_words").invoke(null);
        Files.lines(Paths.get(path)).map(l -> l.toLowerCase().replaceAll("[^a-zA-Z]", " ").split(" ")).
                flatMap(Arrays::stream).filter(w -> w.length() > 1).filter(w -> !stopSet.contains((w))).forEach(w -> freMap.merge(w, 1, (a, b) -> a+b));
        return freMap;
    }
    public static void sortAndPrint(Map<String, Integer> freMap) {
        freMap.keySet().stream().sorted((a, b) -> freMap.get(b) - freMap.get(a)).limit(25).forEach(w -> System.out.printf("%s - %d\n", w, freMap.get(w)));
    }
    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, IOException {
        // call extract_words & sortAndPrint with reflection
        Map<String, Integer> m = (Map<String, Integer>)Sixteen.class.getMethod("extract_words", String.class).invoke(null, args[0]);
        Sixteen.class.getMethod("sortAndPrint", Map.class).invoke(null, m);
    }
}
