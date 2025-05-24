import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;

/**
 * This is utils file for record log in file
 * It is using APPEND operation for safe creation and recording
 */
public class FileUtils {

    /**
     * record one log in file
     * if file not exists function will create it
     * @param path file path
     * @param log log string that has already been created
     * @throws IOException error when entering or exiting a message
     */
    public static void appendLog(Path path, String log) throws IOException {
        if (Files.notExists(path)){
            Files.createFile(path);
        }

        Files.write(path,
                Collections.singletonList(log),
                StandardOpenOption.APPEND);
    }

}
