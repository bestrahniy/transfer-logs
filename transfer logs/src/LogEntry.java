import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * that is formated and record specific operation in file
 * - request balance
 * - transfer money (2 string: transferred and received)
 * - withdrew money
 * - final balance
 */
public class LogEntry{

    private static final LogEntry INSTANCE = new LogEntry();

    private LogEntry(){/* singleton */}

    /** @return only instance of the LogEntry class*/
    public static LogEntry getInstance(){
        return INSTANCE;
    }

    /**
     * gets the time at the moment
     * @return a string of the format "yyyy-MM-dd HH:mm:ss"
     */
    private String getNowTime(){
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(timeFormat);
    }

    /**
     * method are returning a line with time: if time == null takes the current time
     * else: method formats the received value
     *
     * @param time time or null
     * @return a string of the format "yyyy-MM-dd HH:mm:ss"
     */
    private String getTime(LocalDateTime time){
        if (time == null){
            return getNowTime();
        }else {
            return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }

    /**
     * creates a log of the form [time] user*** balance inquire amount of money
     *
     * @param user user object
     * @param balance sting with user balance
     * @param time time of the operation or null
     * @throws IOException error when entering or exiting a message
     */
    public void balanceInquire(User user, String balance, LocalDateTime time) throws IOException {
        String log = String.format("[%s] %s balance inquire %s", getTime(time), user.getId_user(), balance);
        FileUtils.appendLog(user.fileUserLogsPath(), log);
    }

    /**
     * creating a transfer log of the form [time] user*** transferred amount to user***
     * creating a receipted log of the form [time] user*** received amount from user***
     *
     * @param sender user object (sender)
     * @param recipient user object (recipient)
     * @param transferSum string with the transfer amount
     * @param time time of the operation or null
     * @throws IOException error when entering or exiting a message
     */
    public void transferred(User sender, User recipient, String transferSum, LocalDateTime time) throws IOException{
        String receivedLog = String.format("[%s] %s received %s from %s", getTime(time), recipient.getId_user(), transferSum, sender.getId_user());
        String senderLog = String.format("[%s] %s transferred %s to %s", getTime(time), sender.getId_user(), transferSum, recipient.getId_user());
        FileUtils.appendLog(sender.fileUserLogsPath(), senderLog);
        FileUtils.appendLog(recipient.fileUserLogsPath(), receivedLog);
    }

    /**
     * creating a withdrew log of the form [time] user*** withdrew amount
     *
     * @param user user object
     * @param withdrewSum string of withdrawal amount
     * @param time time of the operation or null
     * @throws IOException error when entering or exiting a message
     */
    public void withdrew(User user, String withdrewSum, LocalDateTime time) throws IOException{
        String log = String.format("[%s] %s withdrew %s", getTime(time), user.getId_user(), withdrewSum);
        FileUtils.appendLog(user.fileUserLogsPath(), log);
    }

    /**
     * creating a final log of the form [time] user*** final balance amount
     *
     * @param user user object
     * @param summa string with user balance
     * @throws IOException error when entering or exiting a message
     */
    public void finalBalanсe(User user, String summa) throws IOException {
        String log = String.format("[%s] %s final balance %s", getNowTime(), user.getId_user(), summa);
        FileUtils.appendLog(user.fileUserLogsPath(), log);
    }

}
