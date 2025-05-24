import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/**
 * Coordinates the analysis of the common records
 * - create / find user object
 * - update balance
 * - calls methods of the LogEntry class for record
 * - record final balance to the end of each file
 */
public class LogManager {

    private static final LogManager INSTANCE = new LogManager();

    private LogManager(){/* singleton*/}

    /** @return only instance of the LogManager class*/
    public static LogManager getInstance(){
        return INSTANCE;
    }

    private final HashMap<String, User> users = new HashMap<>();

    /**
     * record and create a new user in HashMap users
     *
     * @param id_user string of the user id
     * @return new or existing user
     */
    private User getOrCreate(String id_user){
        return users.computeIfAbsent(id_user, id -> {
            try{
                User user = new User(id);
                user.createLogsFile();
                return user;
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * process one log which has already parsed
     *
     * @param partsLog map with key: time, sender, operation, summ, [, rceipient]
     * @throws IOException error when entering or exiting a message
     */
    public void process(HashMap<String, String> partsLog) throws IOException {

        String id_sender = partsLog.get("sender");
        String operation = partsLog.get("operation");
        String summa = partsLog.get("summ");

        LocalDateTime ts = LocalDateTime.parse(
                partsLog.get("time"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        );

        User sender = getOrCreate(id_sender);

        OperationType op = switch (operation.toLowerCase()) {
            case "balance inquiry" -> OperationType.BALANCE_INQUIRY;
            case "transferred" -> OperationType.TRANSFERRED;
            case "withdrew" -> OperationType.WITHDREW;
            default -> throw new IllegalArgumentException("Unknown op: " + operation);
        };

        if (op == OperationType.BALANCE_INQUIRY){
            LogEntry.getInstance().balanceInquire(sender, summa, ts);
            sender.setOrUpdateBalance(OperationType.BALANCE_INQUIRY, Double.parseDouble(summa));
            return;
        }

        if (op == OperationType.TRANSFERRED){
            String id_recipient = partsLog.get("recipient");
            User recipient = getOrCreate(id_recipient);
            LogEntry.getInstance().transferred(sender, recipient, summa, ts);
            sender.setOrUpdateBalance(OperationType.TRANSFERRED, Double.parseDouble(summa));
            recipient.setOrUpdateBalance(OperationType.RECEIVE, Double.parseDouble(summa));

        }else if(op == OperationType.WITHDREW){
            LogEntry.getInstance().withdrew(sender, summa, ts);
            sender.setOrUpdateBalance(OperationType.WITHDREW, Double.parseDouble(summa));
        }
    }

    /**
     * record a final balance for a user who has initialized balance
     *
     * @throws IOException error when entering or exiting a message
     */
    public void writeFinalBalance() throws IOException {
        for (String key : users.keySet()){
            User user = users.get(key);
            LogEntry.getInstance().finalBalanсe(user, user.checkBalance());
        }
    }
}
