package bg.sofia.uni.fmi.mjt.torrent.client.commands;

import bg.sofia.uni.fmi.mjt.torrent.client.commands.enums.ClientCommandsKeywords;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class RegisterUserClientCommand implements ClientCommand {
    private final PrintWriter writer;
    private final BufferedReader reader;
    private String username;
    private int serverPort;

    public RegisterUserClientCommand(PrintWriter writer,
                                     BufferedReader reader,
                                     String username,
                                     int serverPort) {
        this.writer = writer;
        this.reader = reader;
        this.username = username;
        this.serverPort = serverPort;
    }

    @Override
    public String execute() throws IOException {
        this.writer.println(ClientCommandsKeywords.REGISTER_USER_COMMAND.getKeyword() +
                ' ' + this.username + ' ' + this.serverPort);
        return this.reader.readLine();
    }
}
