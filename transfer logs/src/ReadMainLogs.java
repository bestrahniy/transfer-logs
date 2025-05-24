import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

/**
 * read and parse logs from file to hasMap
 * and sorts them by time
 */
public class ReadMainLogs {

    private static final ReadMainLogs INSTANCE = new ReadMainLogs();

    private ReadMainLogs(){/* singleton */}

    /** @return only instance of the ReadMainLogs class */
    public static ReadMainLogs getInstance(){
        return INSTANCE;
    }

    private Path PROJECT_ROOT = Paths.get(System.getProperty("user.dir")).resolve("transfer logs");

    private Path LOGS_DIR = PROJECT_ROOT.resolve("transactions_by_users");

    private List<String> lines = new ArrayList<>();

    /**
     * parse all logs from any file and record them to file
     *
     * @throws IOException error in reading
     */
    public void parseAll() throws IOException {
         try (Stream<Path> filePath = Files.walk(LOGS_DIR)){
             filePath.filter(Files::isRegularFile)
                     .filter(file -> file.getFileName().toString().startsWith("log"))
                     .flatMap(file -> {
                         try {
                             return Files.lines(file);
                         }catch (IOException e) {
                             return Stream.empty();
                         }
                     })
                     .forEach(line -> lines.add(line));
         }
    }

    /**
     * Parses a single log line into its components
     *
     * @param line string with one log
     * @return HashMap which contains time, sender (maybe and recipient), type of operation, amount of operation
     */
    public HashMap<String, String> parseOneLog(String line){
        HashMap<String,String> parts = new HashMap<>();

        // Extract timestamp between '[' and ']'
        String time = line.substring(1, line.indexOf(']'));
        parts.put("time", time);

        // Remove the timestamp prefix
        String rest = line.substring(line.indexOf("]") + 2);

        // Sender is the first token
        int firstSpace = rest.indexOf(' ');
        String user = rest.substring(0, firstSpace);
        parts.put("sender", user);

        // Remainder contains operation + amount [+ " to recipient"]
        String remainder = rest.substring(firstSpace + 1);

        if (remainder.contains(" to ")) {
            // Case: transferred X to Y
            String[] splitTo = remainder.split(" to ", 2);
            String left = splitTo[0];
            String recipient = splitTo[1];
            parts.put("recipient", recipient);

            // left format: "transferred amount"
            String[] tok = left.split(" ", 2);
            parts.put("operation", tok[0]);
            parts.put("summ",    tok[1]);

        } else {
            // Case: balance inquiry or withdrew
            int lastSpace = remainder.lastIndexOf(' ');
            String operation = remainder.substring(0, lastSpace);
            String amount    = remainder.substring(lastSpace + 1);
            parts.put("operation", operation);
            parts.put("summ",    amount);
        }

        return parts;
    }

    /**
     * Processes each entry via LogManager
     * Writes final balances
     * @throws IOException error when entering or exiting a message
     */
    public void manager() throws IOException{
        parseAll();
        // Sort lines by their timestamp
        lines.sort(Comparator.comparing(
                line -> LocalDateTime.parse(
                        line.substring(1, line.indexOf("]")),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        ));

        for(String log : lines){
            HashMap<String, String> partsLog = parseOneLog(log);
            LogManager.getInstance().process(partsLog);
        }

        LogManager.getInstance().writeFinalBalance();
    }

}
