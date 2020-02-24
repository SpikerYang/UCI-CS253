import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.*;


class Solution {
    static final int K = 25;
    static final String FILENAME = "pride-and-prejudice.txt";

    public static void main(String[] args) {
        String sText = args[0], sStop = "../stop_words.txt"; // Set fileName
        Scanner textScanner = null, stopScanner = null;
        try {
            textScanner = new Scanner(Paths.get(sText));
            stopScanner = new Scanner(Paths.get(sStop));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Set<String> stopSet = new HashSet<>(); // Store stop words
        Map<String, Integer> freMap = new HashMap<>(); //Store word frequency
        PriorityQueue<String> pq = new PriorityQueue<>((a, b) -> freMap.get(a) - freMap.get(b)); // Sort by frequency

        if (stopScanner != null) {
            stopScanner.useDelimiter(",");
            while (stopScanner.hasNext()) {
                stopSet.add(stopScanner.next());
            }
            stopScanner.close();
        } else System.out.println("stopScanner is null!");


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


        for (String s : freMap.keySet()) {
            pq.offer(s);
            if (pq.size() > K) pq.poll();
        }

        List<String> ans = new LinkedList<>();
        while (!pq.isEmpty()) ans.add(pq.poll());
        Collections.reverse(ans);

        try {
            PrintWriter writer = new PrintWriter(new FileWriter(FILENAME));
            for (String s : ans) {
                writer.printf("%s  -  %d \n", s, freMap.get(s));
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}