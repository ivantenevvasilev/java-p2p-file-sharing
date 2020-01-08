package bg.sofia.uni.fmi.mjt.torrent.client.commands;

import java.io.IOException;

/**
 * Represents an action that the Client can execute
 * all the logic is encapsulated in the commands
 * and each command states in the constructor
 * what it requires.
 */
public interface ClientCommand {
    /**
     *
     * @return Response from the execution of the command
     * it is usually the server response, possibly modified
     * by the command itself
     * @throws IOException when an IO error occurs during transfer from client
     * to server or vice versa. Should be handled in a lower level of abstraction
     */
    String execute() throws IOException;
}
