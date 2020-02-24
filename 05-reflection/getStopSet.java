import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class getStopSet implements Supplier<Set<String>> {
    @Override
    public Set get() {
        try {
            return Files.lines(Paths.get("../stop_words.txt")).map(l -> l.split(",")).flatMap(Arrays::stream).collect(Collectors.toCollection(HashSet::new));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
