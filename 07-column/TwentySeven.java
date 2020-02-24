import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class TwentySeven {
    public static void main(String[] args) throws IOException {
        Map<String, Integer> freMap = new HashMap<>(); //Store word frequency
        HashSet<String> stopSet = Files.lines(Paths.get(args[1])).map(l -> l.split(",")).flatMap(Arrays::stream).collect(Collectors.toCollection(HashSet::new));// Store stop words
        Files.lines(Paths.get(args[0])).map(l -> l.toLowerCase().replaceAll("[^a-zA-Z]", " ").split(" ")).flatMap(Arrays::stream).filter(w -> w.length() > 1).filter(w -> !stopSet.contains((w))).forEach(w -> freMap.merge(w, 1, (a, b) -> a+b)); //merge fre;
        freMap.keySet().stream().sorted((a, b) -> freMap.get(b) - freMap.get(a)).limit(25).forEach(w -> System.out.printf("%s - %d\n", w, freMap.get(w)));
    }
}
