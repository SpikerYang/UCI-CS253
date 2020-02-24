import java.util.Map;
import java.util.function.Consumer;

public class sortAndPrint implements Consumer<Map<String, Integer>> {
    @Override
    public void accept(Map<String, Integer> freMap) {
        freMap.keySet().stream().sorted((a, b) -> freMap.get(b) - freMap.get(a)).limit(25).forEach(w -> System.out.printf("%s - %d\n", w, freMap.get(w)));
    }
}
