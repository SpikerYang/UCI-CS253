
import java.io.IOException;

import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


class Eight {
    static class Pair {
        String s;
        int f;
        public Pair(String s, int f) {
            this.s = s;
            this.f = f;
        }
    }
    static final int K = 25;
    static final String FILENAME = "pride-and-prejudice.txt";

    public static void buildFreMap(String[] args, BiConsumer<Map<String, Integer>, BiConsumer> f) {
        // Store stop words
        Set<String> stopSet = new HashSet<>();
        Scanner stopScanner = null;
        try {
            stopScanner = new Scanner(Paths.get("../stop_words.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (stopScanner != null) {
            stopScanner.useDelimiter(",");
            while (stopScanner.hasNext()) {
                stopSet.add(stopScanner.next());
            }
            stopScanner.close();
        } else System.out.println("stopScanner is null!");
        //Store word frequency
        Map<String, Integer> freMap = new HashMap<>();
        Scanner textScanner = null;
        try {
            textScanner = new Scanner(Paths.get(args[0]));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (textScanner != null) {
            while (textScanner.hasNext()) {
                String word = textScanner.next(), nWord;
                int i = 0;
                while (i < word.length()) {
                    while (i < word.length() && !Character.isLetter(word.charAt(i))) i++;
                    if (i == word.length()) break;
                    nWord = "";
                    while (i < word.length() && Character.isLetter(word.charAt(i))) {
                        nWord += Character.toLowerCase(word.charAt(i));
                        i++;
                    }
                    if (!nWord.equals("") && nWord.length() > 1 && !stopSet.contains(nWord)) freMap.put(nWord, freMap.getOrDefault((nWord), 0) + 1);
                }
            }
            textScanner.close();
        } else System.out.println("textScanner is null!");
        f.accept(freMap, trans(Eight::buildList));
    }
    public static void buildPQ(Map<String, Integer> freMap, BiConsumer<PriorityQueue, BiConsumer> f) {
        PriorityQueue<Pair> pq = new PriorityQueue<>((a, b) -> a.f - b.f); // Sort by frequency
        for (String s : freMap.keySet()) {
            pq.offer(new Pair(s, freMap.get(s)));
            if (pq.size() > K) pq.poll();
        }
        f.accept(pq, trans(Eight::printAns));
    }
    public static void buildList(PriorityQueue<Pair> pq, BiConsumer<List<Pair>, Consumer<?>> f) {
        List<Pair> ans = new LinkedList<>();
        while (!pq.isEmpty()) ans.add(pq.poll());
        Collections.reverse(ans);
        f.accept(ans, Eight::nul);
    }
    public static void printAns(List<Pair> ans, Consumer<?> f) {
        for (Pair p : ans) {
            System.out.printf("%s  -  %d \n", p.s, p.f);
        }
        f.accept(null);
    }
    public static <T> void nul(T t) {
        return;
    }
    private static <T, B> BiConsumer<T, BiConsumer> trans(BiConsumer<T, B> f) {
        return (BiConsumer<T, BiConsumer>) f;
    }
    public static void main(String[] args) {
        Eight z = new Eight();
        buildFreMap(args, Eight::buildPQ);
//        z.printAns(z.buildList(z.buildPQ(z.buildFreMap(args))));
    }
}