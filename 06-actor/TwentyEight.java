import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class TwentyEight {
    public static void main(String[] args) throws IOException {
        WordFreManager wfm = new WordFreManager();
        DataStorageManager dsm = new DataStorageManager(args[0], wfm);
        StopWordManager swm = new StopWordManager(dsm);
        swm.queue.offer(new Message("start", null));
    }
}
class Message {
    String key;
    Object value;
    public Message(String k, Object v) {
        key = k;
        value = v;
    }
}
class ActiveWFObject implements Runnable {
    String name;
    ConcurrentLinkedQueue<Message> queue = new ConcurrentLinkedQueue<>();
    boolean _stop;

    public void dispatch(Message m) {
        if (m.key.equals("die")) this._stop = true;
    }

    @Override
    public void run() {
        while(!_stop) {
            if (!queue.isEmpty()) {
                Message m = queue.poll();
                dispatch(m);
            } else {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void send(ActiveWFObject receiver, Message m) {
        receiver.queue.offer(m);
    }
}

class StopWordManager extends ActiveWFObject {
    HashSet<String> stopSet;
    DataStorageManager dsm;
    public StopWordManager (DataStorageManager dsm) throws IOException {
        this.dsm = dsm;
        stopSet = Files.lines(Paths.get("../stop_words.txt")).map(l -> l.split(",")).flatMap(Arrays::stream).
                collect(Collectors.toCollection(HashSet::new));
        (new Thread(this)).start();
    }

    @Override
    public void dispatch(Message m) {
        super.dispatch(m);
        if (m.key.equals("start")) {
            send(dsm, new Message("gotStopSet", stopSet)); // send stopSet to DataStorageManager
            send(this, new Message("die", null)); // kill thread
        }
    }
}
class DataStorageManager extends ActiveWFObject {
    String path;
    Map<String, Integer> freMap = new HashMap<>();
    WordFreManager wfm;

    public DataStorageManager (String args, WordFreManager wfm) {
        this.wfm = wfm;
        path = args;
        (new Thread(this)).start();
    }

    @Override
    public void dispatch(Message m)  {
        super.dispatch(m);
        if (m.key.equals("gotStopSet")) {
            HashSet stopSet = (HashSet)m.value;
            try {
                Files.lines(Paths.get(path)).map(l -> l.toLowerCase().replaceAll("[^a-zA-Z]", " ").split(" ")).flatMap(Arrays::stream).
                        filter(w -> w.length() > 1).filter(w -> !stopSet.contains((w))).forEach(w -> freMap.merge(w, 1, (a, b) -> a+b));
            } catch (IOException e) {
                e.printStackTrace();
            }
            send(wfm, new Message("gotFreMap", freMap));
            send(this, new Message("die", null));
        }
    }
}
class WordFreManager extends  ActiveWFObject {
    public WordFreManager() {
        (new Thread(this)).start();
    }

    @Override
    public void dispatch(Message m) {
        super.dispatch(m);
        if (m.key.equals("gotFreMap")) {
            HashMap<String, Integer> freMap = (HashMap<String, Integer>)m.value;
            freMap.keySet().stream().sorted((a, b) -> freMap.get(b) - freMap.get(a)).limit(25).forEach(w -> System.out.printf("%s - %d\n", w, freMap.get(w)));
            send(this, new Message("die", null));
        }
    }
}
