package bg.sofia.uni.fmi.mjt.torrent.client.commands;

import bg.sofia.uni.fmi.mjt.torrent.client.commands.enums.ClientCommandsKeywords;
import bg.sofia.uni.fmi.mjt.torrent.client.repositories.interfaces.SharedFilesRepository;
import bg.sofia.uni.fmi.mjt.torrent.models.SharedFile;

import java.io.PrintWriter;

/**
 * This command should be sent to server before closing the connection
 * it sends an unregister command with given username to unregister
 * all files that the user has shared.
 */
public class DisconnectClientCommand implements ClientCommand {
    static final String SUCCESSFULLY_DISCONNECTED_MESSAGE = "Successfully disconnected";
    private final String username;
    private final SharedFilesRepository sharedFiles;
    private final PrintWriter commandWriter;

    public DisconnectClientCommand(String username, SharedFilesRepository sharedFiles,
                                   PrintWriter commandWriter) {
        this.username = username;
        this.sharedFiles = sharedFiles;
        this.commandWriter = commandWriter;
    }

    /**
     * This should be the last command sent to the server.
     * Command sent to server should be in format
     * unregister <username> [<file>, <file>...] where the files are all
     * registered files by user
     * It also sends another command
     * unregister-user <username>
     * To remove him from the online users.
     * @return Message confirming that the disconnect has been successful
     */
    @Override
    public String execute() {
        StringBuilder command = new StringBuilder();
        command.append(ClientCommandsKeywords.UNREGISTER_FILES_COMMAND.getKeyword());
        command.append(' ');
        command.append(this.username);
        command.append(' ');
        for (String sharedFile : sharedFiles.getAvailableFiles()) {
            command.append(sharedFile);
        }
        this.commandWriter.println(command.toString().trim());
        String unregisterUserCommand = "unregister-user " + this.username;
        this.commandWriter.println(unregisterUserCommand);
        return SUCCESSFULLY_DISCONNECTED_MESSAGE;
    }
}
