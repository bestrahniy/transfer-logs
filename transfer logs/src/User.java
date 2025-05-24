import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * model of user
 *  - stores user id
 *  - stores user balance
 *  - stores flag, which stores whether the user's balance has been set
 *  - path of his logs file
 */
public class User {

    private static int userCounter = 1;

    private double balance;

    private String id_user;

    private boolean balanceInitialized = false;

    private static final Path PROJECT_ROOT = Paths.get(System.getProperty("user.dir"));

    private static final Path LOGS_DIR = PROJECT_ROOT.resolve("transactions_by_users");

    static {
        try {
            Files.createDirectories(LOGS_DIR);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать папку для логов: " + LOGS_DIR, e);
        }
    }

    /** if user is not specified it will create his */
    public User(){
        this.id_user = generateId();
    }

    /** the function is set id_user*/
    public User(String id_user){
        this.id_user = id_user;
    }

    /**
     * if user is not specified it will create id user
     *
     * @return string id user of the format user***
     */
    private String generateId(){
        return String.format("user%03d", userCounter++);
    }

    /**
     *
     * @return path of specified user file
     */
    public Path fileUserLogsPath(){
        return  LOGS_DIR.resolve(id_user + ".log");
    }

    /** *  create a specified file for user */
    public void createLogsFile(){
        Path userFile = fileUserLogsPath();
        try{
            if(Files.notExists(userFile)) {
                Files.createFile(userFile);
                System.out.println("Создан файл для пользователя: " + id_user);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * the function updates balance depending on the type operation
     *
     * @param operationType type of operation
     * @param summ operation amount
     * @return updated balance
     */
    public double setOrUpdateBalance(OperationType operationType, double summ){
        switch (operationType){
            case BALANCE_INQUIRY:
                balance = summ;
                balanceInitialized = true;
                break;

            case TRANSFERRED, WITHDREW:
                if(balance >= summ){
                    balance -= summ;
                }else {
                    break;
                }
                break;

            case RECEIVE:
                balance += summ;
                break;

            default:
                throw new IllegalArgumentException("Unknown operation: " + operationType);
        }

        return balance;
    }

    /**
     * @return string user id
     */
    public String getId_user(){
        return id_user;
    }

    /**
     * @return line with string balance if the flag (balanceInitialized) is true otherwise 0
     */
    public String checkBalance(){
        if (balanceInitialized) {
            return String.valueOf(balance);
        }else {
            return new String("0");
        }
    }
}
