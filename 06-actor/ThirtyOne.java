

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class ThirtyOne {
    public static List<Pair> split_words(String path) {
        List<Pair> list = new LinkedList<>();
        try {
            HashSet<String> stopSet = Files.lines(Paths.get("../stop_words.txt")).map(l -> l.split(",")).flatMap(Arrays::stream).collect(Collectors.toCollection(HashSet::new));// Store stop words
            Files.lines(Paths.get(path)).map(l -> l.toLowerCase().replaceAll("[^a-zA-Z]", " ").split(" ")).
                    flatMap(Arrays::stream).filter(w -> w.length() > 1).filter(w -> !stopSet.contains((w))).forEach(l -> list.add(new Pair(l, 1)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
    public static Map<String, List<Pair>> regroup(List<Pair> list) {
        Map<String, List<Pair>> map = new HashMap<>();
        for (Pair p : list) {
            if (!map.containsKey(p.s)) map.put(p.s, new LinkedList<>());
            map.get(p.s).add(p);
        }
        return map;
    }
    public static void count_words(Map<String, List<Pair>> map) {
        Map<String, Integer> freMap = new HashMap<>();
        for (String s : map.keySet()) {
            int sum = 0;
            for (Pair p : map.get(s)) {
                sum += p.v;
            }
            freMap.put(s, sum);
        }
        freMap.keySet().stream().sorted((a, b) -> freMap.get(b) - freMap.get(a)).limit(25).forEach(w -> System.out.printf("%s - %d\n", w, freMap.get(w)));
    }
    public static void main(String[] args) {
        count_words(regroup(split_words(args[0])));
    }
}
class Pair {
    String s;
    int v;
    public Pair(String s, int v) {
        this.s = s;
        this.v =v;
    }
}
