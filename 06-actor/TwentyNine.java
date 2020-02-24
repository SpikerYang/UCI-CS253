import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class TwentyNine {
    public static ConcurrentLinkedQueue<String> word_space = new ConcurrentLinkedQueue<>();
    public static ConcurrentLinkedQueue<ConcurrentHashMap<String, Integer>> fre_space = new ConcurrentLinkedQueue<>();
    public static ConcurrentHashMap<String, Integer> freMap = new ConcurrentHashMap<>();

    public static Set<String> stopSet;

    public TwentyNine() {
        try {
            stopSet = Files.lines(Paths.get("../stop_words.txt")).map(l -> l.split(",")).flatMap(Arrays::stream).collect(Collectors.toCollection(HashSet::new));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void InitWordSpace(String path) {
        try {
            Files.lines(Paths.get(path)).map(l -> l.toLowerCase().replaceAll("[^a-zA-Z]", " ").split(" ")).
                    flatMap(Arrays::stream).filter(w -> w.length() > 1).filter(w -> !stopSet.contains((w))).forEach(l -> word_space.offer(l));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void print() {
        freMap.keySet().stream().sorted((a, b) -> freMap.get(b) - freMap.get(a)).limit(25).forEach(w -> System.out.printf("%s - %d\n", w, freMap.get(w)));
    }
    static class Thread1 implements Runnable {
        @Override
        public void run() {
            ConcurrentHashMap<String, Integer> m = new ConcurrentHashMap<>();
            while (!word_space.isEmpty()) {
                String word = word_space.poll();
                m.put(word, m.getOrDefault(word, 0) + 1);
            }
            fre_space.offer(m);
        }
    }
    static class Thread2 implements Runnable {
        @Override
        public void run() {
            ConcurrentHashMap<String, Integer> m;
            while (!fre_space.isEmpty()) {
                m = fre_space.poll();
                for (Map.Entry<String, Integer> e : m.entrySet()) {
                    freMap.put(e.getKey(), freMap.getOrDefault(e.getKey(), 0) + m.get(e.getKey()));
                    m.remove(e.getKey());
                }
            }
        }
    }

    public static void main(String[] args) {
        TwentyNine tn = new TwentyNine();
        tn.InitWordSpace(args[0]);
        Thread[] t1 = new Thread[5];
        Thread[] t2 = new Thread[5];
        for (int i = 0; i < 5; ++i) {
            t1[i] = new Thread(new Thread1());
            t2[i] = new Thread(new Thread2());
        }
        for (Thread t : t1) {
            t.start();
        }
        for (Thread t : t1) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (Thread t : t2) {
            t.start();
        }
        for (Thread t : t2) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        tn.print();
    }
}

