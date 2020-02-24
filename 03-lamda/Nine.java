import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;


class Nine {
    static final int K = 25;
    static final String FILENAME = "pride-and-prejudice.txt";

    public static Set<String> buildStopSet(String sStop) {
        Set<String> stopSet = new HashSet<>(); // Store stop words
        Scanner stopScanner = null;
        try {
            stopScanner = new Scanner(Paths.get(sStop));
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
        return stopSet;
    }
    public static Map<String, Integer> buildFreMap(String args) {
        Set<String> stopSet = buildStopSet("../stop_words.txt");
        Map<String, Integer> freMap = new HashMap<>(); //Store word frequency
        Scanner textScanner = null;
        try {
            textScanner = new Scanner(Paths.get(args));
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
        return freMap;
    }
    public static PriorityQueue<Pair> buildPQ(Map<String, Integer> freMap) {
        PriorityQueue<Pair> pq = new PriorityQueue<>((a, b) -> a.f - b.f); // Sort by frequency
        for (String s : freMap.keySet()) {
            pq.offer(new Pair(s, freMap.get(s)));
            if (pq.size() > K) pq.poll();
        }
        return pq;
    }
    public static List<Pair> buildList(PriorityQueue<Pair> pq) {
        List<Pair> ans = new LinkedList<>();
        while (!pq.isEmpty()) ans.add(pq.poll());
        Collections.reverse(ans);
        return ans;
    }
    public void printAns(List<Pair> ans) {
        for (Pair p : ans) {
            System.out.printf("%s  -  %d \n", p.s, p.f);
        }
    }

    public static void main(String[] args) {
        TFTheOne t = new TFTheOne(args[0]);
        t.bind(Nine::buildFreMap).bind(Nine::buildPQ).bind(Nine::buildList).print();
    }
}

class Pair {
    String s;
    int f;
    public Pair(String s, int f) {
        this.s = s;
        this.f = f;
    }
}
class TFTheOne {
    Object object;
    public TFTheOne(String s) {
        object = s;
    }
    public<T, R> TFTheOne bind(Function<T, R> f) {
        object = f.apply((T)object);
        return this;
    }
    public void print() {
        for (Pair p : (List<Pair>)object) {
            System.out.printf("%s  -  %d \n", p.s, p.f);
        }
    }
}