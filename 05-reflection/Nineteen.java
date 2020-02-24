import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Nineteen {
    private static Supplier<Set<String>> GET_STOP_WORDS;
    private static BiFunction<Set, String, Map> GET_FRE_MAP;
    private static Consumer<Map<String, Integer>> SORT_AND_PRINT;

    public static void load() throws Exception {
        Properties config = new Properties();
        try {
            config.load(new BufferedReader(new FileReader("config.properties")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        URL path = null;
        try {
            path = Paths.get(config.getProperty("plugPath")).toUri().toURL();
            System.out.println(path);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        ClassLoader classLoader = new URLClassLoader(new URL[] { path }, Nineteen.class.getClassLoader());
        Class<?> getStopSet = null;
        try {
            getStopSet = classLoader.loadClass(config.getProperty("getStopSet"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Class<?> getFreMap = classLoader.loadClass(config.getProperty("getFreMap"));
        Class<?> sortAndPrint = classLoader.loadClass(config.getProperty("sortAndPrint"));
        GET_STOP_WORDS = (Supplier<Set<String>>)getStopSet.newInstance();
        GET_FRE_MAP = (BiFunction<Set, String, Map>)getFreMap.newInstance();
        SORT_AND_PRINT = (Consumer<Map<String, Integer>>)sortAndPrint.newInstance();
    }
    public static void main(String[] args) throws Exception {
        load();
        SORT_AND_PRINT.accept(GET_FRE_MAP.apply(GET_STOP_WORDS.get(), args[0]));
    }
}
