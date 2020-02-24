import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

public class getFreMap implements BiFunction<Set, String, Map> {
    @Override
    public Map apply(Set stopSet, String s) {
        Map<String, Integer> freMap = new HashMap<>();
        try {
            Files.lines(Paths.get(s)).map(l -> l.toLowerCase().replaceAll("[^a-zA-Z]", " ").split(" ")).
                    flatMap(Arrays::stream).filter(w -> w.length() > 1).filter(w -> !stopSet.contains((w))).forEach(w -> freMap.merge(w, 1, (a, b) -> a+b));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return freMap;
    }
}
